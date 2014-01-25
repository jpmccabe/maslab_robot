package stateMachine;

import robotModel.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    private boolean done;
    
    public AvoidWallStateController(Devices robotModel){
        this.robotModel = robotModel;
        done = false;
    }
    
    @Override
    public void stop() {
        robotModel.allMotorsOff();
        done = true;
    }

    @Override
    public void start() {
        double turnSpeed = 0.1;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        done = true;
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
