package devices.actuators;

import devices.Actuator;

public class PWMOut extends Actuator {
	private byte pin;
	private int value;
	
	public PWMOut(int pin) {
		this.pin = (byte) pin;
	}

	@Override
	protected byte getDeviceCode() {
		return 'a';
	}

	@Override
	protected byte[] getInitializationBytes() {
		return new byte[] {pin};
	}

	@Override
	public byte[] generateCommandToMaple() {
		return new byte[] { (byte) ((value >> 8) & 0xff),
				(byte) (value & 0xff) };
	}
	
	public void setValue(double value) {
		this.value = (int) (value * 32767);
	}

}
