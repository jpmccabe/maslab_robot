package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Encoder extends Sensor {
	
	private static final int GEAR_REDUCTION = 29;
	private static final int TICKS_PER_REV = 16; // count only rising edge of pin A.
	                                             // if count rising and falling of A and B,
	                                             // then ticks_per_rev = 64
	byte pinA;
	byte pinB;
	
	int ticks;
	int deltaTicks;
	long lastUpdateTime;
	long deltaTime;
	
	/*
	 * Takes two digital pins.
	 *
	 * The Maple will place an interrupt on pin A.
	 * Be aware that the Maple has a total of 16 interrupt lines,
	 * and their website details which interrupt pins you can't use together.
	 */
	public Encoder(int pinA, int pinB) {
		this.pinA = (byte) pinA;
		this.pinB = (byte) pinB;
		deltaTicks = 0;
		ticks = 0;
		lastUpdateTime = System.nanoTime();
		deltaTime = 0;
	}
	
	@Override
	public byte getDeviceCode() {
		return 'N';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {pinA, pinB};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		long currentTime = System.nanoTime();
		byte msb = buff.get();
		byte lsb = buff.get();
		deltaTicks = (msb * 256) + ((int) lsb & 0xff);
		ticks += deltaTicks;
		deltaTime = currentTime - lastUpdateTime;
		lastUpdateTime = currentTime;
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	// in radians
	public double getTotalAngularDistance() {
		return (double) (2.0 * Math.PI * ticks / (GEAR_REDUCTION * TICKS_PER_REV));
	}
	
	// in radians
	public double getDeltaAngularDistance() {
		return (double) (2.0 * Math.PI * deltaTicks / (GEAR_REDUCTION * TICKS_PER_REV));
	}
	
	// in radians per second
	public double getAngularSpeed() {
		return (double) (1000000000.0 * getDeltaAngularDistance() / deltaTime);
	}

}
