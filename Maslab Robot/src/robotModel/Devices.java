package robotModel;

import java.util.ArrayList;
import java.util.List;

import comm.MapleComm;
import comm.MapleIO;
import comm.MapleIO.SerialPortType;
import devices.actuators.*;
import devices.sensors.*;

public class Devices {
	private final MapleComm maple;
	private final Cytron leftMotor;
	private final Cytron rightMotor;
	private final Cytron spiral;
	private final PWMOutput roller;
	private final Encoder leftEncoder;
	private final Encoder rightEncoder;

	private static final int leftMotor_Dir_Pin = 6;
	private static final int leftMotor_PWM_Pin = 7;
	private static final int rightMotor_Dir_Pin = 1;
	private static final int rightMotor_PWM_Pin = 2;
	private static final int rightMotor_Encoder_A = 30;
	private static final int rightMotor_Encoder_B = 29;
	private static final int leftMotor_Encoder_A = 34;
	private static final int leftMotor_Encoder_B = 32;
	private static final int spiral_Dir_Pin = 4;
	private static final int spiral_PWM_Pin = 5;
	private static final int roller_PWM_Pin = 3;
	
	private double leftMotorSetSpeed = 0;
	private double rightMotorSetSpeed = 0;
	private double leftMotorRawSpeed = 0;
	private double rightMotorRawSpeed = 0;
	
