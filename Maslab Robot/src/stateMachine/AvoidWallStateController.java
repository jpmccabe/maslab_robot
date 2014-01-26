package stateMachine;

import robotModel.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    private boolean done;
    
    public AvoidWallStateController(Devices robotModel){
        this.robotModel = robotModel;
        done = false;
    }
    
    private void avoidWall(){
        double turnSpeed = 0.1;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(500);
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
