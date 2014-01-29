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
        
        System.out.println("Angle:"+angle);
        
        if(Math.abs(centerX-360)>100){
        	System.out.println("CenterX:"+centerX);
        	System.out.println("adjust to center");
        	int rotationDirection=1;
        	if (centerX<360) rotationDirection=-1;
        	robotModel.setMotors(0.17*rotationDirection,-0.17*rotationDirection);
        }
        else if (Math.abs(angle)<=22){
        	System.out.println("Go Straight");
        	robotModel.setMotors(0.16,0.16);
        	if(reactorSummary.getReactorCenterDistance()<5){
        		System.out.print("GOAAAAAAAL");
        		try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        else if(Math.abs(angle)>22)
        {	
        	System.out.println("rotateMode");
        	
        	double sinAngle= Math.sin(angle*Math.PI/180.0);
        	int rotationDirection=1;
        	
        	if (angle>=0) rotationDirection=1;
        	if (angle<0) rotationDirection=-1;
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
				Thread.sleep(850);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	
        	
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
