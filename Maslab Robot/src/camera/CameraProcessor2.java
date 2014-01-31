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
 * Processes green balls.
 */
public class CameraProcessor2 extends CameraProcessor{   
    private double distanceToGreen;
    private double angleToGreenInDegrees;
    private Mat processedImage;
    
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	
	public CameraProcessor2(){
	    distanceToGreen = Double.MAX_VALUE;
	    angleToGreenInDegrees = Double.MAX_VALUE;
	    processedImage = null;
	}
	
	
   	public void processImage(Mat imageToProcess) {
   	    final Mat processedImage = imageToProcess.clone();

   	    //green			
   	    Core.inRange(processedImage, new Scalar(50, 75,10), new Scalar(85, 255, 255), processedImage);

   	    //refining the binary image
   	    Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 1);
   	    Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),0);

   	    //create a clone for the processedImage to be used in finding contours
   	    final Mat clone = processedImage.clone();
   	    Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

   	    //finds list of contours and draw the biggest on the processedImage
   	    final Scalar color1 = new Scalar(0,0,255);
   	    final Scalar color2 = new Scalar(255,255,0);
   	    final Scalar color3 = new Scalar(255,255,255);
   	    final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
   	    Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

   	    final List<MatOfPoint> contour= new ArrayList<MatOfPoint> (1);
   	    double maxArea = 0.0;
   	    for(int index=0;index<contours.size();index++){
   	        double area = Imgproc.contourArea(contours.get(index));
   	        if (area>maxArea && area>25 && Imgproc.boundingRect(contours.get(index)).y>40){
   	            maxArea = area;
   	            contour.add(0,contours.get(index));
   	        }	
   	    }

   	    //finds bounding Rectangle and draws contours
   	    Rect boundingRect = new Rect();
   	    if (contour.size()>0){
   	        Imgproc.drawContours(processedImage , contour, -2, color1);
   	        boundingRect = Imgproc.boundingRect(contour.get(0));
   	        double x = boundingRect.x;
   	        double y = boundingRect.y;
   	        double width = boundingRect.width;
   	        double height = boundingRect.height; 
   	        Core.rectangle(processedImage, new Point(x,y), new Point(x+width,y+height), color3);
   	    }

   	    //finding bounding Circle and draws it
   	    final Point center = new Point();
   	    final float[] radius = new float[1];
   	    if (contour.size()>0){
   	        MatOfPoint2f  contour2f = new MatOfPoint2f(contour.get(0).toArray());
   	        Imgproc.minEnclosingCircle(contour2f, center, radius);
   	        Core.circle(processedImage, center,(int)radius[0], color2);
   	    }

   	    final BallStruct greenBallStruct = new BallStruct(boundingRect, center, (double)radius[0]);
   	    final BallTargeting ballTargeting = new BallTargeting(greenBallStruct);

   	    synchronized(this){
   	        distanceToGreen = ballTargeting.getDistance();
   	        angleToGreenInDegrees = (ballTargeting.getAngle() * (180/Math.PI));
   	        this.processedImage = processedImage;
   	    }
   	}
   	
   	
   	/**
     * The distance the nearest green ball.
     * @return distance in meters
     */
    synchronized public double getDistanceToGreenBall(){
        return distanceToGreen;
    }
    
    
    /**
     * Angle to the nearest green ball.
     * @return angle in degrees
     */
    synchronized public double getAngleToGreenBallInDegrees(){
        return angleToGreenInDegrees;
    }
    
    
    /**
     * 
     * @return the processed image
     */
    synchronized public Mat getProcessedImage(){
        return processedImage;
    }
}