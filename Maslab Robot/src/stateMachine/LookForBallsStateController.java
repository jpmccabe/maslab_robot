package stateMachine;

import org.opencv.core.Mat;

import robotModel.Devices;

public class LookForBallsStateController extends StateMachine {

    private final Devices robotModel;
    private volatile boolean isDone;
    private long startTime;
    
    public LookForBallsStateController(Devices robotModel){
        this.robotModel = robotModel;
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0, 0);
        isDone = true;
    }
    
    
    private void forward(){
        double forwardSpeed = 0.19;
        robotModel.setMotors(forwardSpeed, forwardSpeed);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    private void reverseAndSpin(){
    	double reverseSpeed = -0.18;
        double turnSpeed = 0.17;
        long reverseTime = 900;
        long turnTime = 1000;
        
        try {
            robotModel.setMotors(reverseSpeed, reverseSpeed);
			Thread.sleep(reverseTime);
			robotModel.setMotors(turnSpeed, -1*turnSpeed);
	        Thread.sleep(turnTime);
	        robotModel.setMotors(0,0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    

    @Override
    public void controlState(Mat image) {
    	long timeout = 10000;
    	long currentRunTime = System.currentTimeMillis() - startTime;
    	
    	if(currentRunTime >= timeout){
    		reverseAndSpin();
    		stop();
    	}
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
