package Examples;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import Core.Engine;
import Core.FilterOp;
import Examples.TestBed;

public class Mapper  {
	public static void main( String[] args ) {
		
		
		BufferedImage imgz = null;
		try {
			imgz = ImageIO.read( new File("images\\distort.png") );
		} catch ( Exception e ) {
		}
		
		int W = 640;
		int H = 480;
		TestBed tester = new TestBed(W*2,H);
		Engine.initGL(W,H);
		
		FilterOp lens = new FilterOp("lens");
		FilterOp blur = new FilterOp("blur");
		FilterOp colorize = new FilterOp("colorize");
		FilterOp detect = new FilterOp("detect");
		
		VideoInputFrameGrabber grabber= new VideoInputFrameGrabber(0);
		grabber.setImageWidth(W);
		grabber.setImageHeight(H);
        long prev = System.currentTimeMillis();
        int frames = 0;
        try {
            grabber.start();
            IplImage img;
            while (true) {
                img = grabber.grab();
                if (img != null) {
                	/*
                	BufferedImage blurred = blur.apply(img.getBufferedImage());
                	BufferedImage colorized = colorize.apply(blurred);
                	BufferedImage detected = detect.apply(colorized);
                	BufferedImage mapped = process(detected);
                	mapped.getGraphics().drawImage(colorized,640,0,null);
                	//mapped.getGraphics().drawImage(detected,0,0,null);
                	*/
                	BufferedImage corrected = lens.apply(imgz);
                    tester.setImage(corrected);
                }
                 //Thread.sleep(INTERVAL);
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
	
	public static BufferedImage process( BufferedImage bi ) {
		BufferedImage map = new BufferedImage(640*2,480,BufferedImage.TYPE_INT_ARGB);
		for ( int i = 0; i < bi.getWidth(); i++ ) {
			for ( int j = bi.getHeight()-1; j > 0; j-- ) {
				int pix = bi.getRGB(i, j);
				if ( pix==0xff000000 )
					continue;
				int r = (pix>>16)&0xff;
				int g = (pix>>8)&0xff;
				int b = (pix)&0xff;
				if ( r==0 && g==0 && b>0 ) {
					double dx = (i-320);
					double ht = b*200.0/255.0;//200.0/255.0;
					double angle = (dx/bi.getWidth())*60.0/57.3;
					double theta = (ht/bi.getWidth())*60.0/57.3;
					double dist = (10.0/Math.atan(theta/2))/Math.cos(angle*0.95);
					double X = Math.cos(angle)*dist+320;
					double Y = Math.sin(angle)*dist+240;
					X = 3500.0/ht;
					Y = X*Math.tan(angle);
					try {
						map.setRGB((int)X+320,(int)Y+240,0xff0000ff);
					} catch ( Exception e ) {}
					j = 0;
				}
			}	
		}
		return map;
	}
}
