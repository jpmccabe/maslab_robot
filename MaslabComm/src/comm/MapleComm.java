package comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import devices.MapleDevice;

public class MapleComm {
	private MapleIO mapleIO;
	private List<MapleDevice> deviceList = new ArrayList<MapleDevice>();
	private int consumeSize = 0;
	
	public MapleComm(MapleIO.SerialPortType portType) {
		mapleIO = new MapleIO();
		mapleIO.connect(portType);
	}
	
	public MapleComm() {
	    mapleIO = new MapleIO();
	    mapleIO.connectToMac();
	}

	/*
	 * Add a device to the device list.
	 */
	public void registerDevice(MapleDevice device) {
		deviceList.add(device);
	}

	/*
	 * Send the list of devices and corresponding pins to the Maple.
	 */
	public void initialize() {
		
		if (!verify()) {
			System.err.println("MapleComm initialization failed");
			return;
		}
		
		// Construct the initialization message
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		data.write((byte) deviceList.size());
		for (MapleDevice device : deviceList) {
			consumeSize += device.expectedNumBytesFromMaple();
			try {
				data.write(device.getDeviceCode());
				data.write(device.getInitializationBytes());
			} catch (IOException e) { }
		}
		mapleIO.setExpectedInboundMessageSize(consumeSize);

		// Transmit the initialization message
		mapleIO.sendInitMessage(data);
	}

	/*
	 * Send commands (e.g. motor velocity) to the Maple
	 */
	public void transmit() {

		// Combine commands for all actuators that we want to actuate
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		byte counter = 0;
		for (MapleDevice device : deviceList) {
			byte[] command = device.generateCommandToMaple();
			if (command.length > 0) {
				data.write(counter);
				try {
					data.write(command);
				} catch (IOException e) { }
			}
			counter++;
		}

		// Transmit the combined commands
		mapleIO.sendCommand(data);
	}
	
	/*
	 * Wait for, and process, up-to-date sensor data from the Maple
	 */
	public void updateSensorData() {
		mapleIO.sendSensorDataRequest();
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) { }
		ByteBuffer buff = ByteBuffer.wrap(mapleIO.getMostRecentMessage());
		
		// Give the byte buffer to each device and let it take what it needs
		for (MapleDevice device : deviceList) {
			device.consumeMessageFromMaple(buff);
		}
	}

	/*
	 * Uses Maple specs to verify that all proposed connections are legal
	 * according to the board.
	 */
	private boolean verify() {
		return true;
	}
}
