
package camera;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


/**
 * Processor for silo
 */
class CameraProcessor6 extends CameraProcessor{

	private Mat processedImage;
	private double leftDistance;
	private double rightDistance;
	private double centerDistance;
	private double angleInDegrees;
	private int centerXValue;
	private double angleToTurnParallelDegrees;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}


	public CameraProcessor6(){
		leftDistance = Double.MAX_VALUE;
		rightDistance = Double.MAX_VALUE;
		centerDistance = Double.MIN_VALUE;
		angleInDegrees = Double.MAX_VALUE;
		centerXValue = Integer.MAX_VALUE;
		processedImage  = null;
	}

	public void processImage(Mat imageToProcess) {
		boolean reactorSpotted=false;		
		final Mat processedImage = imageToProcess.clone();

		Core.inRange(processedImage, new Scalar(123, 25,10), new Scalar(160, 255, 255), processedImage);
		Imgproc.dilate(processedImage, processedImage, new Mat(), new Point(-1,-1),1);
		Imgproc.erode(processedImage, processedImage,  new Mat(), new Point (-1, -1), 2);


		final Mat clone = processedImage.clone();

		final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(clone, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);

		double maxArea = 0.0;
		Rect boundingRect = new Rect();
		for(int index=0;index<contours.size();index++){
			double area= Imgproc.contourArea(contours.get(index));
			Rect rect=Imgproc.boundingRect(contours.get(index));
			if (area>maxArea && area>250 && rect.width>50){
				boundingRect=rect;
				reactorSpotted = true;
				maxArea = area;
			}	
		}

		Imgproc.Canny(processedImage, processedImage, 15, 200);		

		double averageDistance = 1000.0;
		double leftDistance = 1000.0;
		double rightDistance = 1000.0;
		int count=0;
		if (reactorSpotted==true){
			leftDistance=0;
			rightDistance=0;
			averageDistance=0;
			for(double x=boundingRect.x+13;x<boundingRect.x+boundingRect.width-13;x+=(boundingRect.width-26)/50.0){
				count+=1;

				if(count>5 && count<23) continue;
				if(count>27 && count<=45) continue;
				int firstPixel=0;
				int secondPixel=0;
				for(int y= Math.min(boundingRect.y+boundingRect.height+2,479);y>=Math.max(boundingRect.y-2, 1);y--){
					if( processedImage.get(y,(int)x)[0]==255){
						if (firstPixel==0) firstPixel=y;
						else secondPixel=y;
					}
				}
				//System.out.println("x:"+x+" pixels:"+(firstPixel-secondPixel));
				if (count>=23 && count<=27) averageDistance+=pixelToDistance(firstPixel-secondPixel);
				if (count<=5){leftDistance+=pixelToDistance(firstPixel-secondPixel);}
				if (count>45){rightDistance+=pixelToDistance(firstPixel-secondPixel);}
				Core.line(processedImage, new Point((int)x,firstPixel), new Point((int)x,secondPixel), new Scalar(255,0,0));
			}
			averageDistance/=5;
			leftDistance/=5;
			rightDistance/=(count-45);

		}

		final double leftAngleRadiansAbs = Math.abs(Math.toRadians(pixelToAngle(boundingRect.x)));
		final double rightAngleRadiansAbs = Math.abs(Math.toRadians(pixelToAngle(boundingRect.x+boundingRect.width)));
		final double centerAngle = pixelToAngle(boundingRect.x + (boundingRect.width/2));
		leftDistance /= Math.cos(leftAngleRadiansAbs);
		rightDistance /= Math.cos(leftAngleRadiansAbs);
		//System.out.println("left distance silo: " + leftDistance);
		//System.out.println("right distance silo: " + rightDistance);
		final double insideOfArcSin = Math.min(1, (Math.min(leftDistance,rightDistance) / 3) * Math.sin(Math.max(leftAngleRadiansAbs,rightAngleRadiansAbs)));
		final double angleToTurnParallelRadians = Math.asin(insideOfArcSin);
		double angleToTurnParallelDegrees = Math.toDegrees(angleToTurnParallelRadians);
		angleToTurnParallelDegrees = leftDistance <= rightDistance ? angleToTurnParallelDegrees : -1*angleToTurnParallelDegrees;
		//System.out.println("angle to turn parallel silo: " + angleToTurnParallelDegrees);

		Imgproc.cvtColor(processedImage,processedImage,Imgproc.COLOR_GRAY2RGB);

		final int centerXValue = (int) ((boundingRect.x) + (boundingRect.width/2.0));

		synchronized(this){
			this.leftDistance = leftDistance;
			this.rightDistance = rightDistance;
			this.centerDistance = averageDistance;
			this.angleInDegrees = centerAngle;
			this.centerXValue = centerXValue;
			this.processedImage = processedImage;
			this.angleToTurnParallelDegrees = angleToTurnParallelDegrees;
		}


	}


	private double pixelToDistance(int pixel){
		return 346.187/Math.pow(pixel,0.878562);
	}
	/**
	 * 
	 * @param pixel the pixel to be converted to angle
	 * @return angle in degrees from front of robot
	 */
	private double pixelToAngle(int pixel){
		int direction=1;
		if (pixel<348){
			pixel=348+(348-pixel);
			direction=-1;
		}
		double absoluteAngleInDegrees =  (-100.845+0.355258*pixel-0.000186951*Math.pow(pixel, 2));
		return absoluteAngleInDegrees*direction;
	}

	synchronized public double getLeftDistance(){
		return leftDistance;
	}

	synchronized public double getRightDistance(){
		return rightDistance;
	}

	synchronized public double getCenterDistance(){
		return centerDistance;
	}

	synchronized public double getAngleInDegrees(){
		return angleInDegrees;
	}

	synchronized public int getCenterXValue(){
		return centerXValue;
	}

	synchronized public Mat getProcessedImage(){
		return processedImage;
	}

	synchronized public double getAngleToTurnParallelInDegrees(){
		return angleToTurnParallelDegrees;
	}
}
