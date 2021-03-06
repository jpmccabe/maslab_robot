package stateMachine;

import java.util.List;

import org.opencv.core.Mat;

import robotModel.*;
import camera.ComputerVisionSummary;
import driving.Driver;

public class ScoreInReactorStateController extends StateMachine {

    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private volatile boolean done;
    private final ComputerVisionSummary reactorSummary;
    private ScoreInReactorStates state = ScoreInReactorStates.NONE;
    private final Driver driver;
    private int centerOfScreen = 348;
    private final long startTime;
    
    public ScoreInReactorStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        this.reactorSummary = new ComputerVisionSummary();
        driver = new Driver();
        startTime = System.currentTimeMillis();
        done = false;
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0, 0);
        done = true;
    }
    
    private void centerRobot(int centerX){
        System.out.println("CenterX:"+centerX);
        System.out.println("adjust to center");
        final double minSpeed = 0.14;
        //final double maxSpeed = 0.2;
        double prop = (centerX-centerOfScreen)*0.0008;
        //System.out.println("prop: " + prop);
        //if(Math.abs(prop) < minSpeed){
            prop = prop >= 0 ? minSpeed : -minSpeed;
        //}
       // if(Math.abs(prop) > maxSpeed){
       //     prop = prop >= 0 ? maxSpeed: -maxSpeed;
       // }
        System.out.println("Center Speed L: " + prop + " R: " +(-prop));
        robotModel.setMotors(prop,-prop);
    }
    
    private void manhattan(double angleToTurnDegrees, double centerDistance){
    	final double turnSpeed = 0.2;
        final double forwardSpeed = 0.18;
        final double turnProportionalTimeConstant = 9;
        final double forwardProportionalTimeConstant = 190;
        final double ninetyDegreeTurnTime = 900;
        final double driveDistance = Math.cos(Math.toRadians(Math.abs(angleToTurnDegrees))) * centerDistance;
        final int driveDirection = angleToTurnDegrees >= 0 ? 1 : -1; // 1 is right, -1 is left
       
        try {
            // turn
            robotModel.setMotors(driveDirection*turnSpeed, -1*driveDirection*turnSpeed);
            Thread.sleep((long)(turnProportionalTimeConstant*Math.abs(angleToTurnDegrees)));
            // drive forward
            robotModel.setMotors(forwardSpeed,forwardSpeed);
            Thread.sleep((long)(forwardProportionalTimeConstant*driveDistance));
            // turn 90
            robotModel.setMotors(-1*driveDirection*turnSpeed, driveDirection*turnSpeed);
            Thread.sleep((long) ninetyDegreeTurnTime);
            robotModel.setMotors(0, 0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void straight(){
        System.out.println("Go Straight");
        robotModel.setMotors(0.17,0.17);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void depositTop(){
        robotModel.setMotors(0,0);
        final int numGreenBalls = robotInventory.getNumGreenBalls();
        final int numToScoreInTop = numGreenBalls > 1 ? numGreenBalls-1 : numGreenBalls;
        for(int i = 0; i < numToScoreInTop; i++){
            System.out.println("Depositing ball in top");
            try {
            	System.out.println("Depositing ball in top, begin try statement");
                robotModel.setServoReleaseToGreenPosition();
                Thread.sleep(900);
                robotModel.setServoReleaseToScoreUpperPosition();
                Thread.sleep(900);
                System.out.println("Depositing ball in top, end try statement");
                robotInventory.removeGreenBalls(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done depositing balls in top");
    }
    
    private void depositBottom(){
        robotModel.setMotors(0,0);
        final int numToScoreInBottom = robotInventory.getNumGreenBalls();
        for(int i = 0; i < numToScoreInBottom; i++){
            System.out.println("Depositing in bottom");
            try{
            	System.out.println("Depositing ball in bottom, begin try statement");
                robotModel.setServoReleaseToGreenPosition();
                Thread.sleep(900);
                robotModel.setServoReleaseToScoreLowerPosition();
                Thread.sleep(900);
                System.out.println("Depositing ball in bottom, end try statement");
                robotInventory.removeGreenBalls(1);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        robotModel.setServoReleaseToScoreLowerPosition();
        try {
			Thread.sleep(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("Done depositing ball in bottom");
    }
    
    private void turnAwayFromReactor(){
    	final double turnSpeed = 0.2;
        final long turnTime = 1300;
        
        robotModel.setMotors(turnSpeed, -turnSpeed);
        try {
			Thread.sleep(turnTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void reverse(){
    	System.out.println("reverse from reactor begin");
        final double reverseSpeed = -1*0.21;
        robotModel.setMotors(reverseSpeed,reverseSpeed);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("reverse fom reactor end");
    }
      
    private void reverseToDistance(){
        final double reverseSpeed = -0.17;
        robotModel.setMotors(reverseSpeed, reverseSpeed);
    }
    

    @Override
    public void controlState(Mat image) {
        final long timeout = 20000;
        final double depositLowerPortDistance = 10;
        final double misAlignmentDistance = 6.5;
        final double misAlignmentAngle = 40;
        final double insertDistance = 6;
        final int centerXThreshold = 35;
        final double goStraightAngleThreshold = 85;

        reactorSummary.updateReactorSummary(image);
        
        double angle=reactorSummary.getReactorAngleInDegrees();
        double distance=reactorSummary.getReactorCenterDistance();
        int centerX=reactorSummary.getReactorCenterXValue();
        double turningAngle = reactorSummary.getReactorAngleToTurn();
        long runningTime = System.currentTimeMillis() - startTime;
         System.out.println("Angle: "+angle);
        System.out.println("Distance: "+distance);
        System.out.println("Angle to turn: " + reactorSummary.getReactorAngleToTurn());
        System.out.println("Running time reactor: " + runningTime);
        // exit if we time out
        if(runningTime >= timeout){
        	System.out.println("timout, exiting");
            stop();
        }
        // exit if we no longer see reactor
        if(!reactorSummary.isReactorScoreable()){
        	System.out.println("reactor no longer scoreable, exiting");
            stop();
        }   
        // exit if we are about to deposit but mis-aligned
        if(distance <= misAlignmentDistance && angle >= misAlignmentAngle){
        	System.out.println("misaligned, reversing and exiting");
            reverse();
            stop();
        }
        // when close to reactor insert the end of the robot into the reactor by driving straight
        if(distance <= insertDistance && robotInventory.hasGreenBalls() && (state ==ScoreInReactorStates.DRIVER ||
        		state == ScoreInReactorStates.SMALL_ANGLE)){
        	System.out.println("inserting");
            robotModel.setMotors(0,0);
            straight();
            System.out.println("entering deposit top");
            depositTop();
            state = ScoreInReactorStates.REVERSE_TO_DISTANCE;
        }
        // switch to lower deposit state after reverse to distance and then exit state
        else if(state == ScoreInReactorStates.REVERSE_TO_DISTANCE && distance >= depositLowerPortDistance){
            System.out.println("entering deposit in bottom");
        	state = ScoreInReactorStates.DEPOSIT_BOTTOM;
            robotModel.setMotors(0, 0);
            depositBottom();
            reverse();
            turnAwayFromReactor();
            stop();
        }
        // reverse to distance for scoring in lower port
        else if(state == ScoreInReactorStates.REVERSE_TO_DISTANCE){
        	System.out.println("reversing to distance");
            reverseToDistance();
        }
        // switch to center from driver if angle to turn becomes too small
        else if(state == ScoreInReactorStates.SMALL_ANGLE && Math.abs(turningAngle) < goStraightAngleThreshold){
            System.out.println("switching to center from small angle");
        	state = ScoreInReactorStates.CENTER;
        }
        // switch to driver state after manhattan state
        else if(state == ScoreInReactorStates.MANHATTAN  || state == ScoreInReactorStates.DRIVER || 
        		state == ScoreInReactorStates.SMALL_ANGLE ){
        	System.out.println("using driver");
            List<Double> motorSpeeds = driver.driveToReactor(distance, insertDistance, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = (state == ScoreInReactorStates.MANHATTAN) ? ScoreInReactorStates.DRIVER : state ;
        } 
        // switch to center state if not in a state and we need to center
        else if(Math.abs(centerX-centerOfScreen) > centerXThreshold  && (state == ScoreInReactorStates.NONE ||
                state == ScoreInReactorStates.CENTER)) {
        	System.out.println("centering");
            state = ScoreInReactorStates.CENTER;
            centerRobot(centerX);
        }
        // switch into driver and skip manhattan if almost lined up with reactor
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && 
                Math.abs(turningAngle) >= goStraightAngleThreshold &&
                (state == ScoreInReactorStates.CENTER || state == ScoreInReactorStates.NONE)){
        	System.out.println("switching to small angle");
            state = ScoreInReactorStates.SMALL_ANGLE;
        }
        // switch to manhattan state once centered
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && (state == ScoreInReactorStates.CENTER ||
                state == ScoreInReactorStates.NONE)){
        	System.out.println("entering Manhattan");
            state = ScoreInReactorStates.MANHATTAN;
            robotModel.setMotors(0,0);
            manhattan(reactorSummary.getReactorAngleToTurn(), distance);
        }        
        
        
        
        
        
        
        
        
        
        /*
        if(!reactorSummary.isReactorScoreable()){
            stop();
        }
        else if(!robotInventory.hasGreenBalls()){
            reverse();
            stop();
        }
        // center reactor in camera view if too far off
        else if(Math.abs(centerX-centerOfScreen) > 20 && distance > 6 && state == ScoreInReactorStates.NONE) {
            centerRobot(centerX);
        }
        
        // drive straight towards the reactor if the angle is small
        else if(Math.abs(centerX-centerOfScreen) <= 20 && distance >= 6){
            List<Double> motorSpeeds = driver.driveToReactor(distance, 6, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = ScoreInReactorStates.STRAIGHT;
        }
        
        else if( distance < 6 && robotInventory.hasGreenBalls() && !(state ==ScoreInReactorStates.INSERT)){
            state = ScoreInReactorStates.INSERT;
            straight();
        }
        else if(robotInventory.hasGreenBalls() && (state == ScoreInReactorStates.INSERT ||
                state == ScoreInReactorStates.DEPOSIT)){
            deposit();
        }
        */
       
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.SCORE_IN_REACTOR;
    }

    @Override
    public boolean isDone() {
        return done;
    }

}
