package Examples;

import java.awt.image.BufferedImage;
import java.io.File;

import Core.Engine;
import Core.FilterOp;

public class AutomataExample {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(640,480);
		
		Engine.initGL(640, 480);
		FilterOp op = new FilterOp("automata");
		
		BufferedImage state = TestBed.loadImage( new File("images\\automata.png") );

		int gen = 0;
		int fps = 0;
		int frames = 0;
		long prev = System.currentTimeMillis();
		while ( true ) {
			tester.setImage(state);
			BufferedImage next = op.apply(state);
			state = next;
			gen++;
			frames++;
			long curr = System.currentTimeMillis();
			if ( curr-prev>1000 ) {
				fps = frames;
				frames = 0;
				prev = curr;
			}
			tester.setText("FPS: " + fps +", GENERATION: " + gen);
		}
	}
}
