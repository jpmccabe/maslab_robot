package driving;
import java.lang.Math;
import java.util.*;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class Driver{
	
	private final PID angularPID;
	private final PID straightPID;
	
	private final static double ANGULAR_KP = 0.015;
	private final static double ANGULAR_KI = 0.0;
	private final static double ANGULAR_KD = 0.0;
	private final static double STRAIGHT_KP = 0.0065;
	private final static double STRAIGHT_KI = 0.0;
	private final static double STRAIGHT_KD = 0.0;
	
	public Driver(){
	    angularPID = new PID(ANGULAR_KP, ANGULAR_KI, ANGULAR_KD);
	    straightPID = new PID(STRAIGHT_KP, STRAIGHT_KI, STRAIGHT_KD);
	}

	
	/**
	 * Returns the necessary motor speed to drive to a ball
	 * @param distance distance to ball
	 * @param distanceTarget target distance to ball
	 * @param angle angle to ball
	 * @param angleTarget target angle to ball
	 * @return list such that list.get(0) is the left motor speed and list.get(1) is
	 * the right motor speed
	 */
	public List<Double> driveToBall(double distance, double distanceTarget, double angle, double angleTarget){
		final double angularSpeed = angularPID.valuePID(angle,angleTarget);
		final double straightSpeed = straightPID.valuePID(distance,distanceTarget);
		System.out.println("distance: " + distance);
		System.out.println("angle: " + angle);
		System.out.println("angular: " + angularSpeed);
		System.out.println("speed: " + straightSpeed);
		
		final List<Double> motorSpeeds  = new ArrayList<Double>();
		final double minSpeed = 0.1;
		final double maxSpeed = 0.6;
		final double leftMotorSpeed = clampSpeed(straightSpeed-angularSpeed,minSpeed, maxSpeed);
		final double rightMotorSpeed = clampSpeed(straightSpeed+angularSpeed, minSpeed, maxSpeed);
	    motorSpeeds.add(leftMotorSpeed);
	    motorSpeeds.add(rightMotorSpeed);
		
		return motorSpeeds;
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
	
	
	/*
	public void driveByWall(double angle,double angleTarget){
		double angularSpeed= angularPID.valuePID(angle,angleTarget);
		Main.devices.setMotors(0.9+angularSpeed,0.9-angularSpeed);
		
	}
	
	
	public void rotateInPlace(double velocity, int direction){
		StopWatch.resetTime();
		while(StopWatch.getTime()<1){
			Main.devices.setMotors(velocity*direction,-velocity*direction);
		}
		Main.devices.setMotors(0,0);
	}
	*/
}