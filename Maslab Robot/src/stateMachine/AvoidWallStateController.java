package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;
import camera.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    private final ComputerVisionSummary obstacleSummary;
    private volatile boolean done;
    private final ObstacleDirection spinDirection;
    
    public AvoidWallStateController(Devices robotModel, ObstacleDirection spinDirection){
        this.robotModel = robotModel;
        obstacleSummary = new ComputerVisionSummary();
        done = false;
        this.spinDirection = spinDirection;
    }
    
    private void avoidWall(){
        double turnSpeed = 0.16;
        turnSpeed = (spinDirection == ObstacleDirection.RIGHT) ? -1*turnSpeed : turnSpeed;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
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
        obstacleSummary.updateObstacleSummary(image);
        if(obstacleSummary.getObstacle() != ObstacleDirection.NONE){
            avoidWall();
        }
        else{
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
