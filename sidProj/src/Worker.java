import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

	private List<Alerta> lastAlertaAproximacao = new ArrayList<>();
	private List<Integer> culturaIdListAproximacao = new ArrayList<>();
	private List<Alerta> lastAlertaLimite = new ArrayList<>();
	private List<Integer> culturaIdListLimite = new ArrayList<>();

	private List<Medicao> anomalies = new ArrayList<>();

	private final double percentagem = 0.9;

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
//		while(true) {
//			if(!thread.isAlive()) {
//				System.out.println("ressucitei");
//				thread = new Thread(this);
//				thread.start();
//			}
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}

	// return true sempre que ha alerta
	public boolean checkMinMaxTypeSensor(ParametrosCultura p, double medicao) {
		if (sensor.equals("T"))
			return (medicao > p.getTemp_max() || medicao < p.getTemp_min());
		if (sensor.equals("H"))
			return (medicao > p.getHumidade_max() || medicao < p.getHumidade_min());
		if (sensor.equals("L"))
			return (medicao > p.getLuminosidade_max() || medicao < p.getLuminosidade_min());
		throw new IllegalArgumentException();
	}

	public boolean checkAproximacaoSensor(ParametrosCultura p, double medicao) {
		if (sensor.equals("T")) {
			double auxMax = (p.getTemp_max() - p.getTemp_min()) * percentagem;
			double auxMin = (p.getTemp_max() - p.getTemp_min()) * (1 - percentagem);

			return (auxMax + p.getTemp_min() < medicao || auxMin + p.getTemp_min() > medicao);

		}

		if (sensor.equals("H")) {
			double auxMax = (p.getHumidade_max() - p.getHumidade_min()) * percentagem;
			double auxMin = (p.getHumidade_max() - p.getHumidade_min()) * (1 - percentagem);
			return (auxMax + p.getHumidade_min() < medicao || auxMin + p.getHumidade_min() > medicao);
		}
		if (sensor.equals("L")) {
			double auxMax = (p.getLuminosidade_max() - p.getLuminosidade_min()) * percentagem;
			double auxMin = (p.getLuminosidade_max() - p.getLuminosidade_min()) * (1 - percentagem);
			return (auxMax + p.getLuminosidade_min() < medicao || auxMin + p.getLuminosidade_min() > medicao);
		}
		return false;
	}

	public boolean isBetween(double min, double max, double value) {
		if (value < max && value > min)
			return true;
		else
			return false;
	}

	public String chooseTipoAlerta() {
		if (sensor.equals("T"))
			return "Temperatura";
		if (sensor.equals("H"))
			return "Humidade";
		if (sensor.equals("L"))
			return "Luminosidade";
		throw new IllegalArgumentException();
	}

	@Override
	public void run() {

		double lastLeitura = 0;
		int booleano = 0;
		int tinyint = 0;
		while (true) {

			LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

			if (!(lastMedicaoDia.equals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) {
				lastMedicaoDia = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				lastMedicaoHora = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
				System.out.println("LAST MEDICAO ERA DE OUTRO DIA");
			}

			MongoCursor<Document> cursor = colLocal
					.find(Filters.and((Filters.gte("Data", lastMedicaoDia)), Filters.gt("Hora", lastMedicaoHora)))
					.iterator();

			Document doc = null;

			while (cursor.hasNext()) {
				doc = cursor.next();
				Medicao m;
				m = new Medicao(doc.get("_id"), doc.getString("Data"), doc.getString("Hora"),
						Double.parseDouble(doc.getString("Medicao")), doc.getString("Sensor"), doc.getString("Zona"));
				if (isBetween(sensorMin, sensorMax, m.getLeitura())) {

					centralWork.getQueueMedicao().offer(m);

					if (Math.abs(lastLeitura - m.getLeitura()) > 2 || anomalies.size() > 0) {
						anomalies.add(m);
						if (anomalies.size() == 4) {
							if (Math.abs(anomalies.get(0).getLeitura() - anomalies.get(1).getLeitura()) < 1)
								booleano++;
							if (Math.abs(anomalies.get(1).getLeitura() - anomalies.get(2).getLeitura()) < 1)
								booleano++;
							if (Math.abs(anomalies.get(2).getLeitura() - anomalies.get(3).getLeitura()) < 1)
								booleano++;

							if (booleano != 3) {
								anomalies.clear();
								booleano = 0;
								System.out.println(
										"           ANOMALIAS        \n *********ANOMALIAS********\n ******************");
							} else {
								for (int i = 0; i < anomalies.size(); i++) {
									if (i == anomalies.size() - 1)
										tinyint = 1;
									for (ParametrosCultura parametro : centralWork.getParameters(zona)) {
										Alerta a;
										if (checkMinMaxTypeSensor(parametro, anomalies.get(i).getLeitura())) {
											a = new Alerta(anomalies.get(i).getId(), parametro.getId(),
													chooseTipoAlerta(), " DE ULTRAPASSAGEM DE VALORES", zona, sensor,
													anomalies.get(i).getData() + " " + anomalies.get(i).getHora(),
													anomalies.get(i).getLeitura(), tinyint);
											centralWork.getAlertaQueue().offer(a);
										}
										if (checkAproximacaoSensor(parametro, anomalies.get(i).getLeitura())) {
											a = new Alerta(anomalies.get(i).getId(), parametro.getId(),
													chooseTipoAlerta(), " DE APROXIMAÇÃO DE VALORES", zona, sensor,
													anomalies.get(i).getData() + " " + anomalies.get(i).getHora(),
													anomalies.get(i).getLeitura(), tinyint);
											centralWork.getAlertaQueue().offer(a);
										}
									}

								}
								anomalies.clear();
								booleano = 0;

							}

						}
					}

					lastLeitura = m.getLeitura();

					if (anomalies.size() == 0) {
						String nowMinus5MiString2 = LocalDateTime.now().minusSeconds(16)
								.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
						LocalDateTime nowMinus5Min2 = LocalDateTime.parse(nowMinus5MiString2,
								DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

						LocalDateTime horaMedicao = LocalDateTime.parse(
								doc.getString("Data") + " " + doc.getString("Hora"),
								DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

						if (horaMedicao.isAfter(nowMinus5Min2)) {

							for (ParametrosCultura parametro : centralWork.getParameters(zona)) {
								Alerta a;
								if (checkMinMaxTypeSensor(parametro, Double.parseDouble(doc.getString("Medicao")))) {
									a = new Alerta(doc.get("_id"), parametro.getId(), chooseTipoAlerta(),
											" DE ULTRAPASSAGEM DE VALORES", zona, sensor,
											doc.getString("Data") + " " + doc.getString("Hora"),
											Double.parseDouble(doc.getString("Medicao")), 0);

									if (minutesToHaveAlert(parametro, a, lastAlertaLimite, culturaIdListLimite)) {
										a.setEnviarAlerta(1);
										centralWork.getAlertaQueue().offer(a);
									} else {
										centralWork.getAlertaQueue().offer(a);
									}
								} else {
									if (checkAproximacaoSensor(parametro,
											Double.parseDouble(doc.getString("Medicao")))) {
										a = new Alerta(doc.get("_id"), parametro.getId(), chooseTipoAlerta(),
												" DE APROXIMAÇÃO DE VALORES", zona, sensor,
												doc.getString("Data") + " " + doc.getString("Hora"),
												Double.parseDouble(doc.getString("Medicao")), 0);
										if (minutesToHaveAlert(parametro, a, lastAlertaAproximacao,
												culturaIdListAproximacao)) {
											a.setEnviarAlerta(1);
											centralWork.getAlertaQueue().offer(a);
										} else {
											centralWork.getAlertaQueue().offer(a);
										}
									}
								}
							}
						}
					}
				}

				lastMedicaoDia = doc.getString("Data");
				lastMedicaoHora = doc.getString("Hora");
			}
		}

	}

	public void discardAnomalies(ArrayList<Medicao> anomalies, double lastLeitura, Medicao atual) {
		if (lastLeitura == 0)
			return;
		if (Math.abs(lastLeitura - atual.getLeitura()) > 2) {
			anomalies.add(atual);
			for (int i = 0; i < 3; i++) {

			}

		}

	}

	/**
	 * @param parametro
	 * @param a
	 */
	public boolean minutesToHaveAlert(ParametrosCultura parametro, Alerta a, List<Alerta> lastAlerta,
			List<Integer> culturaIdList) {
		List<Alerta> aux = new ArrayList<>(lastAlerta);
		if (lastAlerta.isEmpty() || !culturaIdList.contains(a.getCulturaId())) {
			centralWork.getAlertaQueue().offer(a);
			lastAlerta.add(a);
			culturaIdList.add(a.getCulturaId());
			System.out.println("Conseguimos!!!!");
			System.out.println("\n              ****new alerta added**** Cultura: " + parametro.getId());
		} else {
			for (Alerta alerta : aux) {

				String nowMinus5MiString = LocalDateTime.now().minusSeconds(15)
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				LocalDateTime nowMinus5Min = LocalDateTime.parse(nowMinus5MiString,
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				if (alerta.getCulturaId() == a.getCulturaId()) {
					LocalDateTime horaAlerta = LocalDateTime.parse(alerta.getDate(),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

					if (horaAlerta.isBefore(nowMinus5Min)) {

						System.out.println("Conseguimos!!!!");
//						centralWork.getAlertaQueue().offer(a);
						System.out.println("\n              ****new alerta added**** Cultura: " + parametro.getId());
						lastAlerta.remove(alerta);
						lastAlerta.add(a);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String nowMinus5MiString = LocalDateTime.now().minusSeconds(15)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime nowMinus5Min = LocalDateTime.parse(nowMinus5MiString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		String nowMinus5MiString2 = LocalDateTime.now().minusSeconds(20)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime nowMinus5Min2 = LocalDateTime.parse(nowMinus5MiString2,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		System.out.println(nowMinus5Min.toString() + "   " + nowMinus5Min2.toString());
	}

}
