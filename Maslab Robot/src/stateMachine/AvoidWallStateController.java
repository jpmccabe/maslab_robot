package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;
import camera.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    private final ComputerVisionSummary obstacleSummary;
    private volatile boolean done;
    private final ObstacleDirection spinDirection;
    private final long startTime;
    
    public AvoidWallStateController(Devices robotModel, ObstacleDirection spinDirection){
        this.robotModel = robotModel;
        obstacleSummary = new ComputerVisionSummary();
        done = false;
        this.spinDirection = spinDirection;
        this.startTime = System.currentTimeMillis();
    }
    
    private void avoidWall(){
        double turnSpeed = 0.20;
        turnSpeed = (spinDirection == ObstacleDirection.RIGHT) ? -1*turnSpeed : turnSpeed;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    private void backup(){
    	final double reverseSpeed = -0.18;
    	robotModel.setMotors(reverseSpeed, reverseSpeed);
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    @Override
    public void controlState(Mat image) {
    	final long timeout = 6000;
        obstacleSummary.updateObstacleSummary(image);
        System.out.println("bw center distance: " + obstacleSummary.getCenterDistanceToBlueWall());
        System.out.println("open area: " + obstacleSummary.noObstacle());
        
        long currentRunningTime = System.currentTimeMillis() - startTime;
        
        if(currentRunningTime >= timeout){
        	backup();
        	stop();
        }
        
        if(!obstacleSummary.noObstacle()){
        	System.out.println("obstacle");
            avoidWall();
        }
        else{
        	System.out.println("exit avoid wall");
            stop();
        }
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.AVOID_WALLS;
    }

    @Override
    public boolean isDone() {
        return done;
    }
    
}
