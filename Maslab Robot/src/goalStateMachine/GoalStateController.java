package goalStateMachine;

import org.opencv.core.Mat;

import stateMachine.*;
import camera.*;
import robotModel.*;

public class GoalStateController{
    
    private final Devices robotModel;
    private final Camera camera;
    private final CameraGUI redBallProcessedImageGUI;
    private final ComputerVisionSummary summaryOfImage;
    private StateMachine currentStateController;

    public GoalStateController(Devices robotModel, Camera camera){
        this.currentStateController = new StopStateController();
        this.robotModel = robotModel;
        this.camera = camera;
        this.redBallProcessedImageGUI = new CameraGUI(camera);
        summaryOfImage = new ComputerVisionSummary();
    }
    
    
    
    private void roam(){
        
    }
    
    
    
    private void avoidWalls(){
        currentStateController.stop();
        final AvoidWallStateController avoidWallController = 
                new AvoidWallStateController(robotModel,camera);
        
        currentStateController = avoidWallController;
        
        Thread avoidWallThread = new Thread(new Runnable(){
            public void run(){
                avoidWallController.controlState();
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
                new BallCollectionStateController(robotModel, camera);
        
        currentStateController = ballCollectionController;
        
        Thread ballCollectionThread = new Thread(new Runnable(){
            public void run(){
                while(!ballCollectionController.isDone()){
                    ballCollectionController.controlState();
                }
            }
        });
        
        ballCollectionThread.start();
    }
    
    
    
    private void depositRedBalls(){
        
    }
    
    
    
    public void controlState(){
        long startTime = System.nanoTime();

        final Mat image = camera.getLastFrame();
        summaryOfImage.updateFullSummary(image);
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
            //collectGroundBalls();
        }
        
        // if see reactor and have green balls then score
        // if see interface wall and have red balls then score over wall
        // if see energy silo then collect ball
        
        long estimatedTime = (System.nanoTime() - startTime);
        System.out.println(estimatedTime);
    }

    
    
    public static void main(String args[]){
        final Devices robotModel = new Devices();
        final Camera camera = new Camera();
        final GoalStateController goalController = new GoalStateController(robotModel, camera);
        
        //final CameraGUI cameraGUI = new CameraGUI(camera);
        
        Thread cameraUpdateThread = new Thread(new Runnable(){
            public void run(){
                while(true){
                    camera.readNewFrame();
                    //cameraGUI.updateImagePane(camera.getLastFrame());
                }
            }
        });
        
        Thread goalControllerThread  = new Thread(new Runnable(){
            public void run(){
                while(true){
                    goalController.controlState();
                }
            }
        });
        
        cameraUpdateThread.start();
        goalControllerThread.start();
        robotModel.allMotorsOff();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robotModel.setRoller(true);
        robotModel.setSpiral(true);
    }
}
