package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;
import camera.ComputerVisionSummary;

public class ScoreInReactorStateController extends StateMachine {

    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private volatile boolean done;
    private final ComputerVisionSummary reactorSummary;
    
    
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

    @Override
    public void controlState(Mat image) {
        reactorSummary.updateReactorSummary(image);
        // TODO Auto-generated method stub
        int centerX=reactorSummary.getReactorCenterXValue();
        double angle=reactorSummary.getReactorAngleInDegrees();
        double distance=reactorSummary.getReactorCenterDistance();
        
        if(Math.abs(centerX-360)>75){
        	System.out.println("adjust to center");
        	robotModel.setMotors(0.175,-0.175);
        	System.out.println("center:"+centerX);
        }
        else if (distance>20 || Math.abs(angle)<10){
        	System.out.println("Go Straight");
        	robotModel.setMotors(0.18,0.18);
        }
        else if(distance<20 && Math.abs(angle)<10){
        	robotModel.setMotors(0,0);
        	System.out.println("Approach & Score");
        	try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else if(angle>10)
        {	
        	System.out.println("rotateMode");
        	
        	double sinAngle= Math.sin(angle*Math.PI/180.0);
        	int rotationDirection=1;
        	
        	if (angle>=0) rotationDirection=1;
        	if (angle<0) rotationDirection=-1;
        	System.out.println(angle);
        	if(Math.abs(angle)>15){
        		robotModel.setMotors(0.175*rotationDirection,-0.175*rotationDirection);
        		try {
        			Thread.sleep((long) (Math.abs((90-Math.abs(angle))*12)));
        		} catch (InterruptedException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	robotModel.setMotors(0.225,0.225);
        	try {
				Thread.sleep((long) (distance*sinAngle*150));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	robotModel.setMotors(0,0);
        	try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	
        }
        System.out.println("center yo:"+reactorSummary.getReactorCenterXValue());
        System.out.println(angle);
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
