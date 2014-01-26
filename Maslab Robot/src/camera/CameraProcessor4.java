package camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Image processor for reactors.
 */
public class CameraProcessor4 {
    private double wallCloseness;
    private Mat processedImage;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public CameraProcessor4(){
        wallCloseness = Double.MAX_VALUE;
        processedImage  = null;
    }


    public void processImage(Mat imageToProcess) {
        Mat processedImage = imageToProcess.clone();

        //Blue Walls
        Core.inRange(processedImage, new Scalar(90, 75,10), new Scalar(120, 255, 255), processedImage);

        Imgproc.Canny(processedImage, processedImage, 15, 200);     
        double[] blueStripe= new double[64];
        for(int x=5;x<640;x+=10){
            int firstPixel=0;
            int secondPixel=0;
            for(int y=240;y>0;y--){
                if( processedImage.get(y,x)[0]==255){
                    if (firstPixel==0) firstPixel=y;
                    else secondPixel=y;
                }
                if (secondPixel!= 0) break;
            }
            blueStripe[x/10]=firstPixel-secondPixel;
            Core.line(processedImage, new Point(x,firstPixel), new Point(x,secondPixel), new Scalar(255,0,0));
        }


        Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);
        
        synchronized(this){
            wallCloseness = 600/blueStripe[2];
            this.processedImage = processedImage;
        }
    }
    
    
    /**
     * @return how close a reactor was in the last image
     */
    synchronized public double getDistanceToReactor(){
        return wallCloseness;
    }
    
    
    /**
     * Returns the last processed image
     * @return the last processed image
     */
    synchronized public Mat getProcessedImage(){
        return processedImage.clone();
    }
}
