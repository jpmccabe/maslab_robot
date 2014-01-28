package robotModel;

import java.util.LinkedList;
import java.util.Queue;

public class RobotInventory {
    
    private int numRedBallsOnBoard;
    private int numGreenBallsOnBoard;
    private Queue<String> unsortedBalls =new LinkedList<String>();
    
    
    public RobotInventory(){
        numRedBallsOnBoard = 0;
        numGreenBallsOnBoard = 0;
    }
    
    
    /**
     * Adds one ball to the number of green
     * balls stored on the robot
     */
    synchronized public void addGreenBall(){
        numGreenBallsOnBoard++;
        
        
    }
    
    
    /**
     * Adds one ball to the number of red
     * balls stored on the robot
     */
    synchronized public void addRedBall(){
        numRedBallsOnBoard++;
    }
    
    /**
     * add the ball color to the unsorted balls queue 
     * unsorted balls on the first channel from spiral tube window
     */
    synchronized public void addBallToQueue(String ballColor){
        unsortedBalls.add(ballColor);   
    }
    
    /**
     * add the ball color to the unsorted balls queue 
     * unsorted balls on the first channel from spiral tube window
     * @return 
     */
    synchronized public String ballToBeSorted(){
        if(unsortedBalls.size()>0) return unsortedBalls.remove();
        else return "empty";
    }
    
    
    /**
     * Subtracts the given number of green balls from the
     * on board green ball count.
     * @param numberToRemove number of green balls that
     * were removed from the robot.
     */
    synchronized public void removeGreenBalls(int numberToRemove){
        numGreenBallsOnBoard -= numberToRemove;
    }
    
    
    /**
     * Subtracts the given number of red balls from the
     * on board red ball count.
     * @param numberToRemove number of red balls that were
     * removed from the robot.
     */
    synchronized public void removeRedBalls(int numberToRemove){
        numRedBallsOnBoard -= numberToRemove;
    }
    
    
    /**
     * @return true if the robot has green balls on board, false otherwise.
     */
    synchronized boolean hasGreenBalls(){
        return numGreenBallsOnBoard > 0;
    }
    
    
    /**
     * @return true if the robot has red balls on board, false otherwise.
     */
    synchronized boolean hasRedBalls(){
        return numRedBallsOnBoard > 0;
    }
    
    
    /**
     * @return the number of green balls on board.
     */
    synchronized int getNumGreenBalls(){
        return numGreenBallsOnBoard;
    }
    
    
    /**
     * @return the number of red balls on board.
     */
    synchronized int getNumRedBalls(){
        return numRedBallsOnBoard;
    }
    
}
