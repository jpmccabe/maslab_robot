package camera;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;

public class CameraGUI {
        private final int width;
        private final int height;
        private final Camera camera;
    
        public CameraGUI(Camera camera){
            this.camera = camera;
            width = camera.getWidth();
            height = camera.getHeight();
            JLabel cameraPane = createWindow("Camera output", width, height);
            JLabel opencvPane = createWindow("OpenCV output", width, height);
        }
        
        
        private JLabel createWindow(String name, int width, int height) {    
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
