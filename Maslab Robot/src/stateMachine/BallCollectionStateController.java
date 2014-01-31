package stateMachine;

import java.util.List;

import org.opencv.core.Mat;

import robotModel.*;
import driving.*;
import camera.*;

public class BallCollectionStateController extends StateMachine {
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private volatile boolean done;
    private final ComputerVisionSummary ballSummary;
    private final long startTime;
    
    public BallCollectionStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        this.ballSummary = new ComputerVisionSummary();
        startTime = System.currentTimeMillis();
        done = false;
    }
    
       
    private void approach(double distance, double angle){
        final Driver driver = new Driver();
        final List<Double> motorSpeeds = driver.driveToBall(distance,0,angle,0);
        robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
    }
    
    
    private void backUpAndTurn(){
        final double reverseSpeed = -0.18;
        final double turnSpeed = 0.18;
        final long turnTime = 1000;
        final long reverseTime = 800;
        
        try {
            robotModel.setMotors(reverseSpeed, reverseSpeed);
            Thread.sleep(reverseTime);
            robotModel.setMotors(turnSpeed, -turnSpeed);
            Thread.sleep(turnTime);
            robotModel.setMotors(0,0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    
    private void collect(BallColor ballColor){        
    	System.out.println("Collect Ball");
        final double forwardSpeed = 0.17;
        robotModel.setMotors(forwardSpeed, forwardSpeed);
        robotModel.setRoller(true);
        robotInventory.addBallToQueue(new TimedBall(System.currentTimeMillis(), ballColor));
        
        Thread rollerThread = new Thread(new Runnable(){
            public void run(){
                try {
                    Thread.sleep(3000);
                    robotModel.setRoller(false);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        Thread spiralThread = new Thread(new Runnable(){
            public void run(){
                try {
                    robotModel.setSpiral(true);
                    Thread.sleep(10000);
                    robotModel.setRoller(false);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        
        rollerThread.start();
        spiralThread.start();
        
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        robotModel.setMotors(0, 0);
        
        //robotModel.setMotors(-forwardSpeed,-forwardSpeed);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    

    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    

    @Override
    public void controlState(Mat image) {
        final long timeout = 8000;
        final double collectAngleMax = 90;
        final double collectDistanceMax = 6;
        
        ballSummary.updateBallSummary(image);
        long currentRunningTime = System.currentTimeMillis() - startTime;
        
        // if we time out then backup and rotate, then exit state.
        if(currentRunningTime >= timeout){
            backUpAndTurn();
            stop();
        }
        
        // green balls are given priority over red
        if(ballSummary.isGreenBall()){
            double greenBallAngle = ballSummary.getAngleToGreenBall();
            double greenBallDistance = ballSummary.getDistanceToGreenBall();
            
            if(Math.abs(greenBallAngle) <= collectAngleMax && greenBallDistance <= collectDistanceMax){
            	System.out.println("collecting green ball");
                collect(BallColor.GREEN);
                stop();
            } else{
                approach(greenBallDistance, greenBallAngle);
            }
            
        } else if(ballSummary.isRedBall()){
            double redBallAngle = ballSummary.getAngleToRedBall();
            double redBallDistance = ballSummary.getDistanceToRedBall();
            
            if(Math.abs(redBallAngle) <= collectAngleMax && redBallDistance <= collectDistanceMax){
            	System.out.println("collecting red ball");
                collect(BallColor.RED);
                stop();
            } else{
                approach(redBallDistance, redBallAngle);
            }
            
        } else{
            stop();
        }
        
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
