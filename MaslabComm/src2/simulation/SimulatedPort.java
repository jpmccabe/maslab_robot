package simulation;

import jssc.SerialPort;

import comm.MapleIO;

public class SimulatedPort extends SerialPort {
	public SimulatedPort() {
		super("");
	}

	public byte[] readBytes() {
		byte[] data = new byte[] {MapleIO.RESPONSE_SIGNAL, 0, 10, 2, MapleIO.END_SIGNAL};
		System.out.println("Receiving: " + formatBytes(data));
		return data;
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
		for (byte b : data) {
			str += formatByte(b) + " ";
		}
		return str;
	}
}
