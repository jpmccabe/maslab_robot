package BotClient;

public class PingTest {
	public static void main( String[] args ) {
		
		BotClient botclient = new BotClient("18.150.7.174:6667","1221",false);
		
		double latency = 0;
		int cnt = 0;
		while ( true ) {
			botclient.ping();
			try { Thread.sleep(10); } catch ( Exception e ) {}
			latency = latency*0.99 + botclient.getLatency()*0.01;
			cnt++;
			if (cnt%100==0)
				System.out.println((int)(latency)+" ms");
		}
	}
}
