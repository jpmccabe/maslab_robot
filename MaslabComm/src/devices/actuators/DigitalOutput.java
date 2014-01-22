package devices.actuators;

import devices.Actuator;

public class DigitalOutput extends Actuator {
	private byte pin;
	private byte value;
	
	/*
	 * Takes one digital pin
	 */
	public DigitalOutput(int pin) {
		this.pin = (byte) pin;
	}

	@Override
	public byte getDeviceCode() {
		return 'd';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {pin};
	}

	@Override
	public byte[] generateCommandToMaple() {
		return new byte[] {value};
	}
	
	public void setValue(boolean value) {
		this.value = value ? (byte) 1 : (byte) 0;
	}

}
