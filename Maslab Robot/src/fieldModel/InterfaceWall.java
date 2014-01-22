package fieldModel;

public class InterfaceWall {
    
    private int numberOfRedsPlacedOver;
    
    private final static int POINTS_PER_RED_BALL = 5;
    
    public InterfaceWall(){
        numberOfRedsPlacedOver = 0;
    }
    
    
    /**
     * Adds the given number of red balls to the total
     * count of those placed over the interface wall.
     * @param numberToAdd amount to add
     */
    public void addRedsOverWall(int numberToAdd){
        numberOfRedsPlacedOver += numberToAdd;
    }
    
    
    /**
     * @return the number of red balls placed over the 
     * interface wall
     */
    public int getRedsOverWall(){
        return numberOfRedsPlacedOver;
    }
    
    
    /**
     * Calculates the contribution to our score that the
     * interface wall has.
     * @return
     */
    public int getScoreContribution(){
        return (numberOfRedsPlacedOver * POINTS_PER_RED_BALL);
    }
    
}
