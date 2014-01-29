package BotClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import BotClient.BotClient.WritebackHandler;

public class Receiver extends Thread {
	private Socket socket = null;
	private WritebackHandler writeback = null;
	private BufferedReader in = null;
	
	public Receiver( WritebackHandler writeback, Socket socket ) {
		this.writeback = writeback;
		this.socket = socket;
		
		try {
			 in = new BufferedReader( new InputStreamReader(this.socket.getInputStream()));
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while ( !this.socket.isClosed() ) {
			try {
				String msg;
				while ((msg = in.readLine()) != null) {
					process(msg.trim());
				}
			} catch ( Exception e ) {
				if ( !this.socket.isClosed() ) {
					e.printStackTrace();
				} else {
					try { in.close(); } catch ( Exception ex ) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	public void process( String msg ) {
		if ( msg.equals("connected") ) {
			writeback.connected();
		} else if ( msg.equals("pong") ) {
			writeback.pong();
		} else if ( msg.startsWith("{\"MAP") ) {
			writeback.map(msg.split("\"")[3].trim());
		} else if ( msg.startsWith("{\"GAME")) {
			writeback.game(msg.split("\"")[3].trim());
		}
	}
}
