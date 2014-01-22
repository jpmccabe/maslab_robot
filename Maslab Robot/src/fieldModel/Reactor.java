package fieldModel;


/**
 * Represents the state of a reactor on the field.
 */
public class Reactor {
    
    private final ReactorNumber reactorNumber;
    private int ballsScoredInTop;
    private int ballsScoredInBottom;
    
    private final static int POINTS_FOR_UNIQUE_REACTOR = 10;
    private final static int POINTS_FOR_BOTH_PORTS = 5;
    private final static int POINTS_PER_BALL_IN_TOP = 7;
    private final static int POINTS_PER_BALL_IN_BOTTOM = 3;
    
    /**
     * Constructs a new reactor
     * @param number a unique number to identify this reactor
     *        from the others.
     */
    public Reactor(ReactorNumber number){
        reactorNumber = number;
        ballsScoredInTop = 0;
        ballsScoredInBottom = 0;
    }
    
    
    
    /**
     * Adds balls to the number in the top of the reactor.
     * @param numberToAdd amount to add
     */
    public void addBallsToTopPort(int numberToAdd){
        ballsScoredInTop += numberToAdd;
    }
    
   
    
    /**
     * Adds balls to the number in the bottom of the reactor.
     * @param numberToAdd amount to add
     */
    public void addBallsToBottomPort(int numberToAdd){
        ballsScoredInBottom += numberToAdd;
    }
    
    
    
    /**
     * @return the ReactorNumber the object was constructed with
     */
    public ReactorNumber getReactorNumber(){
        return reactorNumber;
    }
    
    
    
    /**
     * @return the number of balls in the top port
     */
    public int getBallsInTopPort(){
        return ballsScoredInTop;
    }
   
    
    
    /**
     * @return the number of balls in the bottom port
     */
    public int getBallsInBottomPort(){
        return ballsScoredInBottom;
    }
    
    
    
    /**
     * Calculates the contribution this reactor has to our
     * overall score.
     * @return the contribution to score in points
     */
    public int getScoreContribution(){
        int totalBalls = ballsScoredInTop + ballsScoredInBottom;
        
        int pointsForUniqueReactor = (totalBalls > 0) ? POINTS_FOR_UNIQUE_REACTOR : 0;
        int pointsForScoringInBothPorts = (ballsScoredInTop > 0 && ballsScoredInBottom > 0) ? POINTS_FOR_BOTH_PORTS : 0;
        int pointsForBottomPort = ballsScoredInBottom * POINTS_PER_BALL_IN_BOTTOM;
        int pointsForTopPort = ballsScoredInTop * POINTS_PER_BALL_IN_TOP;
        
        int totalPoints = pointsForUniqueReactor + pointsForScoringInBothPorts +
                          pointsForBottomPort + pointsForTopPort;
        return totalPoints;
    }
}
