package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Ultrasonic extends Sensor {
	private static final double CONVERSION_FACTOR = 340.29 / 2000000.0;
	private byte trig;
	private byte echo;
	private double distance;
	
	/*
	 * Takes two digital pins
	 */
	public Ultrasonic(int trig, int echo) {
		this.trig = (byte) trig;
		this.echo = (byte) echo;
	}

	@Override
	public byte getDeviceCode() {
		return 'U';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {trig, echo};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		byte msb = buff.get();
		byte lsb = buff.get();
		distance = ((((int) msb & 0xff) * 256) + ((int) lsb & 0xff)) * CONVERSION_FACTOR;
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	// in meters
	public double getDistance() {
		return distance;
	}

}
