package camera;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class BallStruct{

	Rect rect;
	Circle circle= new Circle();

	public class Circle{
		Point center;
		double radius;
	}
	
}