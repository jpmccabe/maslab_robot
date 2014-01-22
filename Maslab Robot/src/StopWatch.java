public class StopWatch{
	
	static double start;
	
	public static void timeOut (int x){
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void resetTime (){
		start= System.nanoTime();
	}
	public static double getTime(){
		return (System.nanoTime()-start)/1000000000;
	}
}