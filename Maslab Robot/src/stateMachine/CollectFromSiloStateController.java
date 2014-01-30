package stateMachine;

import org.opencv.core.Mat;

import robotModel.*;

public class CollectFromSiloStateController extends StateMachine {
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private final long startTime;
    private boolean done = false;
    
    public CollectFromSiloStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        startTime = System.currentTimeMillis();
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    @Override
    public void controlState(Mat image) {
        final long timeout = 20000;
        
        long currentRunningTime = System.currentTimeMillis() - startTime;
        
        // timeout
        if(currentRunningTime >= timeout){
            stop();
        }
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.COLLECT_FROM_ENERGY_SILO;
    }

    @Override
    public boolean isDone() {
        return done;
    }

}
