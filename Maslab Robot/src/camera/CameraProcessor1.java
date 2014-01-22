package camera;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


/**
 * Processes red balls.
 */
class CameraProcessor1{
    private double distanceToRed;
    private double angleToRed;
    private final BallTargeting red;
    private final BallStruct redBall;
    private Mat processedImage;
    
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	
	public CameraProcessor1(){
	    distanceToRed = Double.MAX_VALUE;
	    angleToRed = Double.MAX_VALUE;
	    processedImage = null;
	    red = new BallTargeting();
	    redBall = new BallStruct();
	}
	
	
	public void processImage(Mat rawImage){
	    Mat processedImage = new Mat();
	    Imgproc.cvtColor(rawImage,processedImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV

	    //red
	    Mat redUpper= new Mat();
	    Mat redLower= new Mat();
	    Core.inRange(processedImage, new Scalar(170, 100,20), new Scalar(180, 255, 255), redUpper);
	    Core.inRange(processedImage, new Scalar(0, 100,20), new Scalar(20, 255, 255), redLower);
	    Core.bitwise_or(redLower, redUpper, processedImage);

	    //refining the binary image
	    Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 1);
	    Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),0);

	    //create a clone for the processedImage to be used in finding contours
	    Mat clone= new Mat();
	    clone = processedImage.clone();
	    Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

	    //finds list of contours and draw the biggest on the processedImage
	    Scalar color1= new Scalar(0,0,255);
	    Scalar color2= new Scalar(255,255,0);
	    Scalar color3= new Scalar(255,255,255);
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

	    List<MatOfPoint> contour= new ArrayList<MatOfPoint> (1);
	    double maxArea =0.0;
	    for(int index=0;index<contours.size();index++){
	        double area= Imgproc.contourArea(contours.get(index));
	        if (area>maxArea && area>20 && Imgproc.boundingRect(contours.get(index)).y>150){
	            maxArea=area;
	            contour.add(0,contours.get(index));
	        }	
	    }
	    //finds bounding Rectangle and draws contours
	    Rect boundingRect= new Rect();
	    if (contour.size()>0){
	        Imgproc.drawContours(processedImage , contour, -2, color1);
	        boundingRect= Imgproc.boundingRect(contour.get(0));
	        double x=boundingRect.x;
	        double y=boundingRect.y;
	        double width= boundingRect.width;
	        double height=boundingRect.height; 
	        Core.rectangle(processedImage, new Point(x,y), new Point(x+width,y+height), color3);

	    }
	    //finding bounding Circle and draws it
	    Point center=new Point();
	    float[] radius=new float[1];
	    if (contour.size()>0){
	        MatOfPoint2f  contour2f = new MatOfPoint2f( contour.get(0).toArray() );
	        Imgproc.minEnclosingCircle(contour2f, center, radius);
	        Core.circle(processedImage, center,(int)radius[0], color2);
	    }
	    redBall.rect=boundingRect;
	    redBall.circle.center=center;
	    redBall.circle.radius=(double) radius[0];

	    double returned[]= red.calculate(redBall);

	    synchronized(this){
	        distanceToRed = returned[0];
	        angleToRed = returned[1];
	        this.processedImage = processedImage;
	    }
	}
   	
   	
   	/**
   	 * The distance the nearest red ball.
   	 * @return distance in meters
   	 */
   	synchronized public double getDistanceToRedBall(){
   	    return distanceToRed;
   	}
   	
   	
   	/**
   	 * Angle to the nearest red ball.
   	 * @return angle in radians
   	 */
   	synchronized public double getAngleRedBall(){
   	    return angleToRed;
   	}
   	
   	
   	/**
   	 * @return the processed image
   	 */
   	synchronized public Mat getProcessedImage(){
   	    return processedImage.clone();
   	}
   		
}