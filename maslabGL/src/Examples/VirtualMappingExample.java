package Examples;

import java.awt.image.BufferedImage;
import java.io.File;

import Core.Engine;
import Core.FilterOp;

public class VirtualMappingExample {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(320,240);
		
		Engine.initGL(320,240);
		FilterOp blur = new FilterOp("blur");
		FilterOp colorize = new FilterOp("colorize");
		FilterOp objRec = new FilterOp("objectRecognition");
		
		BufferedImage original = TestBed.loadImage( new File("images\\maslab.png") );
		tester.setImage(original);
		
		int frames = 0;
		long prev = System.currentTimeMillis();
		while ( true ) {
			BufferedImage filtered = blur.apply(original);
			//tester.setImage(filtered);
			
			filtered = colorize.apply(filtered);
			//tester.setImage(filtered);
			
			filtered = objRec.apply(filtered);
			tester.setImage(filtered);
			
			frames++;
			long curr = System.currentTimeMillis();
			if ( curr-prev>1000 ) {
				System.out.println("FPS: " +frames);
				frames = 0;
				prev = curr;
			}
		}
	}
}
