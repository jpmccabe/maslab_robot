package robotModel;

import java.util.LinkedList;
import java.util.Queue;

public class RobotInventory {
    
    private int numRedBallsOnBoard;
    private int numGreenBallsOnBoard;
    private int numUnknownBallsOnBoard;
    private Queue<TimedBall> unsortedBalls = new LinkedList<TimedBall>();
    
    
    public RobotInventory(){
        numRedBallsOnBoard = 0;
        numGreenBallsOnBoard = 0;
        numUnknownBallsOnBoard = 0;
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
     * Adds on ball to the number of unknown color
     * balls stored on the robot
     */
    synchronized public void addUnknownBall(){
        numUnknownBallsOnBoard++;
    }
    
    /**
     * add the ball color to the unsorted balls queue 
     * unsorted balls on the first channel from spiral tube window
     */
    synchronized public void addBallToQueue(TimedBall timedBall){
        unsortedBalls.add(timedBall);   
    }
    
    /**
     * returns the next ball in the to be sorted queue
     * @return Timed ball of next ball to be sorted. Timed ball will 
     * have color of BallColor.NONE if there is not a ball in the queue.
     */
    synchronized public TimedBall ballToBeSorted(){
        TimedBall ballToReturn = new TimedBall(0,BallColor.NONE);
        if(unsortedBalls.size()>0){ 
            ballToReturn =  unsortedBalls.remove();
        }
        
        return ballToReturn;
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
     * Sets the number of red balls on board to zero
     */
    synchronized public void removeRedBalls(){
        numRedBallsOnBoard = 0;
    }
    
    
    /**
     * Sets the number of unknown balls on board to zero
     */
    synchronized public void removeUnknownBalls(){
        numUnknownBallsOnBoard = 0;
    }
    
    /**
     * @return true if the robot has green balls on board, false otherwise.
     */
    synchronized public boolean hasGreenBalls(){
        return numGreenBallsOnBoard > 0;
    }
    
    
    /**
     * @return true if the robot has red balls on board, false otherwise.
     */
    synchronized public boolean hasRedBalls(){
        return numRedBallsOnBoard > 0;
    }
    
    
    /**
     * @return true if the robot has unknown colored balls on board, false otherwise.
     */
    synchronized public boolean hasUnknownBalls(){
        return numUnknownBallsOnBoard > 0;
    }
    
    
    /**
     * @return the number of green balls on board.
     */
    synchronized public int getNumGreenBalls(){
        return numGreenBallsOnBoard;
    }
    
    
    /**
     * @return the number of red balls on board.
     */
    synchronized public int getNumRedBalls(){
        return numRedBallsOnBoard;
    }
    
    
    /**
     * @return the number of unknown balls on board.
     */
    synchronized public int getNumUnknownBallsOnBoard(){
        return numUnknownBallsOnBoard;
    }
    
    
    /**
     * @return true if the green ball storage area on the robot
     * is full, false otherwise
     */
    synchronized public boolean isGreenStorageFull(){
        final int maxNumGreen = 4;
        return numGreenBallsOnBoard >= maxNumGreen;
    }
    
    
    /**
     * @return true if the red ball storage area on the robot
     * is full (this includes unknown colored balls), false otherwise
     */
    synchronized public boolean isRedStorageFull(){
        final int maxNumRed = 4;
        return (numRedBallsOnBoard + numUnknownBallsOnBoard) >= maxNumRed;
    }
}
