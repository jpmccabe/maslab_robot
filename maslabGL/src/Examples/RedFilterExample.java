package Examples;

import java.awt.image.BufferedImage;
import java.io.File;

import org.lwjgl.opengl.Display;

import Core.Engine;
import Core.FilterOp;

public class RedFilterExample {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(640,480);
		
		Engine.initGL(640, 480);
		FilterOp op = new FilterOp("red");
		
		BufferedImage original = TestBed.loadImage( new File("images\\rainbow.png") );
		tester.setImage(original);
		
		try{ Thread.sleep(1000); } catch ( Exception e ) {}
		
		BufferedImage filtered = op.apply(original);
		tester.setImage(filtered);
	}
}
