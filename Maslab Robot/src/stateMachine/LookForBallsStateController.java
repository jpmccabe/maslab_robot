package stateMachine;

import org.opencv.core.Mat;

import robotModel.Devices;

public class LookForBallsStateController extends StateMachine {

    private final Devices robotModel;
    private volatile boolean isDone;
    
    public LookForBallsStateController(Devices robotModel){
        this.robotModel = robotModel;
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0, 0);
        isDone = true;
    }
    
    
    private void forward(){
        double forwardSpeed = 0.17;
        robotModel.setMotors(forwardSpeed, forwardSpeed);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void controlState(Mat image) {
        forward();
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.LOOK_FOR_BALLS;
    }

    @Override
     public boolean isDone() {
        return isDone;
    }

}
