package comm;


import devices.actuators.Cytron;
import devices.sensors.Encoder;
import devices.sensors.Gyroscope;
import devices.sensors.Ultrasonic;

public class Test {

	public static void main(String[] args) {
		new Test();
		System.exit(0);
	}

	public Test() {
		
		/*
		 * Create your Maple communication framework by specifying what kind of 
		 * serial port you would like to try to autoconnect to.
		 */
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.SIMULATION);
		//MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		/*
		 * Create an object for each device. The constructor arguments specify
		 * their pins (or, in the case of the gyroscope, the index of a fixed
		 * combination of pins).
		 * Devices are generally either Sensors or Actuators. For example, a
		 * motor controller is an actuator, and an encoder is a sensor.
		 */
//		Cytron motor1 = new Cytron(4, 5);
//		Cytron motor2 = new Cytron(6, 7);
		Ultrasonic ultra1 = new Ultrasonic(13, 12);
		Ultrasonic ultra2 = new Ultrasonic(36, 34);
//		Gyroscope gyro = new Gyroscope(1, 9);
//		Encoder enc = new Encoder(2, 3);

		/*
		 * Build up a list of devices that will be sent to the Maple for the
		 * initialization step.
		 */
//		comm.registerDevice(motor1);
//		comm.registerDevice(motor2);
		comm.registerDevice(ultra1);
//		comm.registerDevice(ultra2);
//		comm.registerDevice(gyro);
//		comm.registerDevice(enc);

		// Send information about connected devices to the Maple
		comm.initialize();

		while (true) {
			
			// Request sensor data from the Maple and update sensor objects accordingly
			comm.updateSensorData();
			
			// All sensor classes have getters.
			//System.out.println(gyro.getOmega() + " " + ultra1.getDistance());
			System.out.println(ultra1.getDistance() + " " + ultra2.getDistance());
			//System.out.println(enc.getTotalAngularDistance() + " " + enc.getAngularSpeed());
			
			// All actuator classes have setters.
			//motor1.setSpeed(0.2);
			//motor2.setSpeed(-0.3);

			// Request that the Maple write updated values to the actuators
			//comm.transmit();
			
			// Just for console-reading purposes; don't worry about timing
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
	}
}
