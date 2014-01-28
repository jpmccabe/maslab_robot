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
	}
	
	
	/**
	 * Sets the speed of the drive motors. 0.0 is off and 1.0 
	 * is full speed.
	 * @param left speed of the left motor from 0.0 to 1.0
	 * @param right speed of the right motor from 0.0 to 1.0
	 */
	public void setMotors(double left, double right){
		leftMotor.setSpeed(left);
		rightMotor.setSpeed(right);
		maple.transmit();
	}
	
	
	/**
	 * Turns the rubber band roller on or off.
	 * @param on turns the roller on if true, off otherwise.
	 */
	public void setRoller(boolean on){
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
	public void setSpiral(boolean on){
	    double spiralOnSpeed = 0.2;
	    double spiralOffSpeed = 0.0;
	    double speedToSet = on ? spiralOnSpeed : spiralOffSpeed;
		
	    spiral.setSpeed(speedToSet);
		maple.transmit();
	}
	
	
	/**
	 * Turns all motors on the robot off.
	 */
	public void allMotorsOff(){
	    setSpiral(false);
	    setRoller(false);
	    setMotors(0.0,0.0);
	}
	
	
	/**
	 * @return List containing total angular distance in radians of each wheel. The
	 * left motor is the first index and the right motor is the second index.
	 */
    public List<Double> getTotalAngularDistance() {
        List<Double> angularDistance = new ArrayList<Double>();
        
        maple.updateSensorData();
        angularDistance.add(leftEncoder.getTotalAngularDistance());
        angularDistance.add(rightEncoder.getTotalAngularDistance());
        
        return angularDistance;
    }
   
    
    /**
     * @return List containing delta angular distance in radians of each wheel. The
     * left motor is the first index and the right motor is the second index.
     */
    public List<Double> getDeltaAngularDistance() {
        List<Double> deltaAngularDistance = new ArrayList<Double>();
        
        maple.updateSensorData();
        deltaAngularDistance.add(leftEncoder.getDeltaAngularDistance());
        deltaAngularDistance.add(rightEncoder.getDeltaAngularDistance());
        
        return deltaAngularDistance;
    }
    
    
    /**
     * @return List containing angular speed in radians per second of each wheel. The
     * left motor is the first index and the right motor is the second index.
     */
    public List<Double> getAngularSpeed() {
        List<Double> angularSpeed = new ArrayList<Double>();
        
        maple.updateSensorData();
        angularSpeed.add(leftEncoder.getAngularSpeed());
        angularSpeed.add(rightEncoder.getAngularSpeed());
        
        return angularSpeed;
    }
    
    
    
    /**
     * @return List containing delta distance traveled by each wheel in inches. The
     * left motor is the first index and the right motor is the second index.
     */
    public List<Double> getDeltaDistance(){
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
    public List<Double> getSpeed(){
        final double radiusOfWheelInInches = 3.875 / 2;
        List<Double> speed = new ArrayList<Double>();
      
        List<Double> angularSpeed = getAngularSpeed();
        speed.add(angularSpeed.get(0) * radiusOfWheelInInches);
        speed.add(angularSpeed.get(1) * radiusOfWheelInInches);
        
        return speed;
    }
    

}