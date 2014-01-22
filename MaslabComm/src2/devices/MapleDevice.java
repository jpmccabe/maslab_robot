package devices;

import java.nio.ByteBuffer;

public abstract class MapleDevice {

	// Generate the INIT signal
	public byte[] generateInitMessage() {
		byte[] pins = getInitializationBytes();
		byte[] out = new byte[pins.length + 1];
		out[0] = getDeviceCode();
		System.arraycopy(pins, 0, out, 1, pins.length);
		return out;
	}
	
	abstract protected byte getDeviceCode();
	
	abstract protected byte[] getInitializationBytes();

	// Generates data for output stream (from Java to Maple)
	abstract public byte[] generateCommandToMaple();

	// Consumes and stores local byte data from stream (from Maple to Java)
	abstract public void consumeMessageFromMaple(ByteBuffer buff);

	// Size of consume operation
	abstract public int expectedNumBytesFromMaple();

}
