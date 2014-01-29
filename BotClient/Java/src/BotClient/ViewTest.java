package BotClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ViewTest {
	public static void main( String[] args ) {
		
		BotClient botclient = new BotClient("18.150.7.174:6667","1221",false);
		
		botclient.send("a", "State", "Ball Search");
		botclient.send("b", "Gyro", "3.14159");
		
		// Smiley
		BufferedImage image = new BufferedImage(320,240,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		int WD = 320/8;
		for ( int i = 0; i < 8; i++ ) {
			Color col = Color.getHSBColor(i/8.0f,1.0f,1.0f);
			g.setColor(col);
			g.fillRect(i*WD,0,WD,240);
		}
		
		botclient.sendImage(image);
		
		botclient.close();
	}
}