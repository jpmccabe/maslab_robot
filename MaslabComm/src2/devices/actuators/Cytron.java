package devices.actuators;

import devices.Actuator;


public class Cytron extends Actuator {

	private byte pwm = 0, dir = 0;
	private int speed = 0;

	public Cytron(int pwm_pin, int dir_pin) {
		this.pwm = (byte) pwm_pin;
		this.dir = (byte) dir_pin;
	}
	
	@Override
	protected byte getDeviceCode() {
		return 'C';
	}

	@Override
	protected byte[] getInitializationBytes() {
		return new byte[]{pwm, dir};
	}

	@Override
	public byte[] generateCommandToMaple() {
		return new byte[] { (byte) ((speed >> 8) & 0xff),
				(byte) (speed & 0xff) };
	}

	public void setSpeed(double speed) {
		this.speed = (int) (speed * 32767);
	}
}
