package stateMachine;

import org.opencv.core.Mat;

import camera.ComputerVisionSummary;
import robotModel.*;

public class WallFollowStateController extends StateMachine {
    private final Devices robotModel;
    private boolean done = false;
    private final ComputerVisionSummary wallSummary;
    
    public WallFollowStateController(Devices robotModel){
        this.robotModel = robotModel;
        wallSummary = new ComputerVisionSummary();
    }

    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    @Override
    public void controlState(Mat image) {
        wallSummary.updateObstacleSummary(image);
        
        //wallSummary.
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
