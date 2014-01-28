package camera;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class BallStruct{

    private final Rect boundingRectangle;
    private final Point center;
    private final double radius;
	
	public BallStruct(Rect boundingRectangle, Point center, double radius){
	    this.boundingRectangle = boundingRectangle;
	    this.center = center;
	    this.radius = radius;
	}
	
	
	public Rect getBoundingRectangle(){
	    return boundingRectangle;
	}
	
	
	public Point getCenter(){
	    return center;
	}
	
	
	public double getRadius(){
	    return radius;
	}
}