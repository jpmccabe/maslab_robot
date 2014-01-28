package comm;


import devices.actuators.Cytron;
import devices.actuators.Servo;
import devices.actuators.Servo3001HB;
import devices.actuators.Servo6001HB;
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
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		/*
		 * Create an object for each device. The constructor arguments specify
		 * their pins (or, in the case of the gyroscope, the index of a fixed
		 * combination of pins).
		 * Devices are generally either Sensors or Actuators. For example, a
		 * motor controller is an actuator, and an encoder is a sensor.
		 */
		Servo servo1 = new Servo6001HB(0);

		/*
		 * Build up a list of devices that will be sent to the Maple for the
		 * initialization step.
		 */
		comm.registerDevice(servo1);

		// Send information about connected devices to the Maple
		comm.initialize();

		double angle = servo1.getMinAngle();
		
		while (true) {

			// Request sensor data from the Maple and update sensor objects accordingly
			//comm.updateSensorData();
			
			// All sensor classes have getters.
			
			// All actuator classes have setters.
			servo1.setAngle(angle);

			// Request that the Maple write updated values to the actuators
			comm.transmit();
			
			angle += (servo1.getMaxAngle() - servo1.getMinAngle()) / 4;
			if (angle > servo1.getMaxAngle()) {
				angle = servo1.getMinAngle();
			}
			
			// Just for console-reading purposes; don't worry about timing
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { }
			
//			comm.updateSensorData();
		}
	}
}
