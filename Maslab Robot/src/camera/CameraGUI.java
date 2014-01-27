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
        private final JLabel cameraPane;
    
        
        public CameraGUI(int width, int height){
            this.width = width;
            this.height = height;
            cameraPane = createWindow("", width, height);
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
                    System.exit(0);
                }
            });

            imageFrame.setVisible(true);
            return imagePane;
        }
        
        
        /**
         * Updates the camera pane with the last frame read from the camera.
         */
        public void updateImagePane(Mat image) {         
            int w = (int) (image.size().width);
            int h = (int) (image.size().height);
            if (cameraPane.getWidth() != w || cameraPane.getHeight() != h) {
                cameraPane.setSize(w, h);
            }
            BufferedImage bufferedImage = Mat2Image.getImage(image);
            cameraPane.setIcon(new ImageIcon(bufferedImage));
        }
}
