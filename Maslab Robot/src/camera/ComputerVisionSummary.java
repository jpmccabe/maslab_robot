package camera;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    private final CameraProcessor4 reactorProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 60;
    private final static double MAX_WALL_DISTANCE_MIDDLE = 7;
    private final static double MAX_WALL_DISTANCE_SIDES = 10;
    private final static double MAX_REACTOR_DISTANCE = 36;
        
    public ComputerVisionSummary(){
        this.redBallProcessor = new CameraProcessor1();
        this.greenBallProcessor = new CameraProcessor2();
        this.blueWallProcessor = new CameraProcessor3();
        this.reactorProcessor = new CameraProcessor4();
        
    }
    
    
    public void updateFullSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        Thread redBallProcessorThread = new Thread(new ProcessorRunner(redBallProcessor,HSVImage));
        Thread greenBallProcessorThread = new Thread(new ProcessorRunner(greenBallProcessor, HSVImage));
        Thread blueWallProcessorThread = new Thread(new ProcessorRunner(blueWallProcessor, HSVImage));
        Thread reactorProcessorThread = new Thread(new ProcessorRunner(reactorProcessor, HSVImage));
        
        redBallProcessorThread.start();
        greenBallProcessorThread.start();
        blueWallProcessorThread.start();
        reactorProcessorThread.start();
        
        try {
            redBallProcessorThread.join();
            greenBallProcessorThread.join();
            blueWallProcessorThread.join();
           // reactorProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    public void updateRedBallSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        redBallProcessor.processImage(HSVImage);
    }
    
    
    public void updateGreenBallSummar(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        greenBallProcessor.processImage(HSVImage);
    }
    
    
    public void updateBallSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        Thread redBallProcessorThread = new Thread(new ProcessorRunner(redBallProcessor,HSVImage));
        Thread greenBallProcessorThread = new Thread(new ProcessorRunner(greenBallProcessor, HSVImage));
        redBallProcessorThread.start();
        greenBallProcessorThread.start();
        
        try {
            redBallProcessorThread.join();
            greenBallProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    public void updateWallSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        blueWallProcessor.processImage(HSVImage);
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
        return ((blueWallProcessor.getCenterDistanceToBlueWall() <= MAX_WALL_DISTANCE_SIDES) ||
                (blueWallProcessor.getLeftDistanceToBlueWall() <= MAX_WALL_DISTANCE_MIDDLE) ||
                (blueWallProcessor.getRightDistanceToBlueWall() <= MAX_WALL_DISTANCE_SIDES));
    }
    
    
    /**
     * @return true if a reactor is in the image and is close, false otherwise.
     */
    public boolean isReactor(){
        return (reactorProcessor.getCenterDistance() <= MAX_REACTOR_DISTANCE);
    }
    
    
    public Mat getRedBallProcessedImage(){
        return redBallProcessor.getProcessedImage();
    }
    
    public Mat getGreenBallProcessedImage(){
        return greenBallProcessor.getProcessedImage();
    }
    
    public Mat getBlueWallProcessedImage(){
        return blueWallProcessor.getProcessedImage();
    }
    
    public Mat getReactorProcessedImage(){
        return reactorProcessor.getProcessedImage();
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
    
    
    private class ProcessorRunner implements Runnable{
        private final CameraProcessor cameraProcessor;
        private final Mat imageToProcess;
        
        public ProcessorRunner(CameraProcessor cameraProcessor, Mat imageToProcess){
            this.cameraProcessor = cameraProcessor;
            this.imageToProcess = imageToProcess;
        }
        
        public void run(){
            this.cameraProcessor.processImage(imageToProcess);
        }
    }
    
    
}
