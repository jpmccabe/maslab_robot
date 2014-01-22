package devices;


public abstract class Sensor extends MapleDevice {
	
	@Override
	public byte[] generateCommandToMaple() {
		return new byte[] { };
	}

}
