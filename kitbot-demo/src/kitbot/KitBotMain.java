package kitbot;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class KitBotMain {
	
    public static void main(String[] args) {
    	int width = 1366;
    	int height = 768;
    	
    	try {
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		width = dim.width;
    		height = dim.height;
    	} catch ( Exception e ) {
    		System.out.println( e );
    	}
    	
    	JFrame window = new JFrame("Kit Bot Interface");
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	KitBotModel model = new KitBotModel();
    	KitBotView view = new KitBotView(width,height,window);
    	KitBotController controller = new KitBotController(model,view);
    	
    	window.setSize(width,height);
    	window.setVisible(true);
    	
    	while ( true ) {
    		try {
    			Thread.sleep(100);
    			view.repaint();
    		} catch ( Exception e ) {}
    	}
    }
    
}
