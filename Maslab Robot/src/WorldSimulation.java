import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

class WorldSimulation implements Runnable{
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	public static double xRobot=150;
	public static double yRobot=350;
	public static double theta=0;
	public static double pi=Math.PI;
	public static double speed=2;
	public static boolean findWall = true;
	static Mat map = Mat.zeros(480, 640,  16);

	
	public void run(){
		initiliazeMap();
		approachWall();
		while(true){
		StopWatch.timeOut(200);
		theta%=(2*pi);
		if(theta<0){  theta+=2*pi; }
		simulate(xRobot, yRobot, theta);
		double[] ultrasonic=Global.ultrasonic;
		
		if(findWall==true){
			
		}
		ultrasonic[1]*=0.866;
		ultrasonic[4]*=0.866;
		
		if(Math.abs(ultrasonic[5]+ultrasonic[4])<50){
			theta-= Math.min(0.1, 3.0/(ultrasonic[4]+ultrasonic[5]));
		}
		
		if(ultrasonic[3]<75){
			theta-= Math.min(0.1, 10.0/ultrasonic[3]);
		}
		
		theta+=Math.min(0.1,(ultrasonic[4]-ultrasonic[5])/150.0);
		
		
		
		speed=(int) (ultrasonic[2])/12.5;
		
		int xfac=1, yfac=1;
		if(theta>pi/2 && theta<3*pi/2){ xfac=-1;}
		if(theta<Math.PI){ yfac=-1;}
		
		xRobot+=xfac*speed*Math.abs(Math.cos(theta));
		yRobot+=yfac*speed*Math.abs(Math.sin(theta));
		
		}
	}
	
	private void approachWall() {
		simulate(xRobot,yRobot, theta);
		while(Global.ultrasonic[3]>50)
		{
			StopWatch.timeOut(200);
			speed=(int) 10;
			
			int xfac=1, yfac=1;
			if(theta>pi/2 && theta<3*pi/2){ xfac=-1;}
			if(theta<Math.PI){ yfac=-1;}
			
			xRobot+=xfac*speed*Math.abs(Math.cos(theta));
			yRobot+=yfac*speed*Math.abs(Math.sin(theta));
			simulate(xRobot,yRobot,theta);
		}
		
	}

	public static void initiliazeMap(){
		
		Scalar color1= new Scalar(255,255,255);
		
		Point[] p= new Point[11];
		
		p[0] =new Point(40,100);
		p[1] =new Point(100,20);
		p[2] =new Point(300,20);
		p[3] =new Point(300,250);
		p[4] =new Point(400,250);
		p[5] =new Point(500,250);
		p[6] =new Point(500,20);
		p[7] =new Point(620,20);
		p[8] =new Point(620,400);
		p[9] =new Point(100,400);
		p[10] = p[0];
		
		for(int i=0; i<p.length-1;i++){
			Core.line(WorldSimulation.map, p[i], p[i+1], color1,10);	
		}
	}
	
	public static void simulate( double xRobot,double yRobot, double theta) {
		
		Scalar color2= new Scalar(200,200,200);
		double[] ultrasonicAngles = {theta-pi/2,theta-pi/3,theta,theta,theta+pi/3,theta+pi/2};
		Mat simulator= WorldSimulation.map.clone();
		
		
		
		for(int j=0; j<ultrasonicAngles.length;j++){
			double gradient=Math.abs(Math.tan(ultrasonicAngles[j]));
			double constant=5;
			
			double angle=ultrasonicAngles[j];
			if (angle<0){angle+=(2*pi);}
			angle%=(2*pi);
			
			int xfac=1, yfac=1;
			if(angle>pi/2 && angle<3*pi/2){ xfac=-1;}
			if(angle<pi){ yfac=-1;}
			
			double x=xRobot, y=yRobot;
			//System.out.println("Sensor #"+j);
			
			
			
			while(x>5 && x<635 && y>5 && y<475 ){
				x+=xfac*constant*Math.abs(Math.cos(angle));
				y+=yfac*constant*Math.abs(Math.sin(angle));
				if(simulator.get((int)y,(int)x)[1]>250.0){
					Core.line(simulator,new Point(xRobot,yRobot), new Point((int)x,(int)y), color2,1);
					Global.ultrasonic[j]=Math.sqrt(Math.pow(x-xRobot,2)+Math.pow(y-yRobot,2));
					//System.out.println(sensorReadings[j]);
					break;
				}
			}
			
		}
		Global.processedImage= simulator;
		 
	}
	
}