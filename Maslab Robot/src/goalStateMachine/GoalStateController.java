package goalStateMachine;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

//import BotClient.BotClient;
import stateMachine.*;
import camera.*;
import robotModel.*;

public class GoalStateController{
    
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    private final CameraGUI GUI;
    private final ComputerVisionSummary summaryOfImage;
    private StateMachine currentStateController;
    private final VideoCapture camera;
    private Mat lastFrame;


    public GoalStateController(){
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        this.currentStateController = new StopStateController();
        this.robotModel = new Devices();
        this.robotInventory = new RobotInventory();
        robotInventory.addRedBall();
        robotInventory.addRedBall();
        robotInventory.addGreenBall();
        robotInventory.addGreenBall();
        
        summaryOfImage = new ComputerVisionSummary();
         
        lastFrame = new Mat();
        // Setup the camera
        camera = new VideoCapture();
        camera.open(0);
        this.GUI = new CameraGUI(640,480);

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
                 System.gc();
             }
            }
        });
        
        
        Thread ballSortingThread = new Thread(new Runnable(){
            public void run(){
                SorterStateController sorter = new SorterStateController(robotModel, robotInventory);
                while(true){
                    sorter.controlState();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        cameraReadThread.start();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        robotModel.allMotorsOff();
        robotModel.setServoArmToUpPosition();
        robotModel.setServoSorterToCenterPosition();
        robotModel.setServoReleaseToScoreLowerPosition();
        ballSortingThread.start();
    }
    
    
    
    private void roam(){
        
    }
    
    
    private void lookForBalls(){
        currentStateController.stop();
        final LookForBallsStateController lookForBallsController = 
                new LookForBallsStateController(robotModel);
        
        currentStateController = lookForBallsController;
        
        Thread lookForBallThread = new Thread(new Runnable(){
            public void run(){
                while(!lookForBallsController.isDone()){
                    lookForBallsController.controlState(lastFrame);
                }
            }
        });
        
        lookForBallThread.start();
    }
    
    
    
    private void avoidWalls(){
        currentStateController.stop();
        ObstacleDirection direction = summaryOfImage.getObstacle();
        final AvoidWallStateController avoidWallController = 
                new AvoidWallStateController(robotModel, direction);
        
        currentStateController = avoidWallController;
        
        Thread avoidWallThread = new Thread(new Runnable(){
            public void run(){
                while(!avoidWallController.isDone()){
                    avoidWallController.controlState(lastFrame);
                }
            }
        });
        
        avoidWallThread.start();
    }
    
    
    
    private void scoreInReactor(){
        currentStateController.stop();
        final ScoreInReactorStateController scoreInReactorController = 
                new ScoreInReactorStateController(robotModel, robotInventory);
        
        currentStateController = scoreInReactorController;
        
        Thread scoreInReactorThread = new Thread(new Runnable(){
            public void run(){
                while(!scoreInReactorController.isDone()){
                    scoreInReactorController.controlState(lastFrame);
                }
            }
        });
        
        scoreInReactorThread.start();
    }
    
    
    
    private void collectFromEnergySilo(){
        currentStateController.stop();
        final CollectFromSiloStateController collectFromSiloController =
                new CollectFromSiloStateController(robotModel, robotInventory);
        
        currentStateController = collectFromSiloController;
        
        Thread collectFromSiloThread = new Thread(new Runnable(){
            public void run(){
                while(!collectFromSiloController.isDone()){
                    collectFromSiloController.controlState(lastFrame);
                }
            }
        });
        
        collectFromSiloThread.start();
    }
    
    
    
    private void collectGroundBalls(){
        currentStateController.stop();
        final BallCollectionStateController ballCollectionController = 
                new BallCollectionStateController(robotModel, robotInventory);
        
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
        currentStateController.stop();
        final ScoreOverInterfaceWallStateController interfaceWallController = 
                new ScoreOverInterfaceWallStateController(robotModel, robotInventory);
        
        currentStateController = interfaceWallController;
        
        Thread interfaceWallThread = new Thread(new Runnable(){
            public void run(){
                while(!interfaceWallController.isDone()){
                    interfaceWallController.controlState(lastFrame);
                }
            }
        });
        
        interfaceWallThread.start();
    }
    
    
    
    public void controlState(){
        long startTime = System.nanoTime();
        summaryOfImage.updateFullSummary(lastFrame);
        GUI.updateImagePane(summaryOfImage.getInterfaceWallProcessedImage());
        
                     
        // if a reactor is in view and we have green balls, and we are not currently scoring, then score.
        if((summaryOfImage.isReactorScoreable() && robotInventory.hasGreenBalls()) || 
                (currentStateController.getStateMachineType() == StateMachineType.SCORE_IN_REACTOR && !currentStateController.isDone())){
            if(!(currentStateController.getStateMachineType() == StateMachineType.SCORE_IN_REACTOR &&
                    !currentStateController.isDone())){
                System.out.println("Score in reactor");
                scoreInReactor();
            }
        }
        else if((summaryOfImage.isInterfaceWallScoreable() && 
                (robotInventory.hasRedBalls() || robotInventory.isRedStorageFull())) ||  
                (currentStateController.getStateMachineType() == StateMachineType.DEPOSIT_RED_BALLS && !currentStateController.isDone())){
            if(!(currentStateController.getStateMachineType() == StateMachineType.DEPOSIT_RED_BALLS &&
                    !currentStateController.isDone())){
                System.out.println("Score over interface wall");
                depositRedBalls();
            }
        }
        // else if a wall is close, and we are not currently avoiding walls, then avoid walls
        else if(summaryOfImage.isObstacle()){
        	if(!(currentStateController.getStateMachineType() == StateMachineType.AVOID_WALLS && 
                    !currentStateController.isDone())){
        		System.out.println("Avoiding walls");
        		avoidWalls();
        	}
        }      
        // else if see ball, no storage is full, and not currently collecting one, then collect ball
        else if(!robotInventory.isGreenStorageFull() && !robotInventory.isRedStorageFull() &&
                (summaryOfImage.isGreenBall() || summaryOfImage.isRedBall())){
            if(!(currentStateController.getStateMachineType() == StateMachineType.COLLECT_GROUND_BALLS
                    && !currentStateController.isDone())){
                System.out.println("Collecting balls");
                collectGroundBalls();
            }
        }
        // else look for balls
        else if(currentStateController.getStateMachineType() != StateMachineType.LOOK_FOR_BALLS &&
                currentStateController.isDone()){
            System.out.println("Looking for balls");
            lookForBalls();
        }
        
        long estimatedTime = (System.nanoTime() - startTime);
        //System.out.println(estimatedTime);
    }

    
    
    public static void main(String args[]){  
        
        /*BotClient botclient = new BotClient("18.150.7.174:6667","1221",false);
        while( !botclient.gameStarted() ) {
        }
        botclient.close();
        */
        
        final GoalStateController goalController = new GoalStateController();
  
        Thread goalControllerThread  = new Thread(new Runnable(){
            public void run(){
                while(true){
                    goalController.controlState();
                }
            }
        });
          
        goalControllerThread.start();
        
        /*
        final Devices robotModel = new Devices();
        robotModel.setServoSorterToCenterPosition();
        robotModel.setServoArmToUpPosition();
        final RobotInventory robotInventory = new RobotInventory();
        robotInventory.addBallToQueue(new TimedBall(System.currentTimeMillis(),BallColor.RED));
        Thread ballSortingThread = new Thread(new Runnable(){
            public void run(){
                SorterStateController sorter = new SorterStateController(robotModel, robotInventory);
                while(true){
                    sorter.controlState();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        ballSortingThread.start();
        */
    }
}
