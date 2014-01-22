package comm;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;
import simulation.SimulatedPort;

public class MapleIO {
	
	public static final byte INIT_SIGNAL = (byte) 'I';
	public static final byte START_SIGNAL = (byte) 'S';
	public static final byte RESPONSE_SIGNAL = (byte) 'S';
	public static final byte SNAPSHOT_SIGNAL = (byte) 0xfe;
	public static final byte END_SIGNAL = (byte) 0xff;
	
	public enum SerialPortType {
		LINUX, SIMULATION, WINDOWS
	}
	
	private SerialPort serialPort;
	private List<Byte> buffer = new ArrayList<Byte>();
	private int expectedInboundMessageSize;
	
	/*
	 * Connects to your device based on the input mode proposed during object
	 * construction.
	 */
	public void connect(SerialPortType portType) {
		String port = "";
		
		// Windows: auto-connect to COM port
		if (portType == SerialPortType.WINDOWS) {
			port = "COM";
		}

		// Linux: auto-connect to ACM port
		else if (portType == SerialPortType.LINUX) {
			port = "/dev/ttyACM";
		}

		// Simulation mode
		else if (portType == SerialPortType.SIMULATION) {
			System.err.println("RUNNING IN SIMULATION MODE.");
			serialPort = new SimulatedPort();
			return;
		}
		
		connect(port);
	}
	
	void connect(String port) {

		// Auto-connect to port of given type "port"
		int i = 0;
		for (i = 0; i < 20; i++) {
			try {
				serialPort = new SerialPort(port + i);
				serialPort.openPort();
				serialPort.setParams(115200, 8, 1, 0);
				break;
			} catch (SerialPortException ex) {
			}
		}
		if (i == 20) {
			System.err.println("Failed to auto-connect to serial port of type \"" + port + "\"");
			System.exit(-1);
		}

		System.out.println("Connected to serial port: " + port + i);
	}

	/*
	 * Clean-up protocol for serial port
	 */
	public void finalize() {
		try {
			if (serialPort != null && serialPort.isOpened()) {
				serialPort.closePort();
			}
		} catch (SerialPortException ex) {
			System.err.println(ex);
		}
	}
	
	public void setExpectedInboundMessageSize(int dataSize) {
		expectedInboundMessageSize = dataSize + 2;
	}
	
	public void sendInitMessage(ByteArrayOutputStream message) {
		try {
			serialPort.writeByte(INIT_SIGNAL);
			serialPort.writeBytes(message.toByteArray());
			serialPort.writeByte(END_SIGNAL);
		} catch (SerialPortException e) {
			System.err.println("Init message failed to send. [" + e + "]");
		}
	}
	
	public void sendCommand(ByteArrayOutputStream message) {
		try {
			serialPort.writeByte(START_SIGNAL);
			serialPort.writeBytes(message.toByteArray());
			serialPort.writeByte(END_SIGNAL);
		} catch (SerialPortException e) {
			System.err.println("Command message failed to send. [" + e + "]");
		}
	}
	
	public void sendSensorDataRequest() {
		byte message[] = new byte[] { START_SIGNAL, SNAPSHOT_SIGNAL, END_SIGNAL };
		try {
			serialPort.writeBytes(message);
		} catch (SerialPortException e) {
			System.err.println("Sensor data request failed to send. [" + e + "]");
		}
	}
	
	public byte[] getMostRecentMessage() {
		while (true) {
			byte[] data;
			
			try {
				data = serialPort.readBytes();
			} catch (SerialPortException e) {
				System.err.println(e);
				continue;
			}
			
			if (data == null || data.length == 0) {
				continue;
			}
			
			int startPtr = 0;
			if (buffer.isEmpty() && data[0] != RESPONSE_SIGNAL) {
				for (byte b : data) {
					if (b == RESPONSE_SIGNAL) {
						break;
					}
					startPtr++;
				}
				if (startPtr == data.length) {
					System.err.println("Received new message from Maple without 'start' symbol: ignoring this data");
					continue;
				}
			}
			
			for (int i = startPtr; i < data.length; i++) {				
				buffer.add(data[i]);
			}
			if (buffer.size() >= expectedInboundMessageSize) {
				
				int endPtr = expectedInboundMessageSize - 1;
				assert (buffer.get(0) == RESPONSE_SIGNAL);
				assert (buffer.get(endPtr - 1) == END_SIGNAL);
				
				byte[] message = new byte[endPtr - 1];
				for (int i = 0; i < endPtr - 1; i++) {
					message[i] = buffer.get(i + 1).byteValue();
				}
				buffer = buffer.subList(endPtr + 1, buffer.size());
				assert (buffer.size() == 0 || buffer.get(0) == RESPONSE_SIGNAL);
				return message;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { }
		}
	}	
}
