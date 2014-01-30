package camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class CameraProcessor3 extends CameraProcessor{

    private double centerDistance;
    private double leftDistance;
    private double rightDistance;
    private Mat processedImage;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public CameraProcessor3(){
        centerDistance = Double.MAX_VALUE;
        leftDistance =  Double.MAX_VALUE;
        rightDistance = Double.MAX_VALUE;
        processedImage  = null;
    }


    public void processImage(Mat imageToProcess) {
        final Mat processedImage = imageToProcess.clone();
        
        //Blue Walls
        Core.inRange(processedImage, new Scalar(100, 75,10), new Scalar(125, 255, 255), processedImage);    
        Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),1);
        Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 2); 
        Imgproc.Canny(processedImage, processedImage, 15, 200);     
        Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);
        
        synchronized(this){
            this.processedImage = processedImage;
        }
        
        double leftDistance = getDistance(5)/0.8;
        double centerDistance = getDistance(285);
        double rightDistance = getDistance(570)/0.8;
        
        synchronized(this){
            this.leftDistance = leftDistance;
            this.rightDistance = rightDistance;
            this.centerDistance = centerDistance;
        }
    }
    
    
    private double getDistance(int xStart){
        Mat processedImage = this.processedImage;
        int firstPixel=0;
        int secondPixel=0;
        int sampleSize=0;
        double averageDistance = 0;
        for(int x=xStart;x<xStart+65;x+=10){
            //left area
            firstPixel=0;
            secondPixel=0;
            for(int y=350;y>0;y--){
                if( processedImage.get(y,x)[0]==255){
                    if (firstPixel==0) firstPixel=y;
                    else secondPixel=y;
                }
                if (secondPixel!= 0) break;
            }
            if(pixelToDistance(firstPixel-secondPixel)<50){
                sampleSize+=1;
                averageDistance+=pixelToDistance(firstPixel-secondPixel);
                Core.line(processedImage, new Point(x,firstPixel), new Point(x,secondPixel), new Scalar(255,0,0));
            }
        }
        averageDistance = sampleSize > 0 ? averageDistance/sampleSize : 1000;
        System.out.println(sampleSize);
        return averageDistance;
    }
    
    
    private double pixelToDistance(int pixel){
    	return 346.187/Math.pow(pixel,0.878562);
    }
    
    
    /**
     * @return the distance to the center of a blue wall
     */
    synchronized public double getCenterDistanceToBlueWall(){
        return centerDistance;
    }
    
    
    /**
     * @return the distance to the left side of a blue wall
     */
    synchronized public double getLeftDistanceToBlueWall(){
        return leftDistance;
    }
    
    
    /**
     * @return the distance of the right side of a blue wall
     */
    synchronized public double getRightDistanceToBlueWall(){
        return rightDistance;
    }
    
    
    /**
     * Returns the last processed image
     * @return the last processed image
     */
    synchronized public Mat getProcessedImage(){
        return processedImage.clone();
    }
    
}
