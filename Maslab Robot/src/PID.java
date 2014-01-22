import org.opencv.core.Scalar;

public class PID{
	public double kp,ki,kd;
	double P,I,D;
	
	double error,errorOld;
	double timeInterval=0.02;
	
	int reset=1;
	
	public void setConstants(Scalar constants){
		kp= constants.val[0];
		ki= constants.val[1];
		kd= constants.val[2];
	}
	public double valuePID(double angle, double angleDesired){
		
		P= error = angle-angleDesired;
		if (reset==1){reset=0; errorOld=error; I=0;}
		D=(error-errorOld)/timeInterval;
		I+=error*timeInterval;
		if(D<-5.0){I=0;}
		errorOld=error;
		
		return kp*P+ ki*I+ kd*D;	
	}
}