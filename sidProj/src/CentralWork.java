import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CentralWork {

	private BlockingQueue<Medicao> queue;
	
	private SqlDispatcher s;
	
	Connections c;
	
	public CentralWork() throws IOException {
		queue = new LinkedBlockingQueue();
		try {
			c = new Connections();
			s = new SqlDispatcher(c.getConnection(), c.getConnectCloud(), this);
			Worker worker = new Worker(Constants.colt1, "T1", this);
			System.out.println("AAA");
//			s.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public BlockingQueue<Medicao> getQueue() {
		return queue;
	}
	
	public Connections getC() {
		return c;
	}
	
	public static void main(String[] args) throws IOException {
		CentralWork cw = new CentralWork();
//		SqlDispatcher s = new SqlDispatcher(cw.getC().getConnection(), cw.getC().getConnectCloud(), cw);
//		Worker worker = new Worker(Constants.colt1, "T1", cw);
//		worker.run();
//		s.run();
	}
	
}
