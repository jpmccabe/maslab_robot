package goalStateMachine;

import ballCollectionStateMachine.*;
import camera.*;
import robotModel.*;

public class GoalStateController {
    
    private GoalState goalState;
    private final Devices robotModel;
    private final Camera camera;

    public GoalStateController(Devices robotModel, Camera camera){
        goalState = GoalState.ROAM;
        this.robotModel = robotModel;
        this.camera = camera;
    }
    
    
    private void roam(){
        
    }
    
    private void score(){
        
    }
    
    private void collectFromEnergySilo(){
        
    }
    
    private void collectGroundBalls(){
        BallCollectionStateController ballCollectionController = 
                new BallCollectionStateController(robotModel, camera);
    }
    
    private void depositRedBalls(){
        
    }
    
    public GoalState getState(){
        return goalState;
    }

    public static synchronized void main (String args[]) {

        // calling threads and starting them
        Thread camera= new Thread(new Camera());
        Thread cameraProcessor1= new Thread(new CameraProcessor1());
        Thread cameraProcessor2= new Thread(new CameraProcessor2());
        Thread cameraProcessor3= new Thread(new CameraProcessor3());        

        camera.start();     
        devices.setMotors(0,0);
        devices.setRoller(false);
        devices.setSpiral(false);

        StopWatch.timeOut(2000);
        cameraProcessor1.start();
        cameraProcessor2.start();
        cameraProcessor3.start();
        StopWatch.timeOut(100);

        //state machine booleans
        boolean ballFollow=true;
        boolean wallFollow=false;
        boolean collectBall=false;
        boolean wallAhead=false;
        boolean liftBall=false;

        //set up driver
        Driver driver= new Driver();
        driver.setPID();
        //a while loop that runs for 2 minutes
        while(true){
            /*
            System.out.println(Global.wallClosness);
            System.out.println("ballFollow:"+ ballFollow );
            System.out.println("wallFollow:"+ wallFollow );
            System.out.println("collectBall:"+ collectBall );
            System.out.println("wallAhead:"+ wallAhead );
            System.out.println("liftBall:"+ liftBall);
             */
            if(Global.wallClosness<10) wallAhead=true;
            else wallAhead=false;


            if (wallAhead==true){
                devices.setMotors(0.1, 0);
            }
            if(wallFollow==true){

            }
            if(ballFollow==true){
                if (/*Global.RedOrGreen==0 &&*/ Math.min(Global.distance2Red,Global.distance2Green)<20){
                    if (Global.distance2Red<Global.distance2Green){ Global.RedOrGreen=1; System.out.println("Red");}
                    else{ Global.RedOrGreen=2; System.out.println("Green");}
                }
                double angle2Ball=0;
                double distance2Ball=0;
                if(Global.RedOrGreen==1){ angle2Ball= Global.angle2Red; distance2Ball= Global.distance2Red; }
                if(Global.RedOrGreen==2){ angle2Ball= Global.angle2Green; distance2Ball= Global.distance2Green; }

                /*if (Math.abs(angle2Ball)<0.3 && distance2Ball<6){
                    ballFollow=false; 
                    collectBall=true; 
                    //Global.timer1=System.nanoTime();
                    devices.setMotors(0,0);
                }*/

                driver.driveToBall(distance2Ball,5,angle2Ball,0);
            }
            if(collectBall==true){
                devices.setRoller(true);
                devices.setMotors(0.09, 0.09);
                StopWatch.timeOut(2000);
                devices.setRoller(false);
                devices.setMotors(0.0, 0.0);
                collectBall=false;
                ballFollow=false;
                liftBall=true;
            }

            if(liftBall==true){
                devices.setSpiral(true);
            }

        }
    }
}
