package camera;

import org.opencv.core.Mat;

public abstract class CameraProcessor {
    public abstract void processImage(Mat imageToProcess);
}
