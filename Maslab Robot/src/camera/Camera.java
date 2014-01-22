package camera;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

class Camera {
  
    private Mat lastFrame = null;
    private final VideoCapture camera;
    
    public Camera(){
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Setup the camera
        camera = new VideoCapture();
        camera.open(0);
    }

    public void readNewFrame() {
        // Wait until the camera has a new frame
        while (!camera.read(lastFrame)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (Global.processedImage==null) Global.processedImage=Global.rawImage;

        //removes garbage memory taken
        System.gc();		
    }
    
    public Mat getLastFrame(){
        return lastFrame;
    }
  }
   

