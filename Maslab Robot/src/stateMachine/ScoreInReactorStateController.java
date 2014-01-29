package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;
import camera.ComputerVisionSummary;

public class ScoreInReactorStateController extends StateMachine {

    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private volatile boolean done;
    private final ComputerVisionSummary reactorSummary;
    private ScoreInReactorStates state = ScoreInReactorStates.NONE;
    
    
    public ScoreInReactorStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        this.reactorSummary = new ComputerVisionSummary();
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
        stop();
    }
    
    private void rotateAndDrive(double angle, double distance){
        System.out.println("rotateMode");
        
        double sinAngle= Math.sin(angle*Math.PI/180.0);
        int rotationDirection = (angle >= 0) ? 1 : -1;
        robotModel.setMotors(0.175*rotationDirection,-0.175*rotationDirection);
        try {
            Thread.sleep((long) (Math.abs((90-Math.abs(angle))*12)));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        robotModel.setMotors(0.225,0.225);
        try {
            Thread.sleep((long) (Math.abs(distance*sinAngle*150)));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        robotModel.setMotors(0,0);
        robotModel.setMotors(-0.175*rotationDirection,0.175*rotationDirection);
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private void align(int centerX){
    	int rotationDirection = (centerX >= 360) ? 1 : -1;
        robotModel.setMotors(-0.175*rotationDirection,0.175*rotationDirection);
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
        // center reactor in camera view if too far off
        if(Math.abs(centerX-360)>30 && distance>6) {
        	centerRobot(centerX);
        }
        
        // drive straight towards the reactor if the angle is small
        else if(Math.abs(angle)<=11 && distance >= 6 ){
        	straight();
        }
        
        else if( distance < 6 && robotInventory.hasGreenBalls()){
            robotModel.setMotors(0.16,0.16);
            try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            deposit();
        }
        
        
        else if(Math.abs(angle)>11){	
        	System.out.println("rotate and drive");
            rotateAndDrive(angle,distance);
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
