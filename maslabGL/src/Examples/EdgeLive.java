package Examples;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import Core.Engine;
import Core.FilterOp;
import Examples.TestBed;

public class EdgeLive  {
	public static void main( String[] args ) {
		TestBed tester = new TestBed(640,480);
		
		Engine.initGL(640,480);
		FilterOp op = new FilterOp("blur");
		FilterOp op2 = new FilterOp("edge");
		
		VideoInputFrameGrabber grabber= new VideoInputFrameGrabber(0);
        long prev = System.currentTimeMillis();
        int frames = 0;
        try {
            grabber.start();
            IplImage img;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                	BufferedImage blurred = op.apply(img.getBufferedImage());
                	BufferedImage result = op2.apply(blurred);
                    tester.setImage(result);
                }
                frames++;
                long curr = System.currentTimeMillis();
                if ( curr-prev>1000 ) {
                	tester.setText("FPS: " + frames);
                	frames = 0;
                	prev = curr;
                }
            }
        } catch (Exception e) {
        }
	}
}
