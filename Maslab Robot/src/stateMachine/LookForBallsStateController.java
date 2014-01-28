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
    synchronized public void stop() {
        //robotModel.setMotors(0, 0);
        isDone = false;
    }
    
    
    private void turn(){
        double turnSpeed = 0.175;
        System.out.println("hola");
        //robotModel.setMotors(turnSpeed, turnSpeed);
        try {
            Thread.sleep(100);
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
     public boolean isDone() {
        return isDone;
    }

}
