import org.opencv.core.Scalar;

public class PID{
	private final double kp,ki,kd;
	private double P,I,D;	
	private double error,errorOld;
	
	private static final double timeInterval=0.02;

	
	/**
	 * Creates a new PID controller with the given constants.
	 * @param kp proportional constant
	 * @param ki integral constant
	 * @param kd derivative constant
	 */
	public PID(double kp, double ki, double kd){
	    this.kp = kp;
	    this.ki = ki;
	    this.kd = kd;
	}
	
	
	/**
	 * Calculates the output of the PID controller given the desiredValue
	 * and actualValue
	 * @param actualValue current reading
	 * @param desiredValue desired reading
	 * @return output of PID controller
	 */
	public double valuePID(double actualValue, double desiredValue){
		P = error = desiredValue - actualValue;
		D=(error-errorOld)/timeInterval;
		I+=error*timeInterval;
		if(D<-5.0){I=0;}
		errorOld=error;
		
		return kp*P+ ki*I+ kd*D;	
	}
	
	
	
	/**
	 * Resets the integral term to zero and errorOld-error to zero.
	 */
	public void reset(){
	    I = 0;
	    errorOld = error;
	}

}