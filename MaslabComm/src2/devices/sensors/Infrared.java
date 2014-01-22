package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Infrared extends Sensor {
	private byte signalPin = 0;
	private float distance = 0;

	public Infrared(int i) {
		this.signalPin = (byte) i;
	}
	
	@Override
	protected byte getDeviceCode() {
		return 'A';
	}

	@Override
	protected byte[] getInitializationBytes() {
		return new byte[] {signalPin};
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
