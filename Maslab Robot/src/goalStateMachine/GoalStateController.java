package goalStateMachine;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import stateMachine.*;
import camera.*;
import robotModel.*;

public class GoalStateController{
    
    private final Devices robotModel;
    //private final CameraGUI redBallProcessedImageGUI;
    private final ComputerVisionSummary summaryOfImage;
    private StateMachine currentStateController;
    private final VideoCapture camera;
    private Mat lastFrame;


    public GoalStateController(Devices robotModel){
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        this.currentStateController = new StopStateController();
        this.robotModel = robotModel;
        summaryOfImage = new ComputerVisionSummary();
         
        lastFrame = new Mat();
        // Setup the camera
        camera = new VideoCapture();
        camera.open(0);
        //this.redBallProcessedImageGUI = new CameraGUI(1280,720);

        Thread cameraReadThread = new Thread(new Runnable(){
            public void run(){
             while(true){
                 // Wait until the camera has a new frame
                 while (!camera.read(lastFrame)) {
                     try {
                         Thread.sleep(10);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }       
                 //removes garbage memory taken
                 System.out.println("new camera frame");
                 System.gc();
             }
            }
        });
        
        cameraReadThread.start();
    }
    
    
    
    private void roam(){
        
    }
    
    
    
    private void avoidWalls(){
        currentStateController.stop();
        final AvoidWallStateController avoidWallController = 
                new AvoidWallStateController(robotModel);
        
        currentStateController = avoidWallController;
        
        Thread avoidWallThread = new Thread(new Runnable(){
            public void run(){
                avoidWallController.controlState(lastFrame);
            }
        });
        
        avoidWallThread.start();
    }
    
    
    
    private void score(){
        
    }
    
    
    
    private void collectFromEnergySilo(){
        
    }
    
    
    
    private void collectGroundBalls(){
        currentStateController.stop();
        final BallCollectionStateController ballCollectionController = 
                new BallCollectionStateController(robotModel);
        
        currentStateController = ballCollectionController;
        
        Thread ballCollectionThread = new Thread(new Runnable(){
            public void run(){
                while(!ballCollectionController.isDone()){
                    ballCollectionController.controlState(lastFrame);
                }
            }
        });
        
        ballCollectionThread.start();
    }
    
    
    
    private void depositRedBalls(){
        
    }
    
    
    
    public void controlState(){
        long startTime = System.nanoTime();

        summaryOfImage.updateFullSummary(lastFrame);
        //redBallProcessedImageGUI.updateImagePane(summaryOfImage.getRedBallProcessedImage());
        
        final double wallThresholdDistance = 10;
        
        final boolean wallTooClose = ((summaryOfImage.getCenterDistanceToBlueWall() <= wallThresholdDistance) ||
                                   (summaryOfImage.getLeftDistanceToBlueWall() <= wallThresholdDistance) ||
                                   (summaryOfImage.getRightDistanceToBlueWall() <= wallThresholdDistance));
        
        // if a wall is close, and we are not currently avoiding walls, then avoid walls
        if(wallTooClose && 
            !(currentStateController.getStateMachineType() == StateMachineType.AVOID_WALLS && 
            !currentStateController.isDone())){
            System.out.println("Avoiding walls");
            //avoidWalls();
        }
        
        // else if see ball, and not currently collecting one, then collect ball
        else if((summaryOfImage.isGreenBall() || summaryOfImage.isRedBall()) && 
                !(currentStateController.getStateMachineType() == StateMachineType.COLLECT_GROUND_BALLS
                && !currentStateController.isDone())){
            System.out.println("Collecting balls");
            collectGroundBalls();
        }
        
        long estimatedTime = (System.nanoTime() - startTime);
        System.out.println(estimatedTime);
        // if see reactor and have green balls then score
        // if see interface wall and have red balls then score over wall
        // if see energy silo then collect ball
        
        
    }

    
    
    public static void main(String args[]){
        final Devices robotModel = new Devices();
        final GoalStateController goalController = new GoalStateController(robotModel);
  
        Thread goalControllerThread  = new Thread(new Runnable(){
            public void run(){
                while(true){
                    goalController.controlState();
                }
            }
        });
        
        robotModel.allMotorsOff();
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        goalControllerThread.start();
        robotModel.setRoller(true);
        robotModel.setSpiral(true);
    }
}
