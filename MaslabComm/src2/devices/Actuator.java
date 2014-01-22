package devices;

import java.nio.ByteBuffer;


public abstract class Actuator extends MapleDevice {

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) { }

	@Override
	public int expectedNumBytesFromMaple() {
		return 0;
	}

}
