package ballCollectionStateMachine;

import robotModel.*;
import camera.*;

public class BallCollectionStateController {
    
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
}
