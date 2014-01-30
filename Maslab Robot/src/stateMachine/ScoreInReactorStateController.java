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
    
    public ScoreInReactorStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        this.reactorSummary = new ComputerVisionSummary();
        driver = new Driver();
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
        int rotationDirection=1;
        final double minSpeed = 0.13;
        final double maxSpeed = 0.25;
        if (centerX<centerOfScreen) rotationDirection=-1;
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void deposit(){
        robotModel.setMotors(0,0);
        System.out.println("Deposit balls in top");
        try {
            robotModel.setServoReleaseToGreenPosition();
            Thread.sleep(800);
            robotModel.setServoReleaseToScoreUpperPosition();
            Thread.sleep(800);
            robotInventory.removeGreenBalls(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done depositing balls in top");
    }
    
    private void reverse(){
        robotModel.setMotors(-0.2,-0.2);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void controlState(Mat image) {
        reactorSummary.updateReactorSummary(image);
        // TODO Auto-generated method stub
        
        double angle=reactorSummary.getReactorAngleInDegrees();
        double distance=reactorSummary.getReactorCenterDistance();
        int centerX=reactorSummary.getReactorCenterXValue();
       // System.out.println("Angle:"+angle);
        //System.out.println("Distance:"+distance);
  
        //if(!reactorSummary.isReactorScoreable()){
            //stop();
       // }
       
        /*
        else if(!robotInventory.hasGreenBalls()){
            reverse();
            stop();
        }
        */
        if( distance < 6 && true /*robotInventory.hasGreenBalls()*/ && !(state ==ScoreInReactorStates.INSERT)){
            state = ScoreInReactorStates.INSERT;
            robotModel.setMotors(0,0);
            //straight();
        }
        // drive towards the reactor if the angle is small using driver
        else if(state == ScoreInReactorStates.MANHATTAN  || state == ScoreInReactorStates.DRIVER ){
            List<Double> motorSpeeds = driver.driveToReactor(distance, 6, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = ScoreInReactorStates.DRIVER;
        }
        
 
        // center reactor in camera view if too far off
        else if(Math.abs(centerX-centerOfScreen) > 20 && distance > 6 && (state == ScoreInReactorStates.NONE ||
                state == ScoreInReactorStates.CENTER)) {
            state = ScoreInReactorStates.CENTER;
            centerRobot(centerX);
        }
        // once centered do manhattan
        else if(Math.abs(centerX-centerOfScreen) <= 20 && distance > 6 && (state == ScoreInReactorStates.CENTER ||
                state == ScoreInReactorStates.NONE)){
        	System.out.println("Manhattan");
            state = ScoreInReactorStates.MANHATTAN;
            robotModel.setMotors(0,0);
            manhattan(reactorSummary.getReactorAngleToTurn(), distance);
        }        
        
        else if(robotInventory.hasGreenBalls() && (state == ScoreInReactorStates.INSERT ||
                state == ScoreInReactorStates.DEPOSIT)){
            deposit();
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
