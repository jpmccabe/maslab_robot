package devices.actuators;

import devices.Actuator;

public class PWMOutput extends Actuator {
	private byte pin;
	private int value;
	
	/*
	 * Takes one PWM pin (labeled on the Maple as "PWM")
	 */
	public PWMOutput(int pin) {
		this.pin = (byte) pin;
	}

	@Override
	public byte getDeviceCode() {
		return 'P';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {pin};
	}

	@Override
	public byte[] generateCommandToMaple() {
		return new byte[] { (byte) ((value >> 8) & 0xff),
				(byte) (value & 0xff) };
	}
	
	public void setValue(double value) {
		this.value = (int) (value * 65535);
	}

}
