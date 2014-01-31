package stateMachine;
import org.opencv.core.Mat;

import robotModel.*;

public class SorterStateController {
    private final Devices robotModel;
    private final RobotInventory robotInventory;
    
    
    public SorterStateController(Devices robotModel, RobotInventory robotInventory){
        this.robotModel = robotModel;
        this.robotInventory = robotInventory;
    }


    public void controlState() {
        // if there is a ball in the sorter mechanism
        if(robotModel.isBallInSorter()){
            TimedBall ballToSort = robotInventory.ballToBeSorted();
            long timeSincePickup = System.currentTimeMillis() - ballToSort.getPickupTime();
            BallColor colorOfBall = ballToSort.getBallColor();
            final long expirationTime = 8000;
            System.out.println("Ball in sorter");
            
            // the ball is unknown
            if(colorOfBall == BallColor.NONE){
                System.out.println("Sorting unknown");
                sortUnknown();
            }
            // if ball expired then call control state again to get next ball in queue
            else if(timeSincePickup >= expirationTime){
                System.out.println("Expired ball");
                controlState();
            }
            else if(colorOfBall == BallColor.RED){
                System.out.println("Sorting red");
                sortRed();
            }
            else if(colorOfBall == BallColor.GREEN){
                System.out.println("Sorting green");
                sortGreen();
            }
        }
    }
    
    
    private void sortRed(){ 
        try {
            Thread.sleep(900);
            robotModel.setServoSorterToRedPosition();
            Thread.sleep(900);
            robotModel.setServoSorterToCenterPosition();
            Thread.sleep(900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
        
        robotInventory.addRedBall();
    }
    
    
    private void sortGreen(){ 
        try {
            Thread.sleep(900);
            robotModel.setServoSorterToGreenPosition();
            Thread.sleep(900);
            robotModel.setServoSorterToCenterPosition();
            Thread.sleep(900);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
        
        robotInventory.addGreenBall();
    }
    
    
    private void sortUnknown(){
        try {
            Thread.sleep(700);
            robotModel.setServoSorterToRedPosition();
            Thread.sleep(600);
            robotModel.setServoSorterToCenterPosition();
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
        
        robotInventory.addUnknownBall();
    }

}
