import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class Worker implements Runnable {

	private MongoCollection<Document> colLocal;
	private Thread thread;
	private String sensor;
	private int zona;

	private String lastMedicao;
	private String lastMedicaoDia;
	private String lastMedicaoHora;
	private double sensorMin;
	private double sensorMax;

	private CentralWork centralWork;

	public Worker(MongoCollection<Document> colLocal, String sensor, int zona, CentralWork centralWork) {
		this.colLocal = colLocal;
		this.centralWork = centralWork;
		this.sensor = sensor;
		this.zona = zona;
		sensorMax = centralWork.getSensorMax(sensor, zona);
		sensorMin = centralWork.getSensorMin(sensor, zona);
		lastMedicao = centralWork.getSensorLastMedicao(sensor, zona);
		lastMedicaoDia = lastMedicao.split(" ")[0];
		lastMedicaoHora = lastMedicao.split(" ")[1];
		System.out.println("sensor max" + sensorMax);
		System.out.println("sensor min" + sensorMin);
		System.out.println("sensor last hora" + lastMedicaoHora);
		System.out.println("Inicializado worker zona: " + zona + " do sensor: " + sensor);
		thread = new Thread(this);
		thread.start();
		while(true) {
			if(!thread.isAlive()) {
				System.out.println("ressucitei");
				thread = new Thread(this);
				thread.start();
			}
			try {
				Thread.sleep(10000);
				System.out.println("seg");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isBetween(double min, double max, double value) {
		if (value < max && value > min)
			return true;
		else
			return false;
	}

	@Override
	public void run() {

		while (true) {
			MongoCursor<Document> cursor = colLocal
					.find(Filters.and(Filters.eq("Data", lastMedicaoDia), Filters.gt("Hora", lastMedicaoHora)))
					.iterator();

			Document doc = null;

			while (cursor.hasNext()) {
				doc = cursor.next();

				if (isBetween(sensorMin, sensorMax, Double.parseDouble(doc.getString("Medicao")))) {

					centralWork.getQueue()
							.offer(new Medicao(doc.getString("Data"), doc.getString("Hora"),
									Double.parseDouble(doc.getString("Medicao")), doc.getString("Sensor"),
									doc.getString("Zona")));
				}

				lastMedicaoDia = doc.getString("Data");
				lastMedicaoHora = doc.getString("Hora");
			}
			
			thread.stop();
		}
	}

	public static void main(String[] args) {
		System.out.println("2021-04-04 12:12:!2".split(" ")[1]);
	}

}
