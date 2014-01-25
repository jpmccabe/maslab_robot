package stateMachine;

import robotModel.*;

public class AvoidWallStateController extends StateMachine {

    private final Devices robotModel;
    
    public AvoidWallStateController(Devices robotModel){
        this.robotModel = robotModel;
    }
    
    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void start() {
        double turnSpeed = 0.1;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.AVOID_WALLS;
    }
    
}
