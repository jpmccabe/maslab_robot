package ballCollectionStateMachine;

import robotModel.*;
import stateMachine.*;
import camera.*;

public class BallCollectionStateController extends StateMachine {
    
    private BallCollectionState collectionState;
    private final Devices robotModel;
    private final Camera camera;
    
    public BallCollectionStateController(Devices robotModel, Camera camera){
        collectionState = BallCollectionState.APPROACH;
        this.robotModel = robotModel;
        this.camera = camera;
    }
    
    
    public void control(){
        
    }
    
    public void approach(){
        
    }
    
    public void collect(){
        robotModel.setRoller(true);
        robotModel.setMotors(0.05, 0.05);
        Thread.sleep(2000);
        robotModel.setMotors(0,0);
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
    }


    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }
}
