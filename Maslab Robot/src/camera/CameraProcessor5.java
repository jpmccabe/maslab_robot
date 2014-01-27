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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


/**
 * Processor for energy silo
 */
public class CameraProcessor5 extends CameraProcessor{

    private Mat processedImage;
    private double leftDistance;
    private double rightDistance;
    private double centerDistance;
    private double angleInDegrees;
    private int centerXValue;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public CameraProcessor5(){
        leftDistance = Double.MAX_VALUE;
        rightDistance = Double.MAX_VALUE;
        centerDistance = Double.MIN_VALUE;
        angleInDegrees = Double.MAX_VALUE;
        centerXValue = Integer.MAX_VALUE;
        processedImage  = null;
    }

    public void processImage(Mat imageToProcess) {
        boolean reactorSpotted=false;       
        final Mat processedImage = imageToProcess.clone();

        Core.inRange(processedImage, new Scalar(123, 25,10), new Scalar(160, 255, 255), processedImage);
        Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),1);
        Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 2);

        final Mat clone = processedImage.clone();

        final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

        double maxArea = 0.0;
        Rect boundingRect = new Rect();
        for(int index=0;index<contours.size();index++){
            double area= Imgproc.contourArea(contours.get(index));
            if (area>maxArea && area>500){
                boundingRect=Imgproc.boundingRect(contours.get(index));
                reactorSpotted = true;
                maxArea = area;
            }   
        }

        Imgproc.Canny(processedImage, processedImage, 15, 200);     
        final double[] distanceToWall= new double[64];
        double averageDistance = 100.0;
        int firstX=boundingRect.x+10;
        int lastX=0;
        if (reactorSpotted==true){
            averageDistance=0;
            int sampleSize=0;
            for(int x=boundingRect.x+10;x<boundingRect.x+boundingRect.width-10;x+=10){
                int firstPixel=0;
                int secondPixel=0;
                lastX=x;
                for(int y=Math.max(boundingRect.y-2, 1);y<= Math.min(boundingRect.y+boundingRect.height+2,479);y++){
                    if( processedImage.get(y,x)[0]==255){
                        if (firstPixel==0) firstPixel=y;
                        else secondPixel=y;
                    }
                }
                distanceToWall[x/10]=500.0/(secondPixel-firstPixel);
                averageDistance+=distanceToWall[x/10];
                sampleSize+=1;
                Core.line(processedImage, new Point(x,firstPixel), new Point(x,secondPixel), new Scalar(255,0,0));
            }
            averageDistance/=sampleSize;
        }


        Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

        final double leftDistance = distanceToWall[firstX/10];
        final double rightDistance = distanceToWall[lastX/10];
        final double angleInRadians = (leftDistance-rightDistance)/averageDistance*1.80;
        final double angleInDegrees = angleInRadians * (180/Math.PI);
        final int centerXValue = (int) ((boundingRect.x) + (boundingRect.width/2.0));
        
        synchronized(this){
            this.leftDistance = leftDistance;
            this.rightDistance = rightDistance;
            this.centerDistance = averageDistance;
            this.angleInDegrees = angleInDegrees;
            this.centerXValue = centerXValue;
            this.processedImage = processedImage;
        }

        //System.out.println("center: " + centerXValue);
        //System.out.println("distance: " + averageDistance);
        //System.out.println("distance from left: " + leftDistance);
        //System.out.println("distance from right: " + rightDistance);
        //System.out.println("angle:" + angleInDegrees);
    }
    
    
    synchronized public double getLeftDistance(){
        return leftDistance;
    }
    
    synchronized public double getRightDistance(){
        return rightDistance;
    }
    
    synchronized public double getCenterDistance(){
        return centerDistance;
    }
    
    synchronized public double getAngleInDegrees(){
        return angleInDegrees;
    }
    
    synchronized public int getCenterXValue(){
        return centerXValue;
    }
    
    synchronized public Mat getProcessedImage(){
        return processedImage;
    }
}