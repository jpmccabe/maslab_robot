package comm;

import java.io.ByteArrayOutputStream;

import jssc.SerialPort;
import jssc.SerialPortException;

public class MapleIO {

    public static final byte INIT_SIGNAL = (byte) 'I';
    public static final byte SET_SIGNAL = (byte) 'S';
    public static final byte GET_SIGNAL = (byte) 'G';
    public static final byte RESPONSE_SIGNAL = (byte) 'R';
    public static final byte END_SIGNAL = (byte) 0xff;

    public enum SerialPortType {
        LINUX, SIMULATION, WINDOWS
    }

    private SerialPort serialPort;
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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

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
            System.err
                    .println("Failed to auto-connect to serial port of type \""
                            + port + "\"");
            finalize();
            System.exit(0);
        }

        System.out.println("Connected to serial port: " + port + i);
    }

    void connectToMac() {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    finalize();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        String port = "COM16";

        boolean isConnected = false;
        try {
            serialPort = new SerialPort(port);
            serialPort.openPort();
            serialPort.setParams(115200, 8, 1, 0);
            isConnected = true;
        } catch (SerialPortException ex) {
        }
        if (!isConnected) {
            System.err
                    .println("Failed to connect to serial port \""
                            + port + "\"");
            finalize();
            System.exit(0);
        }

        System.out.println("Connected to serial port: " + port);
    }

    /*
     * Clean-up protocol for serial port
     */
    public void finalize() {
        try {
            if (serialPort != null && serialPort.isOpened()) {
                serialPort.closePort();
            } else {
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
            // we have observed strange behavior when several calls to
            // writeBytes()
            // are made in immediate succession, so we pack the entire message
            // into a single call.
            byte[] packet = buildPacket(INIT_SIGNAL, message.toByteArray(),
                    END_SIGNAL);
            // System.out.println("INI: " + Arrays.toString(packet));
            serialPort.writeBytes(packet);
        } catch (SerialPortException e) {
            System.err.println("Init message failed to send. [" + e + "]");
        }
    }

    public void sendCommand(ByteArrayOutputStream message) {
        try {
            // see comment in sendInitMessage()
            byte[] packet = buildPacket(SET_SIGNAL, message.toByteArray(),
                    END_SIGNAL);
            // System.out.println("SET: " + Arrays.toString(packet));
            serialPort.writeBytes(packet);
        } catch (SerialPortException e) {
            System.err.println("Command message failed to send. [" + e + "]");
        }
    }

    public void sendSensorDataRequest() {
        try {
            // see comment in sendInitMessage()
            byte[] packet = new byte[] { GET_SIGNAL };
            // System.out.println("GET: " + Arrays.toString(packet));
            serialPort.writeBytes(packet);
        } catch (SerialPortException e) {
            System.err.println("Sensor data request failed to send. [" + e
                    + "]");
        }
    }

    public byte[] getMostRecentMessage() {
        while (true) {

            byte[] data;

            try {
                byte firstByte = (byte) 0;
                while (firstByte != RESPONSE_SIGNAL) {
                    firstByte = serialPort.readBytes(1)[0];
                }
                data = serialPort.readBytes(expectedInboundMessageSize - 2);
                byte lastByte = serialPort.readBytes(1)[0];
                if (lastByte != END_SIGNAL) {
                    System.err
                            .println("Received packet not terminated with END symbol");
                    System.exit(1);
                }
            } catch (SerialPortException e) {
                System.err.println(e);
                continue;
            }

            return data;
        }
    }

    private static byte[] buildPacket(byte first, byte[] message, byte last) {
        int len = message.length;
        byte[] packet = new byte[len + 2];
        packet[0] = first;
        System.arraycopy(message, 0, packet, 1, len);
        packet[len + 1] = last;
        return packet;
    }
}
