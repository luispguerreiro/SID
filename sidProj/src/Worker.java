import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class Worker implements Runnable {

	private MongoCollection<Document> colLocal;
	private Thread thread;
	private String sensor;
	
	CentralWork centralWork;

	public Worker(MongoCollection<Document> colLocal, String sensor, CentralWork centralWork) {
		this.colLocal = colLocal;
		this.centralWork=centralWork;
		this.sensor = sensor;
		thread = new Thread(this);
		System.out.println("WWW");
		thread.start();
	}

	@Override
	public void run() {
		MongoCursor<Document> cursor = colLocal.find().iterator();
		while (true) {

			if (cursor.hasNext()) {
				Document doc = cursor.next();
				centralWork.getQueue().add(new Medicao(doc.getString("Data"), doc.getString("Hora"), Double.parseDouble(doc.getString("Medicao")),
						doc.getString("Sensor"), doc.getString("Zona")));
			}
		}
	}

	public static void main(String[] args) {
		
	}

}
