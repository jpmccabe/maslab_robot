package robotModel;
import org.opencv.core.Core;

import comm.MapleComm;
import comm.MapleIO;
import comm.MapleIO.SerialPortType;
import devices.actuators.Cytron;
import devices.actuators.PWMOutput;
import devices.sensors.Ultrasonic;

public class Devices {
	private final MapleComm maple;
	private final Cytron leftMotor;
	private final Cytron rightMotor;
	private final Cytron spiral;
	private final PWMOutput roller;

	private static final int leftMotor_Dir_Pin = 6;
	private static final int leftMotor_PWM_Pin = 7;
	private static final int rightMotor_Dir_Pin = 1;
	private static final int rightMotor_PWM_Pin = 2;
	private static final int spiral_Dir_Pin = 4;
	private static final int spiral_PWM_Pin = 5;
	private static final int roller_PWM_Pin = 3;
	
	public Devices(){
	    maple = new MapleComm();
	    leftMotor = new Cytron(leftMotor_Dir_Pin, leftMotor_PWM_Pin);
	    rightMotor = new Cytron(rightMotor_Dir_Pin, rightMotor_PWM_Pin);
	    spiral = new Cytron(spiral_Dir_Pin, spiral_PWM_Pin);
	    roller = new PWMOutput(roller_PWM_Pin);
	    
	    maple.registerDevice(leftMotor);
        maple.registerDevice(rightMotor);
        maple.registerDevice(roller);
        maple.registerDevice(spiral);
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
}