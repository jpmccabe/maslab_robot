package camera;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 100;
    private final static double MAX_WALL_DISTANCE = 100;
    
    public ComputerVisionSummary(CameraProcessor1 redBallProcessor, 
            CameraProcessor2 greenBallProcessor, CameraProcessor3 blueWallProcessor){
        
        this.redBallProcessor = redBallProcessor;
        this.greenBallProcessor = greenBallProcessor;
        this.blueWallProcessor = blueWallProcessor;
    }
    
    public boolean isRedBall(){
        return (redBallProcessor.getDistanceToRedBall() <= MAX_BALL_DISTANCE);
    }
    
    public boolean isGreenBall(){
        return (greenBallProcessor.getDistanceToGreenBall() <= MAX_BALL_DISTANCE);
    }
    
    public boolean isBlueWall(){
        return (blueWallProcessor.getDistanceToBlueWall() <= MAX_WALL_DISTANCE);
    }
    
    public double getDistanceToRedBall(){
        return (redBallProcessor.getDistanceToRedBall());
    }
    
    public double getAngleToRedBall(){
        return (redBallProcessor.getAngleToRedBall());
    }
    
    public double getDistanceToGreenBall(){
        return (greenBallProcessor.getDistanceToGreenBall());
    }
    
    public double getAngleToGreenBall(){
        return (greenBallProcessor.getAngleToGreenBall());
    }
    
    public double getDistanceToBlueWall(){
        return (blueWallProcessor.getDistanceToBlueWall());
    }
    
    
    /**
     * Produces a summary of an image
     * @param image
     * @return
     */
    public static ComputerVisionSummary produceSummary(Mat image){
        Mat convertedImageToHSV = new Mat();
        Imgproc.cvtColor(image,convertedImageToHSV,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        CameraProcessor1 redBallProcessor = new CameraProcessor1();
        CameraProcessor2 greenBallProcessor = new CameraProcessor2();
        CameraProcessor3 blueWallProcessor = new CameraProcessor3();
        
        redBallProcessor.processImage(image);
        greenBallProcessor.processImage(image);
        blueWallProcessor.processImage(image);
        
        return (new ComputerVisionSummary(redBallProcessor, greenBallProcessor, blueWallProcessor));
    }
    
    
}
