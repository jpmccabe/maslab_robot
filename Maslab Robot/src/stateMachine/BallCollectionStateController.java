package stateMachine;

import java.util.List;

import robotModel.*;
import driving.*;
import camera.*;

public class BallCollectionStateController extends StateMachine {
    
    private final Devices robotModel;
    private final Camera camera;
    private boolean done;
    
    
    public BallCollectionStateController(Devices robotModel, Camera camera){
        this.robotModel = robotModel;
        this.camera = camera;
        done = false;
    }
    
    
    
    private void approach(double distance, double angle){
        final Driver driver = new Driver();
        final List<Double> motorSpeeds = driver.driveToBall(distance,0,angle,0);
        robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
    }
    
    
    
    private void collect(){
        final double forwardSpeed = 0.1;
        robotModel.setMotors(forwardSpeed, forwardSpeed);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        stop();
    }
    
    

    @Override
    synchronized public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }

    

    @Override
    synchronized public void controlState() {
        final double collectAngleMax = 20;
        final double collectDistanceMax = 6;
        
        final ComputerVisionSummary ballSummary = ComputerVisionSummary.produceBallSummary(
                                                  camera.getLastFrame());
        
        // green balls are given priority over red
        // TODO more can be added to change this simple strategy
        if(ballSummary.isGreenBall()){
            double greenBallAngle = ballSummary.getAngleToGreenBall();
            double greenBallDistance = ballSummary.getDistanceToGreenBall();
            
            if(Math.abs(greenBallAngle) <= collectAngleMax && greenBallDistance <= collectDistanceMax){
                collect();
            } else{
                approach(greenBallDistance, greenBallAngle);
            }
            
        } else if(ballSummary.isRedBall()){
            double redBallAngle = ballSummary.getAngleToRedBall();
            double redBallDistance = ballSummary.getDistanceToRedBall();
            
            if(Math.abs(redBallAngle) <= collectAngleMax && redBallDistance <= collectDistanceMax){
                collect();
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
    synchronized public boolean isDone() {
        return done;
    }
}
