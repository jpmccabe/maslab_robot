package ballCollectionStateMachine;

import robotModel.*;

public class BallCollectionStateController {
    
    private BallCollectionState collectionState;
    private final Devices robotModel;
    
    public BallCollectionStateController(Devices robotModel){
        collectionState = BallCollectionState.APPROACH;
        this.robotModel = robotModel;
    }
    
    public void approach(){
        
    }
    
    public void collect(){
        
    }
    
    public void lift(){
        
    }
}
