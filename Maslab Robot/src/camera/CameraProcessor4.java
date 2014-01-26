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
    private double reactorDistance;
    private Mat processedImage;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public CameraProcessor4(){
        reactorDistance = Double.MAX_VALUE;
        processedImage  = null;
    }


    public void processImage(Mat imageToProcess) {
        
    }
    
    
    /**
     * @return how close a reactor was in the last image
     */
    synchronized public double getDistanceToReactor(){
        return reactorDistance;
    }
    
    
    /**
     * Returns the last processed image
     * @return the last processed image
     */
    synchronized public Mat getProcessedImage(){
        return processedImage.clone();
    }
}
