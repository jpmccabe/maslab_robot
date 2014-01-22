package devices.actuators;

import devices.Actuator;

public class DigitalOut extends Actuator {
	private byte pin;
	private byte value;
	
	public DigitalOut(int pin) {
		this.pin = (byte) pin;
	}

	@Override
	protected byte getDeviceCode() {
		return 'd';
	}

	@Override
	protected byte[] getInitializationBytes() {
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
