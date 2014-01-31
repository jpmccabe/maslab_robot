package stateMachine;

import java.util.List;

import org.opencv.core.Mat;

import camera.*;
import driving.*;
import robotModel.*;

public class CollectFromSiloStateController extends StateMachine {
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private final long startTime;
    private boolean done = false;
    private CollectFromSiloStates state;
    private final ComputerVisionSummary siloSummary;
    private final Driver driver;
    private final int centerOfScreen = 348;
    
    public CollectFromSiloStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
        startTime = System.currentTimeMillis();
        siloSummary = new ComputerVisionSummary();
        state = CollectFromSiloStates.NONE;
        driver = new Driver();
    }
    
    @Override
    public void stop() {
        robotModel.setMotors(0,0);
        done = true;
    }
    
    
    private void centerRobot(int centerX){
        System.out.println("CenterX:"+centerX);
        System.out.println("adjust to center");
        final double minSpeed = 0.13;
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
        final double turnProportionalTimeConstant = 10;
        final double forwardProportionalTimeConstant = 220;
        final double ninetyDegreeTurnTime = 800;
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
        try {
            robotModel.setMotors(0.14,0.14);
            Thread.sleep(1200);
            /*
            robotModel.setMotors(0.20,-.12);
            Thread.sleep(700);
            robotModel.setMotors(-.12,0.20);
            Thread.sleep(700);
            */
            robotModel.setMotors(0, 0);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    private void remove(){
        final double reverseSpeed = -0.19;
        final double forwardSpeed = 0.19;
        final long reverseTime = 850;
        robotModel.setMotors(0,0);
        System.out.println("Removing ball from silo");
        
        BallColor ballColor = BallColor.NONE;
        if(siloSummary.isGreenBall()){
        	ballColor = BallColor.GREEN;
        } else if(siloSummary.isRedBall()){
        	ballColor = BallColor.RED;
        } 
        
        try {
            System.out.println("lowering arm");
            robotModel.setServoArmToDownPosition();
            Thread.sleep(800);
            System.out.println("driving in reverse");
            robotModel.setRoller(true);
            robotModel.setSpiral(true);
            robotModel.setMotors(reverseSpeed,reverseSpeed);
            Thread.sleep(reverseTime);
            robotModel.setMotors(0,0);
            System.out.println("raising arm");
            robotModel.setServoArmToUpPosition();
            Thread.sleep(150);
            System.out.println("driving forward");
            robotModel.setMotors(forwardSpeed, forwardSpeed);
            Thread.sleep(reverseTime);
            robotModel.setMotors(0,0);
            System.out.println("adding ball to queue");
            TimedBall collectedBall = new TimedBall(System.currentTimeMillis(), ballColor);
            robotInventory.addBallToQueue(collectedBall);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        System.out.println("Done removing ball.");
    }
    
    
    private void turnAwayFromSilo(){
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
        final double reverseSpeed = -0.12;
        robotModel.setMotors(reverseSpeed,reverseSpeed);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void controlState(Mat image) {
        final long timeout = 12000;
        final double misAlignmentDistance = 6;
        final double misAlignmentAngle = 45;
        final double insertDistance = 9;
        final int centerXThreshold = 30;
        final double goStraightAngleThreshold = 85;
       
        siloSummary.updateSiloSummary(image);
        
        double angle = siloSummary.getSiloAngleInDegrees();
        double distance = siloSummary.getSiloCenterDistance();
        double angleToTurn = siloSummary.getSiloAngleToTurn();
        int centerX = siloSummary.getSiloCenterXValue();
        long currentRunningTime = System.currentTimeMillis() - startTime; 

        // System.out.println("Angle: " + angle);
        System.out.println("Distance silo: " + distance);
        System.out.println("Angle to turn silo: " + angleToTurn);
        System.out.println("running time silo: " + currentRunningTime);
        
        // exit if we time out
        if(currentRunningTime >= timeout){
        	reverse();
        	turnAwayFromSilo();
            stop();
        }
        // exit if we no longer see silo
        /*
        if(!siloSummary.isSiloCollectable() &&
                !(state == CollectFromSiloStates.REMOVE) &&
                !(state == CollectFromSiloStates.REVERSE) &&
                !(state == CollectFromSiloStates.INSERT)){
            stop();
        } 
        */
        /*
        // exit if we are about to deposit but mis-aligned
        if(distance <= misAlignmentDistance && angle >= misAlignmentAngle){
            reverse();
            stop();
        }
        */
        // when close to reactor insert the end of the robot into the reactor by driving straight
        if(distance <= insertDistance && (state == CollectFromSiloStates.DRIVER || state == CollectFromSiloStates.SMALL_ANGLE)){
            System.out.println("Inserting into silo");
        	state = CollectFromSiloStates.INSERT;
            robotModel.setMotors(0,0);
            System.out.println("Set servo arm to mid position");
            robotModel.setServoArmToMidPosition();
            System.out.println("Done setting arm to mid position");
            try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            System.out.println("Done delaying setting arm to mid position");
            straight();
            state = CollectFromSiloStates.REMOVE;
            System.out.println("entering remove state");
            remove();
            System.out.println("reverse");
            reverse();
            System.out.println("entering turning away from silo");
            turnAwayFromSilo();
            stop();
        }

        // switch to center from driver if angle to turn becomes too small
        else if(state == CollectFromSiloStates.SMALL_ANGLE && 
                Math.abs(angleToTurn) < goStraightAngleThreshold){
        	System.out.println("switching to center from small angle silo");
            state = CollectFromSiloStates.CENTER;
        }
        // switch to driver state after manhattan state
        else if(state == CollectFromSiloStates.MANHATTAN  || state == CollectFromSiloStates.DRIVER || 
                state == CollectFromSiloStates.SMALL_ANGLE ){
        	System.out.println("using driver silo");
            List<Double> motorSpeeds = driver.driveToReactor(distance, insertDistance, centerX-centerOfScreen, 0);
            System.out.println("Left: " + motorSpeeds.get(0) + " Right: " + motorSpeeds.get(1));
            robotModel.setMotors(motorSpeeds.get(0), motorSpeeds.get(1));
            state = (state == CollectFromSiloStates.MANHATTAN) ? CollectFromSiloStates.DRIVER : state ;
        } 
        // switch to center state if not in a state and we need to center
        else if(Math.abs(centerX-centerOfScreen) > centerXThreshold  && (state == CollectFromSiloStates.NONE ||
                state == CollectFromSiloStates.CENTER)) {
        	System.out.println("centering silo");
            state = CollectFromSiloStates.CENTER;
            centerRobot(centerX);
        }
        // switch into driver and skip manhattan if almost lined up with interface wall
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && 
                Math.abs(angleToTurn) >= goStraightAngleThreshold &&
                (state == CollectFromSiloStates.CENTER || state == CollectFromSiloStates.NONE)){
        	System.out.println("entering small angle silo");
            state = CollectFromSiloStates.SMALL_ANGLE;
        }
        // switch to manhattan state once centered
        else if(Math.abs(centerX-centerOfScreen) <= centerXThreshold && (state == CollectFromSiloStates.CENTER ||
                state == CollectFromSiloStates.NONE)){
            System.out.println("Manhattan silo");
            state = CollectFromSiloStates.MANHATTAN;
            robotModel.setMotors(0,0);
            manhattan(angleToTurn, distance);
        }    
    }

    @Override
    public StateMachineType getStateMachineType() {
        return StateMachineType.COLLECT_FROM_ENERGY_SILO;
    }

    @Override
    public boolean isDone() {
        return done;
    }

}
