package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class DigitalInput extends Sensor {
	
	byte pin;
	boolean val;
	
	/*
	 * Takes one digital pin
	 */
	public DigitalInput(int pin) {
		this.pin = (byte) pin;
	}

	@Override
	public byte getDeviceCode() {
		return 'D';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {pin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		val = (buff.get() != 0);
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 1;
	}
	
	public boolean getValue() {
		return val;
	}

}
