package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Ultrasonic extends Sensor {
	private byte echo;
	private byte trig;
	private int distance;
	
	public Ultrasonic(int i, int j) {
		this.echo = (byte) i;
		this.trig = (byte) j;
	}

	@Override
	protected byte getDeviceCode() {
		return 'U';
	}

	@Override
	protected byte[] getInitializationBytes() {
		return new byte[] {echo, trig};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		byte msb = buff.get();
		byte lsb = buff.get();
		// this is incorrect; ignores the conversion
		distance = (msb * 256) + lsb;
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	public float getDistance() {
		return distance;
	}

}
