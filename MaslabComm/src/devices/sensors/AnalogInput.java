package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class AnalogInput extends Sensor {
	private byte signalPin = 0;
	protected float value = 0;

	/*
	 * Takes one analog-in pin (labeled on the Maple as "AIN")
	 */
	public AnalogInput(int i) {
		this.signalPin = (byte) i;
	}
	
	@Override
	public byte getDeviceCode() {
		return 'A';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {signalPin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		byte msb = buff.get();
		byte lsb = buff.get();
		value = (Math.abs(msb) * 256) + Math.abs(lsb);
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}

	public float getValue() {
		return value;
	}

}
