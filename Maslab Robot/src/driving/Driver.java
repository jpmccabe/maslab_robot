package driving;
import java.lang.Math;
import java.util.*;

public class Driver{
	
	private final PID angularPID;
	private final PID straightPID;
	private final PID angularPIDReactor;
	private final PID straightPIDReactor;
	
	private final static double ANGULAR_KP = 0.0040;
	private final static double ANGULAR_KI = 0.0;
	private final static double ANGULAR_KD = -0.0;
	private final static double STRAIGHT_KP = -0.0080;
	private final static double STRAIGHT_KI = 0.0;
	private final static double STRAIGHT_KD = 0.0;
	
	private final static double ANGULAR_KP_REACTOR = -0.0006;
	private final static double ANGULAR_KI_REACTOR = 0;
	private final static double ANGULAR_KD_REACTOR = 0;
	private final static double STRAIGHT_KP_REACTOR = -0.0025;
	private final static double STRAIGHT_KI_REACTOR = 0.0;
	private final static double STRAIGHT_KD_REACTOR = 0.0;
	
	
	public Driver(){
	    angularPID = new PID(ANGULAR_KP, ANGULAR_KI, ANGULAR_KD);
	    straightPID = new PID(STRAIGHT_KP, STRAIGHT_KI, STRAIGHT_KD);
	    angularPIDReactor = new PID (ANGULAR_KP_REACTOR, ANGULAR_KI_REACTOR,ANGULAR_KD_REACTOR );
	    straightPIDReactor = new PID(STRAIGHT_KP_REACTOR,STRAIGHT_KP_REACTOR,STRAIGHT_KD_REACTOR );
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
		
		final double minStraightSpeed = 0.17;
		final double maxStraightSpeed = 0.250;
		
		final double minAngularSpeed = -0.15;
		final double maxAngularSpeed = 0.15;
		
		final double angularSpeed = clampSpeed(angularPID.valuePID(angle,angleTarget),minAngularSpeed,maxAngularSpeed);
		final double straightSpeed =clampSpeed(straightPID.valuePID(distance,distanceTarget),minStraightSpeed,maxStraightSpeed);
		//System.out.println("distance: " + distance);
		//System.out.println("angle: " + angle);
		//System.out.println("angular: " + angularSpeed);
		//System.out.println("speed: " + straightSpeed);
		
		final List<Double> motorSpeeds  = new ArrayList<Double>();
		final double leftMotorSpeed = straightSpeed+angularSpeed;
		final double rightMotorSpeed = straightSpeed-angularSpeed;
		
		//System.out.println("left: " + leftMotorSpeed);
		//System.out.println("right: " + rightMotorSpeed);
		//System.out.println("---------------------");
		
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
	
	
	public List<Double> driveToReactor(double distance, double distanceTarget, double angle, double angleTarget){
		
		final double minStraightSpeed = 0.15;
		final double maxStraightSpeed = 0.250;
		
		final double minAngularSpeed = -0.15;
		final double maxAngularSpeed = 0.15;
		
		final double angularSpeed = clampSpeed(angularPIDReactor.valuePID(angle,angleTarget),minAngularSpeed,maxAngularSpeed);
		final double straightSpeed =clampSpeed(straightPIDReactor.valuePID(distance,distanceTarget),minStraightSpeed,maxStraightSpeed);
		//System.out.println("distance: " + distance);
		//System.out.println("angle: " + angle);
		//System.out.println("angular: " + angularSpeed);
		//System.out.println("speed: " + straightSpeed);
		
		final List<Double> motorSpeeds  = new ArrayList<Double>();
		final double leftMotorSpeed = straightSpeed+angularSpeed;
		final double rightMotorSpeed = straightSpeed-angularSpeed;
		
		//System.out.println("left: " + leftMotorSpeed);
		//System.out.println("right: " + rightMotorSpeed);
		//System.out.println("---------------------");
		
	    motorSpeeds.add(leftMotorSpeed);
	    motorSpeeds.add(rightMotorSpeed);
		
		return motorSpeeds;
	}
	

}