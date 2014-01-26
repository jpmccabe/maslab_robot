package stateMachine;

public abstract class StateMachine {
    
    //Stops execution of the state machine. Do all clean up here.
    abstract public void stop();
    
    //Controls the state of the state machine
    abstract public void controlState();
    
    //Gets the state machine type
    abstract public StateMachineType getStateMachineType();
    
    //Returns true if state machine is done running
    abstract public boolean isDone();
}
