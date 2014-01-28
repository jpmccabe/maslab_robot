package stateMachine;

import org.opencv.core.Mat;

import robotModel.Devices;
import camera.ComputerVisionSummary;

public class ScoreInReactorStateController extends StateMachine {

    
    private final Devices robotModel;
    private volatile boolean done;
    private final ComputerVisionSummary reactorSummary;
    
    
    public ScoreInReactorStateController(Devices robotModel){
        this.robotModel = robotModel;
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
