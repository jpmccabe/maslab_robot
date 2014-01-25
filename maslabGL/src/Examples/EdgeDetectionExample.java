package Examples;

import java.awt.image.BufferedImage;
import java.io.File;

import Core.Engine;
import Core.FilterOp;

public class EdgeDetectionExample {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(512,512);
		
		Engine.initGL(512,512);
		FilterOp blur = new FilterOp("blur");
		FilterOp edge = new FilterOp("edge");
		
		BufferedImage original = TestBed.loadImage( new File("images\\lena.png") );
		tester.setImage(original);
		
		try{ Thread.sleep(1000); } catch ( Exception e ) {}
		
		BufferedImage filtered = blur.apply(original);
		tester.setImage(filtered);
		
		try{ Thread.sleep(1000); } catch ( Exception e ) {}
		
		filtered = edge.apply(filtered);
		tester.setImage(filtered);
	}
}
