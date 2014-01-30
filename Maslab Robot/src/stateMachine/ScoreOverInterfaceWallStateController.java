package stateMachine;

import java.util.List;

import org.opencv.core.Mat;

import camera.*;
import driving.*;
import robotModel.*;

public class ScoreOverInterfaceWallStateController extends StateMachine {
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private final long startTime;
    private boolean done = false;
    private final ComputerVisionSummary interfaceWallSummary;
    private ScoreOverInterfaceWallStates state = ScoreOverInterfaceWallStates.NONE;
    private final int centerOfScreen = 348;
    private final Driver driver;
    
    public ScoreOverInterfaceWallStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        startTime = System.currentTimeMillis();
        interfaceWallSummary = new ComputerVisionSummary();
        driver = new Driver();
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }
    
    
    private void centerRobot(int centerX){
        System.out.println("CenterX interfaace wall: "+centerX);
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
        final double turnSpeed = 0.2;
        final double forwardSpeed = 0.18;
        final double turnProportionalTimeConstant = 10;
        final double forwardProportionalTimeConstant = 120;
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
        robotModel.setMotors(0.17,0.17);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    private void deposit(){
        robotModel.setMotors(0,0);
        final int numToScore = robotInventory.getNumRedBalls() + robotInventory.getNumUnknownBallsOnBoard();
        System.out.println("Depositing " + numToScore + " balls over wall.");
        for(int i = 0; i < numToScore; i++){
            try{
                robotModel.setServoReleaseToRedPosition();
                Thread.sleep(800);
                robotModel.setServoReleaseToScoreLowerPosition();
                Thread.sleep(800);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        robotInventory.removeRedBalls();
        robotInventory.removeUnknownBalls();
        System.out.println("Done depositing balls over wall.");
    }
    
    
    private void turnAwayFromInterfaceWall(){
        final double turnSpeed = 0.2;
        final long turnTime = 1300;
        
        robotModel.setMotors(turnSpeed, -turnSpeed);
        try {
            Thread.sleep(turnTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    private void reverse(){
        final double reverseSpeed = -0.17;
        robotModel.setMotors(reverseSpeed,reverseSpeed);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
      
    
    @Override
    public void controlState(Mat image) {
        final long timeout = 20000;
        final double misAlignmentDistance = 6;
        final double misAlignmentAngle = 45;
        final double insertDistance = 6;
        final int centerXThreshold = 20;
        final double goStraightAngleThreshold = 80;
       
        interfaceWallSummary.updateInterfaceWallSummary(image);
        
        double angle = interfaceWallSummary.getInterfaceWallAngleInDegrees();
        double distance = interfaceWallSummary.getInterfaceWallCenterDistance();
        double angleToTurn = interfaceWallSummary.getInterfaceWallAngleToTurn();
        int centerX = interfaceWallSummary.getInterfaceWallCenterXValue();
        long currentRunningTime = System.currentTimeMillis() - startTime; 

        // System.out.println("Angle: " + angle);
        System.out.println("Distance: " + distance);
        System.out.println("Angle to turn: " + angleToTurn);
        
        // exit if we time out
        if(currentRunningTime >= timeout){
            stop();
        }
        // exit if we no longer see reactor
        if(!interfaceWallSummary.isInterfaceWallScoreable() &&
        		!(state == ScoreOverInterfaceWallStates.DEPOSIT) &&
        		!(state == ScoreOverInterfaceWallStates.INSERT) &&
        		!(state == ScoreOverInterfaceWallStates.REVERSE)){
            stop();
        } 
        /*
        // exit if we are about to deposit but mis-aligned
        if(distance <= misAlignmentDistance && angle >= misAlignmentAngle){
            reverse();
            stop();
        }
        */
        // when close to reactor insert the end of the robot into the reactor by driving straight
        if(distance <= insertDistance && state == ScoreOverInterfaceWallStates.DRIVER){
            state = ScoreOverInterfaceWallStates.INSERT;
            robotModel.setMotors(0,0);
            straight();
            state = ScoreOverInterfaceWallStates.DEPOSIT;
        }
        // switch to deposit in top state after insert state, then switch to reverse to distance state
        else if(state == ScoreOverInterfaceWallStates.DEPOSIT){
            deposit();
            reverse();
            turnAwayFromInterfaceWall();
            stop();
        }
        // switch to center from driver if angle to turn becomes too small
        else if(state == ScoreOverInterfaceWallStates.SMALL_ANGLE && 
        		angleToTurn < goStraightAngleThreshold){
            state = ScoreOverInterfaceWallStates.CENTER;
        }
        // switch to driver state after manhattan state
        else if(state == ScoreOverInterfaceWallStates.MANHATTAN  || state == ScoreOverInterfaceWallStates.DRIVER || 
                state == ScoreOverInterfaceWallStates.SMALL_ANGLE ){
            List<Double> motorSpeeds = driver.driveToReactor(distance, insertDistance, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = (state == ScoreOverInterfaceWallStates.MANHATTAN) ? ScoreOverInterfaceWallStates.DRIVER : state ;
        } 
        // switch to center state if not in a state and we need to center
        else if(Math.abs(centerX-centerOfScreen) > centerXThreshold  && (state == ScoreOverInterfaceWallStates.NONE ||
                state == ScoreOverInterfaceWallStates.CENTER)) {
            state = ScoreOverInterfaceWallStates.CENTER;
            centerRobot(centerX);
        }
        // switch into driver and skip manhattan if almost lined up with interface wall
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && 
                angleToTurn >= goStraightAngleThreshold &&
                (state == ScoreOverInterfaceWallStates.CENTER || state == ScoreOverInterfaceWallStates.NONE)){
            state = ScoreOverInterfaceWallStates.SMALL_ANGLE;
        }
        // switch to manhattan state once centered
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && (state == ScoreOverInterfaceWallStates.CENTER ||
                state == ScoreOverInterfaceWallStates.NONE)){
            System.out.println("Manhattan");
            state = ScoreOverInterfaceWallStates.MANHATTAN;
            robotModel.setMotors(0,0);
            manhattan(angleToTurn, distance);
        }    
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.DEPOSIT_RED_BALLS;
    }

    @Override
    public boolean isDone() {
        return done;
    }

}
