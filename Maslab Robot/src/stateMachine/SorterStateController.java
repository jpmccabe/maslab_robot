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
        	try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            TimedBall ballToSort = robotInventory.ballToBeSorted();
            long timeSincePickup = System.currentTimeMillis() - ballToSort.getPickupTime();
            BallColor colorOfBall = ballToSort.getBallColor();
            final long expirationTime = 15000;
            System.out.println("ball in sorter");
            
            // if that ball has not expired
            if(timeSincePickup <= expirationTime || colorOfBall == BallColor.NONE){
                // if a ball sneaked in then assume it is red 
                if(colorOfBall == BallColor.NONE || colorOfBall == BallColor.RED){
                	System.out.println("Sorting red");
                    sortRed();
                }
                else if(colorOfBall == BallColor.GREEN){
                	System.out.println("Sorting green");
                	
                    sortGreen();
                }
            } else{
                controlState();
            }
        }
    }
    
    
    private void sortRed(){ 
        try {
            robotModel.setServoSorterToRedPosition();
            Thread.sleep(600);
            robotModel.setServoSorterToCenterPosition();
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
        
        robotInventory.addRedBall();
    }
    
    
    private void sortGreen(){ 
        try {
            robotModel.setServoSorterToGreenPosition();
            Thread.sleep(600);
            robotModel.setServoSorterToCenterPosition();
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }  
        
        robotInventory.addGreenBall();
    }

}