	public Devices(){
	    maple = new MapleComm();
	    leftMotor = new Cytron(leftMotor_Dir_Pin, leftMotor_PWM_Pin);
	    rightMotor = new Cytron(rightMotor_Dir_Pin, rightMotor_PWM_Pin);
	    spiral = new Cytron(spiral_Dir_Pin, spiral_PWM_Pin);
	    roller = new PWMOutput(roller_PWM_Pin);
	    leftEncoder = new Encoder(leftMotor_Encoder_A, leftMotor_Encoder_B);
	    rightEncoder = new Encoder(rightMotor_Encoder_A, rightMotor_Encoder_B);
	    
	    maple.registerDevice(leftMotor);
        maple.registerDevice(rightMotor);
        maple.registerDevice(roller);
        maple.registerDevice(spiral);
        maple.registerDevice(leftEncoder);
        maple.registerDevice(rightEncoder);
        maple.initialize();
        
        Thread PIDThread =  new Thread(new Runnable(){
            public void run(){
                final PIDMotor leftMotorPID = new PIDMotor(0);
                while(true){
                    List<Double> measuredSpeed = getMeasuredSpeed();
                    List<Double> setSpeed = getSetSpeed();
                    
                    double leftSetSpeed = setSpeed.get(0);
                    double leftMeasuredSpeed = measuredSpeed.get(0);
                    
                    System.out.println("set speed: " + leftSetSpeed);
                    System.out.println("measured speed: " + leftMeasuredSpeed);
                    
                    if(leftMotorPID.getSetPoint() != leftSetSpeed){
                        leftMotorPID.newSetPoint(leftSetSpeed);
                    }
                    
                    double leftValueToSet = leftMotorPID.update(measuredSpeed.get(0));
                    System.out.println("Raw value: " + leftValueToSet+getLeftMotorRawSpeed());
                    setMotors(leftValueToSet+getLeftMotorRawSpeed(),0);
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //PIDThread.start();
	}
	
	
	/**
	 * Sets the speed of the drive motors. 0.0 is off and 1.0 
	 * is full speed.
	 * @param left speed of the left motor from 0.0 to 1.0
	 * @param right speed of the right motor from 0.0 to 1.0
	 */
	public void setMotors(double left, double right){
	    leftMotorRawSpeed = left;
	    rightMotorRawSpeed = right;
		leftMotor.setSpeed(left);
		rightMotor.setSpeed(right);
		maple.transmit();
	}
	
	
	/**
	 * @param left target speed of the left motor in inches per second
	 * @param right target speed of the right motor in inches per second
	 */
	synchronized public void setMotorSpeed(double left, double right){
	    leftMotorSetSpeed = left;
	    rightMotorSetSpeed = right;
	}
	
	
	/**
	 * @return list containing the speeds of the motor, in inches per second, as
	 * set in setMotorSpeed. The left motor is the first index and the right motor 
	 * is the second index.
	 */
	synchronized public List<Double> getSetSpeed(){
	    List<Double> speeds = new ArrayList<Double>();
	    speeds.add(leftMotorSetSpeed);
	    speeds.add(rightMotorSetSpeed);
	    return speeds;
	}
	
	
	synchronized public double getLeftMotorRawSpeed(){
	    return leftMotorRawSpeed;
	}
	
	
	/**
	 * Turns the rubber band roller on or off.
	 * @param on turns the roller on if true, off otherwise.
	 */
	synchronized public void setRoller(boolean on){
	    double rollerOnSpeed = 0.5;
	    double rollerOffSpeed = 0.0;
	    double speedToSet = on ? rollerOnSpeed : rollerOffSpeed;
	    
		roller.setValue(speedToSet);
		maple.transmit();
	}
	
	
	/**
	 * Turns the spiral lift on or off.
	 * @param on turns the lift on if true, off otherwise.
	 */
	synchronized public void setSpiral(boolean on){
	    double spiralOnSpeed = 0.2;
	    double spiralOffSpeed = 0.0;
	    double speedToSet = on ? spiralOnSpeed : spiralOffSpeed;
		
	    spiral.setSpeed(speedToSet);
		maple.transmit();
	}
	
	
	/**
	 * Turns all motors on the robot off.
	 */
	synchronized public void allMotorsOff(){
	    setSpiral(false);
	    setRoller(false);
	    setMotors(0.0,0.0);
	}
	
	
	/**
	 * @return List containing total angular distance in radians of each wheel. The
	 * left motor is the first index and the right motor is the second index.
	 */
	synchronized public List<Double> getTotalAngularDistance() {
        List<Double> angularDistance = new ArrayList<Double>();
        
        maple.updateSensorData();
        angularDistance.add(-1*leftEncoder.getTotalAngularDistance());
        angularDistance.add(rightEncoder.getTotalAngularDistance());
        
        return angularDistance;
    }
   
    
    /**
     * @return List containing delta angular distance in radians of each wheel. The
     * left motor is the first index and the right motor is the second index.
     */
	synchronized public List<Double> getDeltaAngularDistance() {
        List<Double> deltaAngularDistance = new ArrayList<Double>();
        
        maple.updateSensorData();
        deltaAngularDistance.add(-1*leftEncoder.getDeltaAngularDistance());
        deltaAngularDistance.add(rightEncoder.getDeltaAngularDistance());
        
        return deltaAngularDistance;
    }
    
    
    /**
     * @return List containing angular speed in radians per second of each wheel. The
     * left motor is the first index and the right motor is the second index.
     */
	synchronized public List<Double> getAngularSpeed() {
        List<Double> angularSpeed = new ArrayList<Double>();
        
        maple.updateSensorData();
        angularSpeed.add(-1*leftEncoder.getAngularSpeed());
        angularSpeed.add(rightEncoder.getAngularSpeed());
        
        return angularSpeed;
    }
    
    
    
    /**
     * @return List containing delta distance traveled by each wheel in inches. The
     * left motor is the first index and the right motor is the second index.
     */
	synchronized public List<Double> getDeltaDistance(){
        final double radiusOfWheelInInches = 3.875 / 2;
        List<Double> deltaDistanceInches = new ArrayList<Double>();
      
        List<Double> deltaAngularDistance = getDeltaAngularDistance();
        deltaDistanceInches.add(deltaAngularDistance.get(0) * radiusOfWheelInInches);
        deltaDistanceInches.add(deltaAngularDistance.get(1) * radiusOfWheelInInches);
        
        return deltaDistanceInches;
    }
    
    
    /**
     * @return List containing speed of each wheel in inches per second. The left motor
     * is the first index and the right motor is the second index.
     */
	synchronized public List<Double> getMeasuredSpeed(){
        final double radiusOfWheelInInches = 3.875 / 2;
        List<Double> speed = new ArrayList<Double>();
      
        List<Double> angularSpeed = getAngularSpeed();
        speed.add(angularSpeed.get(0) * radiusOfWheelInInches);
        speed.add(angularSpeed.get(1) * radiusOfWheelInInches);
        
        return speed;
    }
	
	
	private class PIDMotor{
	    private final static double KP = 0.1;
	    private final static double KI = 0.01;
	    private final static double KD = 0.;
	    private final static double MAX_SPEED = 0.6;
	    private final static double MIN_SPEED = 0.0;
	    
	    private double previousError = 0;
	    private double integral = 0;
	    private double setPoint;
	           
	    public PIDMotor(double setPoint){
	        this.setPoint = setPoint;
	    }
	    
	    public double update(double measuredValue){	        
	        if(setPoint == 0){
	            return 0;
	        }
	        
	        double error = setPoint - measuredValue;	        
	        double proportional = KP * error;
	        integral += KI * error;
	        double derivative = KD * (error - previousError);
	        previousError = error;
	        
	        double returnValue = proportional + integral + derivative;
	        returnValue = clampSpeed(returnValue, MIN_SPEED, MAX_SPEED);
	        
	        return ( returnValue);
	    }
	    
	    public void newSetPoint(double setPoint){
	        integral = 0;
	        previousError = 0;
	        this.setPoint = setPoint;
	    }
	    
	    public double getSetPoint(){
	        return setPoint;
	    }
	    
	    
	    /**
	     * Keeps a speed between a minimum and maximum
	     * @param currentSpeed the speed to be clamped
	     * @param minSpeed the minimum allowed speed
	     * @param maxSpeed the maximum allowed speed
	     * @return returns minSpeed if currentSpeed < minSpeed, maxSpeed if currentSpeed > maxSpeed,
	     *         or currentSpeed otherwise.
	     */
	    private double clampSpeed(double currentSpeed, double minSpeed, double maxSpeed){
	        final double topClamp = Math.min(currentSpeed, maxSpeed);
	        final double bottomClamp  = Math.max(topClamp, minSpeed);
	        return bottomClamp;
	    }
	}
    
}

