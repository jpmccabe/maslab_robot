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
        if (centerX<360) rotationDirection=-1;
        robotModel.setMotors(0.17*rotationDirection,-0.17*rotationDirection);
    }
    
    private void straight(){
        System.out.println("Go Straight");
        robotModel.setMotors(0.16,0.16);
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
        reverse();
        
    }
    
    private void reverse(){
        robotModel.setMotors(-0.16,-0.16);
        try {
            Thread.sleep(3000);
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
        System.out.println("Angle:"+angle);
        System.out.println("Distance:"+distance);
        
        if(!robotInventory.hasGreenBalls()){
            reverse();
            stop();
        }
        // center reactor in camera view if too far off
        else if(Math.abs(centerX-360) > 30 && distance > 6) {
        	centerRobot(centerX);
        }
        
        // drive straight towards the reactor if the angle is small
        else if(Math.abs(centerX-360) <= 30  && distance >= 6 ){
        	List<Double> motorSpeeds = driver.driveToBall(distance, 0, centerX-360, 0);
        	robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
        }
        
        else if( distance < 6 && robotInventory.hasGreenBalls()){
            deposit();
        }
 
       
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
