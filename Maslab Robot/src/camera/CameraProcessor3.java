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


class CameraProcessor3 implements Runnable{

    private double wallCloseness;
    private Mat processedImage;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public CameraProcessor3(){
        wallCloseness = Double.MAX_VALUE;
        processedImage  = null;
    }


    public  void run() {
        StopWatch.resetTime();
        //StopWatch.timeOut(200);
        Mat processedImage = new Mat();
        Imgproc.cvtColor(Global.rawImage,processedImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV

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

        /*for(int i=0;i<64;i++){
				System.out.println(i+":"+600/blueStripe[i]);
			}*/
        Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);
        wallCloseness = 600/blueStripe[2];
        //Global.processedImage= processedImage.clone();
    }
    
    
    /**
     * Returns how close a blue wall was in the last processed image
     * @return
     */
    synchronized public double getWallCloseness(){
        return wallCloseness;
    }
    
    
    
}
