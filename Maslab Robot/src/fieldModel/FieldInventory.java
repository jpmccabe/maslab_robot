package fieldModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an inventory of scored balls.
 */
public class FieldInventory {
    
    private final InterfaceWall interfaceWall;
    private final List<Reactor> reactors;
    
    public FieldInventory(){
        interfaceWall = new InterfaceWall();;
        reactors = new ArrayList<Reactor>();
    }
    
    
    /**
     * Adds a new reactor to the field inventory.
     * @param reactorNumber the unique number to identify this
     * new reactor. Requires that reactorNumber is not in the List
     * getReactorNumbers().
     */
    public void addReactor(ReactorNumber reactorNumber){
        reactors.add(new Reactor(reactorNumber));
    }
    
    
    /**
     * @return the unique reactor numbers for the currently defined
     * reactors
     */
    public List<ReactorNumber> getReactorNumbers(){
        List<ReactorNumber> reactorNumbers = new ArrayList<ReactorNumber>();
        
        for(Reactor reactor : reactors){
            reactorNumbers.add(reactor.getReactorNumber());
        }
        
        return reactorNumbers;
    }
    
    
    /**
     * @return the number of red balls placed over the interface wall
     */
    public int getNumBallsOverInterfaceWall(){
        return interfaceWall.getRedsOverWall();
    }
    
    
    /**
     * Returns the number of balls in the given reactor and port
     * @param reactorNumber the reactor 
     * @param port the port of the reactor
     * @return the number of balls in the port of the reactor. Returns zero
     *         if no reactor has the given reactorNumber.
     */
    public int getBallsInReactor(ReactorNumber reactorNumber, Port port){
        for(Reactor reactor : reactors){
            if(reactor.getReactorNumber() == reactorNumber){
                int numBalls = (port == Port.TOP) ? reactor.getBallsInTopPort() : reactor.getBallsInBottomPort();
                return numBalls;
            }
        }
        
        return 0;
    }
    
    
    /**
     * Updates the field inventory with the scoring of a given
     * number of red balls over the interface wall.
     * @param numberOfRedBalls amount of red balls scored over wall
     */
    public void scoreBallsOverInterfaceWall(int numberOfRedBalls){
        interfaceWall.addRedsOverWall(numberOfRedBalls);
    }
    
    
    /**
     * Updates the field inventory with the scoring of a given
     * number of green balls, in the given reactor, in the given port.
     * @param numBalls the number of balls being scored
     * @param reactorNumber the reactor being scored in
     * @param port the port of the reactor being scored in
     */
    public void scoreBallsInReactor(int numBalls, ReactorNumber reactorNumber, Port port){
        for(Reactor reactor : reactors){
            if(reactor.getReactorNumber() == reactorNumber){
                if(port == Port.TOP){
                    reactor.addBallsToTopPort(numBalls);
                } else{
                    reactor.addBallsToBottomPort(numBalls);
                }
            }
        }
    }
    
}
