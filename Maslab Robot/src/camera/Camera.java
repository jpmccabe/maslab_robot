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

class Camera implements Runnable
{

    public  void run() {
        // Load the OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Setup the camera
        VideoCapture camera = new VideoCapture();
        camera.open(0);
        // Create GUI windows to display camera output and OpenCV output
        int width = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
        int height = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT));
        JLabel cameraPane = createWindow("Camera output", width, height);
        JLabel opencvPane = createWindow("OpenCV output", width, height);
        // Main loop

        while (true) {
            // Wait until the camera has a new frame
            while (!camera.read(Global.rawImage)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (Global.processedImage==null) Global.processedImage=Global.rawImage;

            // Update the GUI windows
            updateWindow(cameraPane, Global.rawImage);
            updateWindow(opencvPane, Global.processedImage);

            //removes garbage memory taken
            System.gc();		
        }

    }

    private static JLabel createWindow(String name, int width, int height) {    
        final JFrame imageFrame = new JFrame(name);
        imageFrame.setSize(width, height);
        imageFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel imagePane = new JLabel();
        imagePane.setLayout(new BorderLayout());
        imageFrame.setContentPane(imagePane);

        imageFrame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                System.out.println("EXIT MOTHAFUCKAAAS");
                Main.devices.setMotors(0,0);
                System.exit(0);
            }
        });

        imageFrame.setVisible(true);
        return imagePane;
    }

    private static void updateWindow(JLabel imagePane, Mat mat) {
        int w = (int) (mat.size().width);
        int h = (int) (mat.size().height);
        if (imagePane.getWidth() != w || imagePane.getHeight() != h) {
            imagePane.setSize(w, h);
        }
        BufferedImage bufferedImage = Mat2Image.getImage(mat);
        imagePane.setIcon(new ImageIcon(bufferedImage));
    }

  }
   

