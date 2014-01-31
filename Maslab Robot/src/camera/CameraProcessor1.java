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
public class CameraProcessor1 extends CameraProcessor{
    private double distanceToRed;
    private double angleToRedInDegrees;
    private Mat processedImage;
    
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	
	public CameraProcessor1(){
	    distanceToRed = Double.MAX_VALUE;
	    angleToRedInDegrees = Double.MAX_VALUE;
	    processedImage = null;
	}
	
	
	public void processImage(Mat imageToProcess){
	    final Mat processedImage = imageToProcess.clone();
	    //red
	    final Mat redUpper = new Mat();
	    final Mat redLower = new Mat();
	    Core.inRange(processedImage, new Scalar(170, 100,20), new Scalar(180, 255, 255), redUpper);
	    Core.inRange(processedImage, new Scalar(0, 100,20), new Scalar(20, 255, 255), redLower);
	    Core.bitwise_or(redLower, redUpper, processedImage);

	    //refining the binary image
	    Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 1);
	    Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),0);

	    //create a clone for the processedImage to be used in finding contours
	    final Mat clone =  processedImage.clone();
	    Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

	    //finds list of contours and draw the biggest on the processedImage
	    final Scalar color1 = new Scalar(0,0,255);
	    final Scalar color2 = new Scalar(255,255,0);
	    final Scalar color3 = new Scalar(255,255,255);
	    final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

	    final List<MatOfPoint> contour = new ArrayList<MatOfPoint> (1);
	    double maxArea = 0.0;
	    for(int index=0;index<contours.size();index++){
	        double area= Imgproc.contourArea(contours.get(index));
	        if (area>maxArea && area>25 && Imgproc.boundingRect(contours.get(index)).y>40){
	            maxArea=area;
	            contour.add(0,contours.get(index));
	        }	
	    }
	    //finds bounding Rectangle and draws contours
	    Rect boundingRect = new Rect();
	    if (contour.size()>0){
	        Imgproc.drawContours(processedImage , contour, -2, color1);
	        boundingRect = Imgproc.boundingRect(contour.get(0));
	        final double x = boundingRect.x;
	        final double y = boundingRect.y;
	        final double width = boundingRect.width;
	        final double height =boundingRect.height; 
	        Core.rectangle(processedImage, new Point(x,y), new Point(x+width,y+height), color3);

	    }
	    //finding bounding Circle and draws it
	    final Point center = new Point();
	    final float[] radius = new float[1];
	    if (contour.size()>0){
	        final MatOfPoint2f  contour2f = new MatOfPoint2f( contour.get(0).toArray() );
	        Imgproc.minEnclosingCircle(contour2f, center, radius);
	        Core.circle(processedImage, center,(int)radius[0], color2);
	    }
	    
	    final BallStruct redBallStruct = new BallStruct(boundingRect, center, (double)radius[0]);
	    final BallTargeting ballTargeting = new BallTargeting(redBallStruct);

	    synchronized(this){
	        distanceToRed = ballTargeting.getDistance();
	        angleToRedInDegrees = ballTargeting.getAngle() * (180/Math.PI);
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
   	 * @return angle in degrees
   	 */
   	synchronized public double getAngleToRedBallInDegrees(){
   	    return angleToRedInDegrees;
   	}
   	
   	
   	/**
   	 * @return the processed image
   	 */
   	synchronized public Mat getProcessedImage(){
   	    return processedImage.clone();
   	}
   		
}