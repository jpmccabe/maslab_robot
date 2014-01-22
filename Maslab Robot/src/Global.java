import org.opencv.core.Mat;

public class Global{
	static volatile Mat rawImage= new Mat(); //Camera Image
	static volatile Mat processedImage;     // the filtered image
	
	static volatile double distance2Red=100;
	static volatile double angle2Red=100;
	
	static volatile double distance2Green=100;
	static volatile double angle2Green=100;
	
	static volatile double timer1=0;
	static volatile double timer2=0;
	static volatile double wallClosness=0;
	static volatile int wallThreshold= 160;
	
	static double[] ultrasonic ={0,0,0,0,0,0}; //for Use with World Simulation
	
	static int RedOrGreen=0; //0 means none,1 means red, 2 means green
	
}