package comm;

import jssc.SerialPort;


public class SimulatedPort extends SerialPort {
	public SimulatedPort() {
		super("");
	}

	public byte[] readBytes(int numBytes) {
		byte[] data = new byte[numBytes];
		for (int i = 0; i < numBytes; i++) {
			data[i] = MapleIO.END_SIGNAL;
		}
		System.out.println("Receiving: " + formatBytes(data));
		return data;
	}
	
	public byte[] readBytes() {
		return readBytes(1);
	}
	
	public boolean writeByte(byte b) {
		System.out.println("Sending: " + formatByte(b));
		return true;
	}

	public boolean writeBytes(byte[] data) {
		System.out.println("Sending: " + formatBytes(data));
		return true;
	}
	
	public static String formatByte(byte b) {
		String hex = Integer.toHexString(b);
		if (hex.length() > 2)
			hex = hex.substring(hex.length() - 2);
		if (hex.length() < 2)
			hex = "0" + hex;
		return "0x" + hex;
	}
	
	public static String formatBytes(byte[] data) {
		String str = "";
		if (data != null) {
			for (byte b : data) {
				str += formatByte(b) + " ";
			}
		}
		return str;
	}
}
