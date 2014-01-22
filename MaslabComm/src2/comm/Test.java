package comm;

import devices.actuators.Cytron;
import devices.sensors.Infrared;
import devices.sensors.Ultrasonic;

public class Test {
	public static void main ( String[] args ) {
		new Test();
		System.exit(0);
	}
	
	public Test() {
		//MapleComm comm = new MapleComm(MapleIO.SerialPortType.SIMULATION);
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);
		
		Infrared infra1 = new Infrared(1);
		Infrared infra2 = new Infrared(2);
		Cytron motor1 = new Cytron(4, 5);
		Cytron motor2 = new Cytron(6, 7);
		
		comm.registerDevice(infra1);
		comm.registerDevice(infra2);
		comm.registerDevice(motor1);
		comm.registerDevice(motor2);
		
		System.out.println("Initializing");
		comm.initialize();
		
		while (true) {
			comm.updateSensorData();
			System.out.println(infra1.getDistance() + " " + infra2.getDistance());
			
			motor1.setSpeed(0.5);
			motor2.setSpeed(-0.5);
			
			comm.transmit();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
	}
}
