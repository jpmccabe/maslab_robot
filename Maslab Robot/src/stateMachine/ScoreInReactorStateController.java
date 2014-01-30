package stateMachine;

import java.util.List;

import org.opencv.core.Mat;

import robotModel.*;
import camera.ComputerVisionSummary;
import driving.Driver;

public class ScoreInReactorStateController extends StateMachine {

    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private volatile boolean done;
    private final ComputerVisionSummary reactorSummary;
    private ScoreInReactorStates state = ScoreInReactorStates.NONE;
    private final Driver driver;
    private int centerOfScreen = 320;
    private final long startTime;
    
    public ScoreInReactorStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        this.reactorSummary = new ComputerVisionSummary();
        driver = new Driver();
        startTime = System.currentTimeMillis();
        done = false;
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0, 0);
        done = true;
    }
    
    private void centerRobot(int centerX){
        System.out.println("CenterX:"+centerX);
        System.out.println("adjust to center");
        final double minSpeed = 0.13;
        final double maxSpeed = 0.25;
        double prop = (centerX-centerOfScreen)*0.0009;
        if(Math.abs(prop) < minSpeed){
            prop = prop >= 0 ? minSpeed : -minSpeed;
        }
        if(Math.abs(prop) > maxSpeed){
            prop = prop >= 0 ? maxSpeed: -maxSpeed;
        }
        System.out.println("Center Speed L: " + prop + " R: " +(-prop));
        robotModel.setMotors(prop,-prop);
    }
    
    private void manhattan(double angleToTurnDegrees, double centerDistance){
        final double turnSpeed = 0.25;
        final double forwardSpeed = 0.2;
        final double turnProportionalTimeConstant = 8;
        final double forwardProportionalTimeConstant = 137;
        final double ninetyDegreeTurnTime = 800;
        final double driveDistance = Math.cos(Math.toRadians(Math.abs(angleToTurnDegrees))) * centerDistance;
        final int driveDirection = angleToTurnDegrees >= 0 ? 1 : -1; // 1 is right, -1 is left
       
        try {
            // turn
            robotModel.setMotors(driveDirection*turnSpeed, -1*driveDirection*turnSpeed);
            Thread.sleep((long)(turnProportionalTimeConstant*Math.abs(angleToTurnDegrees)));
            // drive forward
            robotModel.setMotors(forwardSpeed,forwardSpeed);
            Thread.sleep((long)(forwardProportionalTimeConstant*driveDistance));
            // turn 90
            robotModel.setMotors(-1*driveDirection*turnSpeed, driveDirection*turnSpeed);
            Thread.sleep((long) ninetyDegreeTurnTime);
            robotModel.setMotors(0, 0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void straight(){
        System.out.println("Go Straight");
        robotModel.setMotors(0.2,0.2);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void depositTop(){
        robotModel.setMotors(0,0);
        final int numGreenBalls = robotInventory.getNumGreenBalls();
        final int numToScoreInTop = numGreenBalls > 1 ? numGreenBalls-1 : numGreenBalls;
        for(int i = 0; i < numToScoreInTop; i++){
            System.out.println("Depositing ball in top");
            try {
                robotModel.setServoReleaseToGreenPosition();
                Thread.sleep(800);
                robotModel.setServoReleaseToScoreUpperPosition();
                Thread.sleep(800);
                robotInventory.removeGreenBalls(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done depositing balls in top");
    }
    
    private void depositBottom(){
        robotModel.setMotors(0,0);
        final int numToScoreInBottom = robotInventory.getNumGreenBalls();
        for(int i = 0; i < numToScoreInBottom; i++){
            System.out.println("Depositing in bottom");
            try{
                robotModel.setServoReleaseToGreenPosition();
                Thread.sleep(800);
                robotModel.setServoReleaseToScoreLowerPosition();
                Thread.sleep(800);
                robotInventory.removeGreenBalls(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("Done depositing ball in bottom");
    }
    
    private void reverse(){
        final double reverseSpeed = -0.2;
        robotModel.setMotors(reverseSpeed,reverseSpeed);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
      
    private void reverseToDistance(){
        final double reverseSpeed = -0.2;
        robotModel.setMotors(reverseSpeed, reverseSpeed);
    }
    

    @Override
    public void controlState(Mat image) {
        final long timeout = 20000;
        final double depositLowerPortDistance = 12;
        final double misAlignmentDistance = 6;
        final double misAlignmentAngle = 45;
        final double insertDistance = 6;
        final int centerXThreshold = 20;

        reactorSummary.updateReactorSummary(image);
        
        double angle=reactorSummary.getReactorAngleInDegrees();
        double distance=reactorSummary.getReactorCenterDistance();
        int centerX=reactorSummary.getReactorCenterXValue();
        long runningTime = System.currentTimeMillis() - startTime;
        // System.out.println("Angle:"+angle);
        //System.out.println("Distance:"+distance);
        
        // exit if we time out
        if(runningTime >= timeout){
            stop();
        }
        // exit if we no longer see reactor
        if(!reactorSummary.isReactorScoreable()){
            stop();
        }   
        // exit if we are about to deposit but mis-aligned
        if(distance <= misAlignmentDistance && angle >= misAlignmentAngle){
            reverse();
            stop();
        }
        // when close to reactor insert the end of the robot into the reactor by driving straight
        if( distance < insertDistance && robotInventory.hasGreenBalls() && !(state ==ScoreInReactorStates.INSERT)){
            state = ScoreInReactorStates.INSERT;
            robotModel.setMotors(0,0);
            straight();
        }
        // switch to deposit in top state after insert state, then switch to reverse to distance state
        else if(state == ScoreInReactorStates.INSERT){
            state = ScoreInReactorStates.DEPOSIT_TOP;
            depositTop();
            state = ScoreInReactorStates.REVERSE_TO_DISTANCE;
        }
        // reverse to distance for scoring in lower port
        else if(state == ScoreInReactorStates.REVERSE_TO_DISTANCE){
            reverseToDistance();
        }
        // switch to lower deposit state after reverse to distance and exit state
        else if(state == ScoreInReactorStates.REVERSE_TO_DISTANCE && distance >= depositLowerPortDistance ){
            state = ScoreInReactorStates.DEPOSIT_BOTTOM;
            depositBottom();
            stop();
        }
        // switch to driver state after manhattan state
        else if(state == ScoreInReactorStates.MANHATTAN  || state == ScoreInReactorStates.DRIVER ){
            List<Double> motorSpeeds = driver.driveToReactor(distance, insertDistance, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = ScoreInReactorStates.DRIVER;
        } 
        // switch to center state if not in a state and we need to center
        else if(Math.abs(centerX-centerOfScreen) > centerXThreshold  && (state == ScoreInReactorStates.NONE ||
                state == ScoreInReactorStates.CENTER)) {
            state = ScoreInReactorStates.CENTER;
            centerRobot(centerX);
        }
        // switch to manhattan state once centered
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && (state == ScoreInReactorStates.CENTER ||
                state == ScoreInReactorStates.NONE)){
        	System.out.println("Manhattan");
            state = ScoreInReactorStates.MANHATTAN;
            robotModel.setMotors(0,0);
            manhattan(reactorSummary.getReactorAngleToTurn(), distance);
        }        
        
        
        
        
        
        
        
        
        
        /*
        if(!reactorSummary.isReactorScoreable()){
            stop();
        }
        else if(!robotInventory.hasGreenBalls()){
            reverse();
            stop();
        }
        // center reactor in camera view if too far off
        else if(Math.abs(centerX-centerOfScreen) > 20 && distance > 6 && state == ScoreInReactorStates.NONE) {
            centerRobot(centerX);
        }
        
        // drive straight towards the reactor if the angle is small
        else if(Math.abs(centerX-centerOfScreen) <= 20 && distance >= 6){
            List<Double> motorSpeeds = driver.driveToReactor(distance, 6, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = ScoreInReactorStates.STRAIGHT;
        }
        
        else if( distance < 6 && robotInventory.hasGreenBalls() && !(state ==ScoreInReactorStates.INSERT)){
            state = ScoreInReactorStates.INSERT;
            straight();
        }
        else if(robotInventory.hasGreenBalls() && (state == ScoreInReactorStates.INSERT ||
                state == ScoreInReactorStates.DEPOSIT)){
            deposit();
        }
        */
       
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.SCORE_IN_REACTOR;
    }

    @Override
    public boolean isDone() {
        return done;
    }

}
