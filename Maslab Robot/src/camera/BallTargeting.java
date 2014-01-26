package camera;
class BallTargeting{
	
    final private BallStruct ballStruct;
	final private static Double distanceConversionConstant = 500.0;
	
	
	public BallTargeting(BallStruct ballStruct){
	    this.ballStruct =  ballStruct;
	}
	
	
	/**
	 * @return distance to ball in inches
	 */
	public double getDistance(){
	    double diameter = ballStruct.getRadius() * 2;
        final double width = ballStruct.getBoundingRectangle().width;
        final double height = ballStruct.getBoundingRectangle().height;        
        final Boolean overlappingVertical= (width/diameter<0.85 && diameter>20);
        final Boolean overlappingHorizontal= (height/diameter<0.85 && diameter>20);
        
        if (overlappingHorizontal){
            diameter*=0.5;
        }
        else if (overlappingVertical){
            diameter*=0.675;
        }
        
        final double distance=distanceConversionConstant/diameter;  
        return distance;
	}
	
	
	
	/**
	 * @return angle to ball in radians
	 */
	public double getAngle(){
        final double xCircle = ballStruct.getCenter().x;
        final double yCircle = ballStruct.getCenter().y;
        final double angle= Math.atan((320.0-xCircle)/(480.0-yCircle));
    
        return angle;
	}
}