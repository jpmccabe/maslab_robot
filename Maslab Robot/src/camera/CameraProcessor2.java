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
class CameraProcessor2 {   
    private double distanceToGreen;
    private double angleToGreen;
    private final BallTargeting green;
    private final BallStruct greenBall;
    private Mat processedImage;
    
	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	
	public CameraProcessor2(){
	    distanceToGreen = Double.MAX_VALUE;
	    angleToGreen = Double.MAX_VALUE;
	    processedImage = null;
	    green = new BallTargeting();
	    greenBall = new BallStruct();
	}
	
	
   	public void proccesImage(Mat rawImage) {
   	    Mat processedImage = new Mat();
   	    Imgproc.cvtColor(rawImage,processedImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV

   	    //green			
   	    Core.inRange(processedImage, new Scalar(50, 75,10), new Scalar(85, 255, 255), processedImage);

   	    //refining the binary image
   	    Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 1);
   	    Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),0);

   	    //create a clone for the processedImage to be used in finding contours
   	    Mat clone = new Mat();
   	    clone = processedImage.clone();
   	    Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

   	    //finds list of contours and draw the biggest on the processedImage
   	    Scalar color1 = new Scalar(0,0,255);
   	    Scalar color2 = new Scalar(255,255,0);
   	    Scalar color3 = new Scalar(255,255,255);
   	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
   	    Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

   	    List<MatOfPoint> contour= new ArrayList<MatOfPoint> (1);
   	    double maxArea = 0.0;
   	    for(int index=0;index<contours.size();index++){
   	        double area = Imgproc.contourArea(contours.get(index));
   	        if (area>maxArea && area>20 && Imgproc.boundingRect(contours.get(index)).y>150){
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
   	    Point center=new Point();
   	    float[] radius=new float[1];
   	    if (contour.size()>0){
   	        MatOfPoint2f  contour2f = new MatOfPoint2f( contour.get(0).toArray() );
   	        Imgproc.minEnclosingCircle(contour2f, center, radius);
   	        Core.circle(processedImage, center,(int)radius[0], color2);
   	    }

   	    greenBall.rect = boundingRect;
   	    greenBall.circle.center = center;
   	    greenBall.circle.radius = (double) radius[0];

   	    double returned[] = green.calculate(greenBall);

   	    synchronized(this){
   	        distanceToGreen = returned[0];
   	        angleToGreen = returned[1];
   	        this.processedImage = processedImage;
   	    }
   	    //System.out.println("distance2green:" +returned[0] + ",angle2green:" +returned[0]);
   	    //Global.processedImage=processedImage.clone();
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
     * @return angle in radians
     */
    synchronized public double getAngleToGreenBall(){
        return angleToGreen;
    }
    
    
    /**
     * 
     * @return the processed image
     */
    synchronized public Mat getProcessedImage(){
        return processedImage;
    }
}