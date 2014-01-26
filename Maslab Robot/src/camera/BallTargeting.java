package camera;
class BallTargeting{
	
	final static Double distanceConversionConstant = 500.0;
	
	public double[] calculate(BallStruct ballStruct){
		//finds the exact diameter of the closest ball and the distance to it in inches
		double diameter= ballStruct.circle.radius * 2;
		final double width = ballStruct.rect.width;
		final double height = ballStruct.rect.height;
		final double xCircle = ballStruct.circle.center.x;
		final double yCircle = ballStruct.circle.center.y;
		
		final Boolean overlappingVertical= (width/diameter<0.85 && diameter>20);
		final Boolean overlappingHorizontal= (height/diameter<0.85 && diameter>20);
		
		if (overlappingHorizontal){
			diameter*=0.5;
		}
		else if (overlappingVertical){
			diameter*=0.675;
		}
		
		final double distance=distanceConversionConstant/diameter;	
		final double angle= Math.atan((320.0-xCircle)/(480.0-yCircle));
	
		return new double[] {distance,angle};
	}
		
}