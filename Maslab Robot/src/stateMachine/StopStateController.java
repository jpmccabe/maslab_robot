package stateMachine;

import org.opencv.core.Mat;

public class StopStateController extends StateMachine {
    
    
    public StopStateController(){

    }
    
    @Override
    public void stop() {
        
    }

    @Override
    public void controlState(Mat image) {
        
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.STOPPED;
    }

    @Override
    public boolean isDone() {
        return true;
    }

}
