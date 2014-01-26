package camera;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    private final CameraProcessor4 reactorProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 36;
    private final static double MAX_WALL_DISTANCE = 36;
    private final static double MAX_REACTOR_DISTANCE = 36;
    
    public ComputerVisionSummary(CameraProcessor1 redBallProcessor, 
            CameraProcessor2 greenBallProcessor, CameraProcessor3 blueWallProcessor,
            CameraProcessor4 reactorProcessor){
        
        this.redBallProcessor = redBallProcessor;
        this.greenBallProcessor = greenBallProcessor;
        this.blueWallProcessor = blueWallProcessor;
        this.reactorProcessor = reactorProcessor;
    }
    
    
    /**
     * @return true if a red ball is in the image and is close, false otherwise.
     */
    public boolean isRedBall(){
        return (redBallProcessor.getDistanceToRedBall() <= MAX_BALL_DISTANCE);
    }
    
    
    /**
     * @return true if a green ball is in the image and is close, false otherwise.
     */
    public boolean isGreenBall(){
        return (greenBallProcessor.getDistanceToGreenBall() <= MAX_BALL_DISTANCE);
    }
    
    
    /**
     * @return true if a blue wall is in the image and is close, false otherwise.
     */
    public boolean isBlueWall(){
        return (blueWallProcessor.getDistanceToBlueWall() <= MAX_WALL_DISTANCE);
    }
    
    
    /**
     * @return true if a reactor is in the image and is close, false otherwise.
     */
    public boolean isReactor(){
        return (reactorProcessor.getCenterDistance() <= MAX_REACTOR_DISTANCE);
    }
    
    
    /**
     * @return distance to red ball, if there is one, in inches.
     */
    public double getDistanceToRedBall(){
        return (redBallProcessor.getDistanceToRedBall());
    }
    
    
    /**
     * @return angle to red ball, if there is one, in degrees
     */
    public double getAngleToRedBall(){
        return (redBallProcessor.getAngleToRedBallInDegrees());
    }
    
    
    /**
     * @return distance to green ball, if there is one, in inches
     */
    public double getDistanceToGreenBall(){
        return (greenBallProcessor.getDistanceToGreenBall());
    }
    
    
    /**
     * @return angle to green ball, if there is one, in degrees
     */
    public double getAngleToGreenBall(){
        return (greenBallProcessor.getAngleToGreenBallInDegrees());
    }
    
    
    /**
     * @return distance to the center of a blue wall, if there is one, in inches
     */
    public double getCenterDistanceToBlueWall(){
        return (blueWallProcessor.getCenterDistanceToBlueWall());
    }
    
    
    /**
     * @return distance to the left side of a blue wall, if there is one, in inches
     */
    public double getLeftDistanceToBlueWall(){
        return (blueWallProcessor.getLeftDistanceToBlueWall());
    }
    
    
    /**
     * @return distance to the right side of a blue wall, if there is one, in inches
     */
    public double getRightDistanceToBlueWall(){
        return (blueWallProcessor.getRightDistanceToBlueWall());
    }
    
    
    /**
     * @return distance to left side of reactor, if there is one, in inches.
     */
    public double getReactorLeftDistance(){
        return reactorProcessor.getLeftDistance();
    }
    
    
    /**
     * @return distance to right side of reactor, if there is one, in inches.
     */
    public double getReactorRightDistance(){
        return reactorProcessor.getRightDistance();
    }
    
    
    /**
     * @return distance to center of reactor, if there is one, in inches.
     */
    public double getReactorCenterDistance(){
        return reactorProcessor.getCenterDistance();
    }
    
    
    /**
     * @return angle to reactor, if there is one, in degrees.
     */
    public double getReactorAngleInDegrees(){
        return reactorProcessor.getAngleInDegrees();
    }
    
    
    /**
     * Produces a full summary of an image by running all camera processors on that image
     * @param image
     * @return full summary of the recognizable features in the image
     */
    public static ComputerVisionSummary produceFullSummary(Mat image){
        Mat convertedImageToHSV = new Mat();
        Imgproc.cvtColor(image,convertedImageToHSV,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        CameraProcessor1 redBallProcessor = new CameraProcessor1();
        CameraProcessor2 greenBallProcessor = new CameraProcessor2();
        CameraProcessor3 blueWallProcessor = new CameraProcessor3();
        CameraProcessor4 reactorProcessor = new CameraProcessor4();
        
        redBallProcessor.processImage(image);
        greenBallProcessor.processImage(image);
        blueWallProcessor.processImage(image);
        reactorProcessor.processImage(image);
        
        return (new ComputerVisionSummary(redBallProcessor, greenBallProcessor, blueWallProcessor, reactorProcessor));
    }
    
    
    
    /**
     * Produces a summary of an image by just running the camera processors for balls on
     * that image. The methods not pertaining to balls in the returned ComputerVisionSummary
     * are not applicable. 
     * @param image
     * @return summary of the ball features in the image
     */
    public static ComputerVisionSummary produceBallSummary(Mat image){
        Mat convertedImageToHSV = new Mat();
        Imgproc.cvtColor(image,convertedImageToHSV,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        CameraProcessor1 redBallProcessor = new CameraProcessor1();
        CameraProcessor2 greenBallProcessor = new CameraProcessor2();
        CameraProcessor3 blueWallProcessor = new CameraProcessor3();
        CameraProcessor4 reactorProcessor = new CameraProcessor4();
        
        redBallProcessor.processImage(image);
        greenBallProcessor.processImage(image);
        
        return (new ComputerVisionSummary(redBallProcessor, greenBallProcessor, blueWallProcessor, reactorProcessor));
    }
    
    
    
}
