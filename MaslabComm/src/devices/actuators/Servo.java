package devices.actuators;

import devices.Actuator;

public class Servo extends Actuator {
	private final byte pin;
	private final int minPulseWidth;
	private final int maxPulseWidth;
	private final int minAngle;
	private final int maxAngle;
	
	private int pulseWidth;
	
	/* Takes one PWM pin, and a bunch of parameters.
	 * When you use the setAngle() function, use angles between minAngle and maxAngle.
	 * Look in the ServoXXX subclasses to see the (approximate) angle ranges
	 * for different servo models.
	 */
	public Servo(int pin, int minPulseWidth, int maxPulseWidth, int minAngle, int maxAngle) {
		this.pin = (byte) pin;
		this.minPulseWidth = minPulseWidth;
		this.maxPulseWidth = maxPulseWidth;
		this.minAngle = minAngle;
		this.maxAngle = maxAngle;
	}

	@Override
	public byte getDeviceCode() {
		return 'V';
	}

	@Override
	public byte[] getInitializationBytes() {
		byte mipwMSB = (byte) ((minPulseWidth >> 8) & 0xff);
		byte mipwLSB = (byte) (minPulseWidth & 0xff);
		byte mapwMSB = (byte) ((maxPulseWidth >> 8) & 0xff);
		byte mapwLSB = (byte) (maxPulseWidth & 0xff);
		byte miaMSB = (byte) ((minAngle >> 8) & 0xff);
		byte miaLSB = (byte) (minAngle & 0xff);
		byte maaMSB = (byte) ((maxAngle >> 8) & 0xff);
		byte maaLSB = (byte) (maxAngle & 0xff);
		return new byte[] {pin, mipwMSB, mipwLSB, mapwMSB, mapwLSB, miaMSB, miaLSB, maaMSB, maaLSB};
	}

	@Override
	public byte[] generateCommandToMaple() {
		byte out[] = new byte[] { (byte) ((pulseWidth >> 8) & 0xff), (byte) (pulseWidth & 0xff) };
		return out;
	}
	
	// Angle is in degrees. Different devices (subclasses) have different angle ranges.
	public void setAngle(double angle) {
		double scaledAngle = (angle - minAngle) / (maxAngle - minAngle);
		scaledAngle = Math.max(scaledAngle, minAngle);
		scaledAngle = Math.min(scaledAngle, maxAngle);
		pulseWidth = (int) (minPulseWidth + scaledAngle*(maxPulseWidth - minPulseWidth));
		System.out.println("WIDTH: " + pulseWidth);
	}
	
	public double getMinAngle() {
		return minAngle;
	}
	
	public double getMaxAngle() {
		return maxAngle;
	}

}
