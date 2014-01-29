package stateMachine;
import robotModel.*;

public class SorterStateController {
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    
    public SorterStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
    }
}
