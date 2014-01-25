package stateMachine;

import robotModel.*;
import stateMachine.*;
import camera.*;

public class BallCollectionStateController extends StateMachine {
    
    private BallCollectionState collectionState;
    private final Devices robotModel;
    private final Camera camera;
    private boolean done;
    
    public BallCollectionStateController(Devices robotModel, Camera camera){
        collectionState = BallCollectionState.APPROACH;
        this.robotModel = robotModel;
        this.camera = camera;
        done = false;
    }
    
    
    public void control(){
        
    }
    
    public void approach(){
        
    }
    
    public void collect(){
        
    }
    
    public void lift(){
        
    }
    
    public void setState(BallCollectionState state){
        collectionState = state;
    }


    @Override
    public void stop() {
        // clean up the state
        // TODO make sure balls make it all the way through the spiral and are sorted
        robotModel.allMotorsOff();
        done = true;
    }


    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.COLLECT_GROUND_BALLS;
    }


    @Override
    public boolean isDone() {
        return done;
    }
}
