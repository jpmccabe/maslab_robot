package stateMachine;

import org.opencv.core.Mat;

import robotModel.Devices;

public class LookForBallsStateController extends StateMachine {

    private final Devices robotModel;
    private boolean isDone;
    
    public LookForBallsStateController(Devices robotModel){
        this.robotModel = robotModel;
    }
    
    @Override
    synchronized public void stop() {
        robotModel.setMotors(0, 0);
        isDone = false;
    }
    
    
    private void turn(){
        double turnSpeed = 0.14;
        robotModel.setMotors(turnSpeed, -1*turnSpeed);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    synchronized public void controlState(Mat image) {
        turn();
    }

    @Override
    synchronized public StateMachineType getStateMachineType() {
        return StateMachineType.LOOK_FOR_BALLS;
    }

    @Override
    synchronized public boolean isDone() {
        return isDone();
    }

}
