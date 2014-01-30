package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;

public class ScoreOverInterfaceWallStateController extends StateMachine {
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    
    public ScoreOverInterfaceWallStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
    }
    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controlState(Mat image) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public StateMachineType getStateMachineType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDone() {
        // TODO Auto-generated method stub
        return false;
    }

}
