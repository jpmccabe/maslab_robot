package Examples;

import java.awt.image.BufferedImage;
import java.io.File;

import Core.Engine;
import Core.FilterOp;

public class GaussianFilterExample {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(512,512);
		
		Engine.initGL(512,512);
		FilterOp op = new FilterOp("blur");
		
		BufferedImage original = TestBed.loadImage( new File("images\\lena.png") );
		tester.setImage(original);
		
		try{ Thread.sleep(1000); } catch ( Exception e ) {}
		
		while ( true ) {
			BufferedImage filtered = op.apply(original);
			tester.setImage(filtered);
			original = filtered;
		}
	}
}
