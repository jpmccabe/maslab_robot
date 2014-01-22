package kitbot;

import jssc.SerialPort;
import jssc.SerialPortException;

public class KitBotModel {
	private SerialPort serialPort;
    private byte motorA = 0;
    private byte motorB = 0;
    
	public KitBotModel() {
		try {
			serialPort = new SerialPort("COM4");
            serialPort.openPort();
            serialPort.setParams(115200, 8, 1, 0);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
	}
	
	public void setMotors( double powerA, double powerB ) {
		motorA = (byte)(-powerA*127);
		motorB = (byte)(powerB*127);
		modified();
	}
	
	public void modified() {
		try {
			byte[] data = new byte[4];
			data[0] = 'S';		// Start signal "S"
			data[1] = motorA;	// Motor A data
			data[2] = motorB;	// Motor B data
			data[3] = 'E';		// End signal "E"
			serialPort.writeBytes(data);
		} catch ( Exception ex ) {
			System.out.println(ex);
		}
	}
	
	public void finalize() {
		try {
			serialPort.closePort();
		} catch ( Exception ex ) {
			System.out.println(ex);
		}
	}
}
