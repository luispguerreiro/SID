import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class Worker implements Runnable {
	
	private MongoCollection<Document> colLocal;
	private Thread thread;
	
	
	public Worker(MongoCollection<Document> colLocal) {
		this.colLocal = colLocal;

		thread = new Thread(this);
		thread.start();
	}
	


	@Override
	public void run() {
		
		
		
	}
	public static void main(String[] args) {
		
	}

}
