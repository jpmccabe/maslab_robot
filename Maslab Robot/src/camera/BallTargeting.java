package camera;
class BallTargeting{
	
	Double distanceConversionConstant=500.0;
	
	public synchronized double[] calculate(BallStruct ballStruct){
		//finds the exact diameter of the closest ball and the distance to it in inches
		double diameter= ballStruct.circle.radius * 2;
		double width= ballStruct.rect.width;
		double height= ballStruct.rect.height;
		double xCircle=ballStruct.circle.center.x;
		double yCircle=ballStruct.circle.center.y;
		
		Boolean overlappingVertical= (width/diameter<0.85 && diameter>20);
		Boolean overlappingHorizontal= (height/diameter<0.85 && diameter>20);
		
		if (overlappingHorizontal){
			diameter*=0.5;
		}
		else if (overlappingVertical){
			diameter*=0.675;
		}
		double distance=distanceConversionConstant/diameter;
				
		double angle= Math.atan((320.0-xCircle)/(480.0-yCircle));
	
		return new double[] {distance,angle};
	}
		
}