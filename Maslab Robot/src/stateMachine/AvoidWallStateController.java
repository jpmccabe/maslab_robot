package stateMachine;

import robotModel.*;
import camera.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    private final Camera camera;
    private boolean done;
    
    public AvoidWallStateController(Devices robotModel, Camera camera){
        this.robotModel = robotModel;
        this.camera = camera;
        done = false;
    }
    
    private void avoidWall(){
        final double turnSpeed = 0.1;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        stop();
    }
    
    @Override
    synchronized public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    @Override
    synchronized public void controlState() {
        avoidWall();
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.AVOID_WALLS;
    }

    @Override
    synchronized public boolean isDone() {
        return done;
    }
    
}
