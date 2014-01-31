package camera;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ComputerVisionSummary {
    
    private final CameraProcessor1 redBallProcessor;
    private final CameraProcessor2 greenBallProcessor;
    private final CameraProcessor3 blueWallProcessor;
    private final CameraProcessor4 reactorProcessor;
    private final CameraProcessor5 interfaceWallProcessor;
    private final CameraProcessor6 siloProcessor;
    
    private final static double MAX_BALL_DISTANCE  = 40;
    private final static double MAX_WALL_DISTANCE_MIDDLE = 8;
    private final static double MAX_WALL_DISTANCE_LEFT = 10;
    private final static double MAX_WALL_DISTANCE_RIGHT = 10;
    private final static double MAX_REACTOR_OBSTACLE_DISTANCE = 13;
    private final static double MAX_REACTOR_SCORING_DISTANCE = 30;
    private final static double MAX_INTERFACE_WALL_SCORING_DISTANCE = 30;
    private final static double MAX_SILO_FOLLOW_DISTANCE = 20;
    private final static double NO_OBSTACLE_DISTANCE = 15;
        
    public ComputerVisionSummary(){
        this.redBallProcessor = new CameraProcessor1();
        this.greenBallProcessor = new CameraProcessor2();
        this.blueWallProcessor = new CameraProcessor3();
        this.reactorProcessor = new CameraProcessor4();
        this.interfaceWallProcessor = new CameraProcessor5();
        this.siloProcessor = new CameraProcessor6();
    }
    
    
    public void updateFullSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        
        Thread redBallProcessorThread = new Thread(new ProcessorRunner(redBallProcessor,HSVImage));
        Thread greenBallProcessorThread = new Thread(new ProcessorRunner(greenBallProcessor, HSVImage));
        Thread blueWallProcessorThread = new Thread(new ProcessorRunner(blueWallProcessor, HSVImage));
        Thread reactorProcessorThread = new Thread(new ProcessorRunner(reactorProcessor, HSVImage));
        Thread interfaceWallProcessorThread = new Thread(new ProcessorRunner(interfaceWallProcessor, HSVImage));
        Thread siloProcessorThread = new Thread(new ProcessorRunner(siloProcessor, HSVImage));
        
        redBallProcessorThread.start();
        greenBallProcessorThread.start();
        blueWallProcessorThread.start();
        reactorProcessorThread.start();
        interfaceWallProcessorThread.start();
        siloProcessorThread.start();
        
        try {
            redBallProcessorThread.join();
            greenBallProcessorThread.join();
            blueWallProcessorThread.join();
            reactorProcessorThread.join();
            interfaceWallProcessorThread.join();
            siloProcessorThread.join();
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
        Thread siloProcessorThread = new Thread(new ProcessorRunner(siloProcessor, HSVImage));
        
        blueWallProcessorThread.start();
        reactorProcessorThread.start();
        interfaceWallProcessorThread.start();
        siloProcessorThread.start();
        
        try{
           blueWallProcessorThread.join();
           reactorProcessorThread.join();
           interfaceWallProcessorThread.join();
           siloProcessorThread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    
    public void updateReactorSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        reactorProcessor.processImage(HSVImage);
    }
    
    
    public void updateSiloSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
    	Thread redBallProcessorThread = new Thread(new ProcessorRunner(redBallProcessor,HSVImage));
        Thread greenBallProcessorThread = new Thread(new ProcessorRunner(greenBallProcessor, HSVImage));
        Thread siloProcessorThread = new Thread(new ProcessorRunner(siloProcessor, HSVImage));
        
        redBallProcessorThread.start();
        greenBallProcessorThread.start();
        siloProcessorThread.start();
        
        try {
            redBallProcessorThread.join();
            greenBallProcessorThread.join();
            siloProcessorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void updateInterfaceWallSummary(Mat image){
        final Mat HSVImage = new Mat();
        Imgproc.cvtColor(image,HSVImage,Imgproc.COLOR_BGR2HSV); //convert BGR to HSV
        interfaceWallProcessor.processImage(HSVImage);
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
        else if(isSiloObstacle()){
        	direction = getSiloAngleInDegrees() <= 0 ? ObstacleDirection.RIGHT: ObstacleDirection.LEFT;
        }
       
        return direction;
    }
    
    
    public boolean noObstacle(){
    	boolean value = (getCenterDistanceToBlueWall() > NO_OBSTACLE_DISTANCE) &&
    			(getLeftDistanceToBlueWall() > NO_OBSTACLE_DISTANCE) &&
    			(getRightDistanceToBlueWall() > NO_OBSTACLE_DISTANCE) &&
                (getReactorCenterDistance() > NO_OBSTACLE_DISTANCE) &&
                (getReactorRightDistance() > NO_OBSTACLE_DISTANCE) &&
                (getReactorLeftDistance() > NO_OBSTACLE_DISTANCE) &&
    			(getInterfaceWallCenterDistance() > NO_OBSTACLE_DISTANCE) &&
    			(getInterfaceWallRightDistance() > NO_OBSTACLE_DISTANCE) &&
    			(getInterfaceWallLeftDistance() > NO_OBSTACLE_DISTANCE) &&
    			(getSiloCenterDistance() > NO_OBSTACLE_DISTANCE) &&
    	        (getSiloRightDistance() > NO_OBSTACLE_DISTANCE) &&
    	        (getSiloLeftDistance() > NO_OBSTACLE_DISTANCE);
      
    	return value;
    }
    
    
    
    /**
     * @return true if a reactor is in the image and is close enough to be considered
     * an obstacle, false otherwise.
     */
    public boolean isReactorObstacle(){
        return (getReactorCenterDistance() <= MAX_REACTOR_OBSTACLE_DISTANCE ||
                getReactorLeftDistance() <= MAX_REACTOR_OBSTACLE_DISTANCE ||
                getReactorRightDistance() <= MAX_REACTOR_OBSTACLE_DISTANCE);
    }
    
    
    /**
     * @return true if a reactor is in the image and is close enough to be considered
     * for scoring purposes, false otherwise.
     */
    public boolean isReactorScoreable(){
        return  (getReactorCenterDistance() <= MAX_REACTOR_SCORING_DISTANCE);
    }
    
    
    /**
     * @return true if the silo is in the image and is close enough to be considered
     * and obstacle.
     */
    public boolean isSiloObstacle(){
        return (getSiloCenterDistance() <= MAX_WALL_DISTANCE_MIDDLE ||
                getSiloLeftDistance() <= MAX_WALL_DISTANCE_LEFT ||
                getSiloRightDistance() <= MAX_WALL_DISTANCE_RIGHT);
    }
    
    
    /**
     * @return true if the silo is in the image and is close enough to be considered
     * for collecting balls
     */
    public boolean isSiloCollectable(){
        return (getSiloCenterDistance() <= MAX_SILO_FOLLOW_DISTANCE);
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
    public Mat getSiloProcessedImage(){
        return siloProcessor.getProcessedImage();
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
     * @return pixel number of the center of the reactor.
     */
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
     * @return pixel number of the center of the interface wall.
     */
    public int getInterfaceWallCenterXValue(){
        return interfaceWallProcessor.getCenterXValue();
    }
    
    /**
     * @return angle to interface, if there is one, in degrees.
     */
    public double getInterfaceWallAngleInDegrees(){
        return interfaceWallProcessor.getAngleInDegrees();
    }
    
    /**
     * 
     * @return Manhattan angle to interface wall.
     */
    public double getInterfaceWallAngleToTurn(){
    	return interfaceWallProcessor.getAngleToTurnParallelInDegrees();
    }
    
    
    /**
     * @return distance to left side of silo, if there is one, in inches.
     */
    public double getSiloLeftDistance(){
        return siloProcessor.getLeftDistance();
    }
    
    
    /**
     * @return distance to right side of silo, if there is one, in inches.
     */
    public double getSiloRightDistance(){
        return siloProcessor.getRightDistance();
    }
    
    
    /**
     * @return distance to center of silo, if there is one, in inches.
     */
    public double getSiloCenterDistance(){
        return siloProcessor.getCenterDistance();
    }
    /**
     * @return pixel number of the center of the silo.
     */
    public int getSiloCenterXValue(){
        return siloProcessor.getCenterXValue();
    }
    
    /**
     * @return angle to silo, if there is one, in degrees.
     */
    public double getSiloAngleInDegrees(){
        return siloProcessor.getAngleInDegrees();
    }
    
    /**
     * 
     * @return Manhattan angle to silo
     */
    public double getSiloAngleToTurn(){
    	return siloProcessor.getAngleToTurnParallelInDegrees();
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
