package devices.actuators;

import devices.Actuator;


public class Cytron extends Actuator {

	private byte dir = 0;
	private byte pwm = 0;
	private int speed = 0;

	/*
	 * Takes one digital and one PWM pin (labeled on the Maple as "PWM")
	 */
	public Cytron(int dir_pin, int pwm_pin) {
		this.dir = (byte) dir_pin;
		this.pwm = (byte) pwm_pin;
	}
	
	@Override
	public byte getDeviceCode() {
		return 'C';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[]{dir, pwm};
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
