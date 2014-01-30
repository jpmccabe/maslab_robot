package camera;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    private final CameraProcessor4 reactorProcessor;
    private final CameraProcessor5 interfaceWallProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 40;
    private final static double MAX_WALL_DISTANCE_MIDDLE = 8;
    private final static double MAX_WALL_DISTANCE_LEFT = 12;
    private final static double MAX_WALL_DISTANCE_RIGHT = 12;
    private final static double MAX_REACTOR_SCORING_DISTANCE = 50;
    private final static double MAX_INTERFACE_WALL_SCORING_DISTANCE = 60;
        
    public ComputerVisionSummary(){
        this.redBallProcessor = new CameraProcessor1();
        this.greenBallProcessor = new CameraProcessor2();
        this.blueWallProcessor = new CameraProcessor3();
        this.reactorProcessor = new CameraProcessor4();
        this.interfaceWallProcessor = new CameraProcessor5();
    }
    
    
    public void updateFullSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        Thread redBallProcessorThread = new Thread(new ProcessorRunner(redBallProcessor,HSVImage));
        Thread greenBallProcessorThread = new Thread(new ProcessorRunner(greenBallProcessor, HSVImage));
        Thread blueWallProcessorThread = new Thread(new ProcessorRunner(blueWallProcessor, HSVImage));
        Thread reactorProcessorThread = new Thread(new ProcessorRunner(reactorProcessor, HSVImage));
        Thread interfaceWallProcessorThread = new Thread(new ProcessorRunner(interfaceWallProcessor, HSVImage));
        
        redBallProcessorThread.start();
        greenBallProcessorThread.start();
        blueWallProcessorThread.start();
        reactorProcessorThread.start();
        interfaceWallProcessorThread.start();
        
        try {
            redBallProcessorThread.join();
            greenBallProcessorThread.join();
            blueWallProcessorThread.join();
            reactorProcessorThread.join();
            interfaceWallProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    
    
    public void updateObstacleSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        Thread blueWallProcessorThread = new Thread(new ProcessorRunner(blueWallProcessor, HSVImage));
        Thread reactorProcessorThread = new Thread(new ProcessorRunner(reactorProcessor, HSVImage));
        Thread interfaceWallProcessorThread = new Thread(new ProcessorRunner(interfaceWallProcessor, HSVImage));
        
        blueWallProcessorThread.start();
        reactorProcessorThread.start();
        interfaceWallProcessorThread.start();
        
        try{
           blueWallProcessorThread.join();
           reactorProcessorThread.join();
           interfaceWallProcessorThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    
    public void updateReactorSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        reactorProcessor.processImage(HSVImage);
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
        return ((getCenterDistanceToBlueWall() <= MAX_WALL_DISTANCE_MIDDLE) ||
                (getRightDistanceToBlueWall() <= MAX_WALL_DISTANCE_RIGHT) ||
                (getLeftDistanceToBlueWall() <= MAX_WALL_DISTANCE_LEFT));
    }
    
    
    /**
     * @return true if there is an obstacle ahead, false otherwise.
     */
    public boolean isObstacle(){
        return getObstacle() != ObstacleDirection.NONE;
    }
    
    
    /**
     * @return if there is an obstacle, returns the direction where it is at (left or right). Returns
     * ObstacleDirection.NONE if there is no obstacle.
     */
    public ObstacleDirection getObstacle(){
        ObstacleDirection direction = ObstacleDirection.NONE;
        
        if(isBlueWall()){
            direction = getLeftDistanceToBlueWall() <= getRightDistanceToBlueWall() ? ObstacleDirection.LEFT :
                        ObstacleDirection.RIGHT;
        }    
        else if(isReactorObstacle()){
            direction = getReactorAngleInDegrees() <= 0 ? ObstacleDirection.RIGHT: ObstacleDirection.LEFT;
        }
        else if(isInterfaceWallObstacle()){
            direction = getInterfaceWallAngleInDegrees() <= 0 ? ObstacleDirection.RIGHT: ObstacleDirection.LEFT;
        }
       
        return direction;
    }
    
    
    
    /**
     * @return true if a reactor is in the image and is close enough to be considered
     * an obstacle, false otherwise.
     */
    public boolean isReactorObstacle(){
        return (getReactorCenterDistance() <= MAX_WALL_DISTANCE_MIDDLE ||
                getReactorLeftDistance() <= MAX_WALL_DISTANCE_LEFT ||
                getReactorRightDistance() <= MAX_WALL_DISTANCE_RIGHT);
    }
    
    
    /**
     * @return true if a reactor is in the image and is close enough to be considered
     * for scoring purposes, false otherwise.
     */
    public boolean isReactorScoreable(){
        return  (getReactorCenterDistance() <= MAX_REACTOR_SCORING_DISTANCE);
    }
    
    
    /**
     * @return true if the interface wall is in the image and is close enough to be
     * considered an obstacle, false otherwise.
     */
    public boolean isInterfaceWallObstacle(){
        return (getInterfaceWallCenterDistance() <= MAX_WALL_DISTANCE_MIDDLE ||
                getInterfaceWallLeftDistance() <= MAX_WALL_DISTANCE_LEFT ||
                getInterfaceWallRightDistance() <= MAX_WALL_DISTANCE_RIGHT);
    }
    
    
    /**
     * @return true if the interface wall is in the image and is close enough to be 
     * considered for scoring purposes, false otherwise.
     */
    public boolean isInterfaceWallScoreable(){
        return (getInterfaceWallCenterDistance() <= MAX_INTERFACE_WALL_SCORING_DISTANCE);
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
    
    public Mat getInterfaceWallProcessedImage(){
        return interfaceWallProcessor.getProcessedImage();
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
    
    
    public int getReactorCenterXValue(){
        return reactorProcessor.getCenterXValue();
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
     * @return the angle to turn to get parallel to the reactor
     */
    public double getReactorAngleToTurn(){
    	return reactorProcessor.getAngleToTurnParallelInDegrees();
    }
    
    /**
     * @return distance to left side of interface wall, if there is one, in inches.
     */
    public double getInterfaceWallLeftDistance(){
        return interfaceWallProcessor.getLeftDistance();
    }
    
    
    /**
     * @return distance to right side of interface wall, if there is one, in inches.
     */
    public double getInterfaceWallRightDistance(){
        return interfaceWallProcessor.getRightDistance();
    }
    
    
    /**
     * @return distance to center of interface wall, if there is one, in inches.
     */
    public double getInterfaceWallCenterDistance(){
        return interfaceWallProcessor.getCenterDistance();
    }
    
    
    /**
     * @return angle to interface, if there is one, in degrees.
     */
    public double getInterfaceWallAngleInDegrees(){
        return interfaceWallProcessor.getAngleInDegrees();
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
