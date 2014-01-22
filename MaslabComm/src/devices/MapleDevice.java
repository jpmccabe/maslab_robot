package devices;

import java.nio.ByteBuffer;

public abstract class MapleDevice {
	
	// Return the single-byte code used to identify this device type to the Maple
	public abstract byte getDeviceCode();
	
	// Return the list of bytes the Maple is expecting to setup this device
	public abstract byte[] getInitializationBytes();

	// Generates data for output "SET" stream (from Java to Maple)
	abstract public byte[] generateCommandToMaple();

	// Consumes and stores local byte data from "GET" stream (from Maple to Java)
	abstract public void consumeMessageFromMaple(ByteBuffer buff);

	// Size of consume operation
	abstract public int expectedNumBytesFromMaple();

}
