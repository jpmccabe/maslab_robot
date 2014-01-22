import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Mat2Image {
    static BufferedImage img;
    static byte[] dat;
    
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static BufferedImage getImage(Mat mat) {
        allocateTempSpace(mat);
        Mat convMat = new Mat(mat.size(), mat.type());
        Imgproc.cvtColor(mat, convMat, Imgproc.COLOR_BGR2RGB);
        convMat.get(0, 0, dat);
        img.getRaster().setDataElements(0, 0, img.getWidth(), img.getHeight(), dat);
        return img;
    }
    
    private static void allocateTempSpace(Mat mat) {
        int w = mat.cols();
        int h = mat.rows();
        if (dat == null || dat.length != w * h * 3) {
            dat = new byte[w * h * 3];
        }
        if (img == null || img.getWidth() != w || img.getHeight() != h) {
            img = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        }
    }
}