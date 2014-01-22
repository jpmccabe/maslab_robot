import java.lang.Math;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class Driver{
	
	PID angularPID = new PID();
	PID straightPID = new PID();
	
	public void setPID(){
		angularPID.kp=0.25;
		angularPID.ki=0;
		angularPID.kd=0.0008;
		
		straightPID.kp=0.002;
		straightPID.ki=0.0;
		straightPID.kd=0.0;
		
	}
	public void Stop(){
		
	}
	public void driveToBall(double distance, double distanceTarget, double angle, double angleTarget){
		
		double angularSpeed= angularPID.valuePID(angle,angleTarget)/3.0;
		double straightSpeed= straightPID.valuePID(distance,distanceTarget);
		straightSpeed+=0.075;
		//straightSpeed=Math.min(straightSpeed,0.3);
		//straightSpeed=Math.max(straightSpeed,0.09);
		System.out.println("distance:" + distance);
		System.out.println("angle:" + angle);
		System.out.println("angular:" + angularSpeed);
		System.out.println("speed:"+ straightSpeed);
		Main.devices.setMotors(straightSpeed-angularSpeed,straightSpeed+angularSpeed);
	}
	public void driveByWall(double angle,double angleTarget)
	{
		double angularSpeed= angularPID.valuePID(angle,angleTarget)/3.0;
		Main.devices.setMotors(0.9+angularSpeed,0.9-angularSpeed);
		
	}
	public void rotateInPlace(double velocity, int direction){
		StopWatch.resetTime();
		while(StopWatch.getTime()<1){
			Main.devices.setMotors(velocity*direction,-velocity*direction);
		}
		Main.devices.setMotors(0,0);
	}
	
}