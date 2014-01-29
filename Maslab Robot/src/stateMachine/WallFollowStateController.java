package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;

public class WallFollowStateController extends StateMachine {
    private final Devices robotModel;
    private boolean done = false;
    
    public WallFollowStateController(Devices robotModel){
        this.robotModel = robotModel;
    }

    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    @Override
    public void controlState(Mat image) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.WALL_FOLLOW;
    }

    @Override
    public boolean isDone() {
        return done;
    }
    
    
}
