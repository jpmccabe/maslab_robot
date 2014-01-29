package robotModel;

public class TimedBall {
    private final long pickupTime;
    private final BallColor ballColor;
    
    public TimedBall(long pickupTime, BallColor ballColor){
        this.pickupTime = pickupTime;
        this.ballColor = ballColor;
    }
    
    
    /**
     * @return the time when the ball was picked up
     */
    public long getPickupTime(){
        return pickupTime;
    }
    
    
    /**
     * @return the color of the ball
     */
    public BallColor getBallColor(){
        return ballColor;
    }
}
