package camera;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    private final CameraProcessor4 reactorProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 100;
    private final static double MAX_WALL_DISTANCE = 100;
    private final static double MAX_REACTOR_DISTANCE = 90;
    
    public ComputerVisionSummary(CameraProcessor1 redBallProcessor, 
            CameraProcessor2 greenBallProcessor, CameraProcessor3 blueWallProcessor,
            CameraProcessor4 reactorProcessor){
        
        this.redBallProcessor = redBallProcessor;
        this.greenBallProcessor = greenBallProcessor;
        this.blueWallProcessor = blueWallProcessor;
        this.reactorProcessor = reactorProcessor;
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
    
    public boolean isReactor(){
        return (reactorProcessor.getCenterDistance() <= MAX_REACTOR_DISTANCE);
    }
    
    public double getDistanceToRedBall(){
        return (redBallProcessor.getDistanceToRedBall());
    }
    
    public double getAngleToRedBall(){
        return (redBallProcessor.getAngleToRedBallInDegrees());
    }
    
    public double getDistanceToGreenBall(){
        return (greenBallProcessor.getDistanceToGreenBall());
    }
    
    public double getAngleToGreenBall(){
        return (greenBallProcessor.getAngleToGreenBallInDegrees());
    }
    
    public double getDistanceToBlueWall(){
        return (blueWallProcessor.getDistanceToBlueWall());
    }
    
    public double getReactorLeftDistance(){
        return reactorProcessor.getLeftDistance();
    }
    
    public double getReactorRightDistance(){
        return reactorProcessor.getRightDistance();
    }
    
    public double getReactorCenterDistance(){
        return reactorProcessor.getCenterDistance();
    }
    
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
