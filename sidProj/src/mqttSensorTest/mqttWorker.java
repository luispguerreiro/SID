package mqttSensorTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class mqttWorker implements Runnable, MqttCallback {

	private MqttClient mqttclient;

	private MongoCollection<Document> colLocal;
	private Thread thread;
	private String sensor;
	private int zona;

	private String lastMedicao;
	private String lastMedicaoDia;
	private String lastMedicaoHora;
	private double sensorMin;
	private double sensorMax;

	private List<mqttAlerta> lastAlertaAproximacao = new ArrayList<>();
	private List<Integer> culturaIdListAproximacao = new ArrayList<>();
	private List<mqttAlerta> lastAlertaLimite = new ArrayList<>();
	private List<Integer> culturaIdListLimite = new ArrayList<>();

	private List<mqttMedicao> anomalies = new ArrayList<>();

	private final double percentagem = 0.9;
	private final int diferencaLeituras = 2;
	private final int numeroMedicoesToleraveis = 4;
	private final int minutesToHaveAlerta = 4;

	private mqttCentralWork centralWork;

	public mqttWorker() {

	}

	public mqttWorker(MongoCollection<Document> colLocal, String sensor, int zona, mqttCentralWork centralWork) {
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

	public void connecCloud() {
		try {
			(mqttclient = new MqttClient("tcp://broker.mqtt-dashboard.com:1883", "MongoToCloud123455" + "pina"))
					.connect();
			mqttclient.setCallback((MqttCallback) this);
//			mqttclient.subscribe("pina");
		} catch (MqttException ex) {
			ex.printStackTrace();
		}
	}

	public void writeSensor(final String s) {
		try {
			final MqttMessage mqttMessage = new MqttMessage();
			mqttMessage.setPayload(s.getBytes());
			mqttclient.publish("pina", mqttMessage);
		} catch (MqttException ex) {
			ex.printStackTrace();
		}
	}

	// return true sempre que ha alerta
	public boolean checkMinMaxTypeSensor(mqttParametrosCultura p, double medicao) {
		if (sensor.equals("T"))
			return (medicao > p.getTemp_max() || medicao < p.getTemp_min());
		if (sensor.equals("H"))
			return (medicao > p.getHumidade_max() || medicao < p.getHumidade_min());
		if (sensor.equals("L"))
			return (medicao > p.getLuminosidade_max() || medicao < p.getLuminosidade_min());
		throw new IllegalArgumentException();
	}

	public boolean checkAproximacaoSensor(mqttParametrosCultura p, double medicao) {
		if (sensor.equals("T")) {
			double auxMax = (p.getTemp_max() - p.getTemp_min()) * mqttConstants.percentagemAviso;
			double auxMin = (p.getTemp_max() - p.getTemp_min()) * (1 - mqttConstants.percentagemAviso);

			return (auxMax + p.getTemp_min() < medicao || auxMin + p.getTemp_min() > medicao);

		}

		if (sensor.equals("H")) {
			double auxMax = (p.getHumidade_max() - p.getHumidade_min()) * mqttConstants.percentagemAviso;
			double auxMin = (p.getHumidade_max() - p.getHumidade_min()) * (1 - mqttConstants.percentagemAviso);
			return (auxMax + p.getHumidade_min() < medicao || auxMin + p.getHumidade_min() > medicao);
		}
		if (sensor.equals("L")) {
			double auxMax = (p.getLuminosidade_max() - p.getLuminosidade_min()) * mqttConstants.percentagemAviso;
			double auxMin = (p.getLuminosidade_max() - p.getLuminosidade_min()) * (1 - mqttConstants.percentagemAviso);
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
		int aux = 0;
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
				aux = 0;
				doc = cursor.next();
				mqttMedicao m;
				m = new mqttMedicao(doc.get("_id"), doc.getString("Data"), doc.getString("Hora"),
						Double.parseDouble(doc.getString("Medicao")), doc.getString("Sensor"), doc.getString("Zona"));
				if (isBetween(sensorMin, sensorMax, m.getLeitura())) {

					centralWork.getQueueMedicao().offer(m);

//					try {
//						Thread.sleep(0);
//						for (ParametrosCultura p : centralWork.getParameters(zona)) {
//							centralWork.getCulturaMedicaoQueue().offer(new CulturaMedicao(m.getId(), p.getId()));
//						}
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					String nowMinus5MiString2 = LocalDateTime.now().minusSeconds(16)
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
					LocalDateTime nowMinus5Min2 = LocalDateTime.parse(nowMinus5MiString2,
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

					LocalDateTime horaMedicao = LocalDateTime.parse(doc.getString("Data") + " " + doc.getString("Hora"),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

					if (horaMedicao.isAfter(nowMinus5Min2)) {
						if (Math.abs(lastLeitura - m.getLeitura()) > mqttConstants.variacaoParaAnomalos
								|| anomalies.size() > 0) {
							// entra sempre que temos pelo menos 1 anomalia ja registada ou a diferenca
							// entre a lastMedicao e a atual �>2
							anomalies.add(m);
							if (anomalies.size() == mqttConstants.numeroMedicoesToleraveis) {
								for (int a = 1; a < anomalies.size(); a++) {

									if (Math.abs(anomalies.get(a - 1).getLeitura() - anomalies.get(a).getLeitura()) < 1)
										booleano++;
								}
								if (booleano != mqttConstants.numeroMedicoesToleraveis - 1) {
									anomalies.clear();
									booleano = 0;
									System.out.println(
											"           ANOMALIAS        \n *********ANOMALIAS********\n ******************");
								} else {
									for (int i = 0; i < anomalies.size(); i++) {
										if (i == anomalies.size() - 1)
											tinyint = 1;
										for (mqttParametrosCultura parametro : centralWork.getParameters(zona)) {
											mqttAlerta a;
											if (checkMinMaxTypeSensor(parametro, anomalies.get(i).getLeitura())) {
												a = new mqttAlerta(anomalies.get(i).getId(), parametro.getId(),
														chooseTipoAlerta(), " DE ULTRAPASSAGEM DE VALORES", zona,
														sensor,
														anomalies.get(i).getData() + " " + anomalies.get(i).getHora(),
														anomalies.get(i).getLeitura(), tinyint);
												centralWork.getAlertaQueue().offer(a);
												aux = 1;
											} else {
												if (checkAproximacaoSensor(parametro, anomalies.get(i).getLeitura())) {
													a = new mqttAlerta(anomalies.get(i).getId(), parametro.getId(),
															chooseTipoAlerta(), " DE APROXIMA��O DE VALORES", zona,
															sensor,
															anomalies.get(i).getData() + " "
																	+ anomalies.get(i).getHora(),
															anomalies.get(i).getLeitura(), tinyint);
													centralWork.getAlertaQueue().offer(a);
													aux = 1;
												}
											}
										}
									}
									System.out.println(
											"*********************\n******FALSA ANOMALIA******\n******� ALERTA*****");
									booleano = 0;
									tinyint = 0;
									anomalies.clear();
								}
							}
						}
					}

					lastLeitura = m.getLeitura();

					if (anomalies.size() == 0 && aux == 0) { // segue aqui para alertas normais
						sendAlertas(doc);
					}

				}

				lastMedicaoDia = doc.getString("Data");
				lastMedicaoHora = doc.getString("Hora");
			}
		}

	}

	/**
	 * @param doc
	 */
	public void sendAlertas(Document doc) {
		String nowMinus5MiString2 = LocalDateTime.now().minusSeconds(16)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		LocalDateTime nowMinus5Min2 = LocalDateTime.parse(nowMinus5MiString2,
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

		LocalDateTime horaMedicao = LocalDateTime.parse(doc.getString("Data") + " " + doc.getString("Hora"),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (horaMedicao.isAfter(nowMinus5Min2)) {

			for (mqttParametrosCultura parametro : centralWork.getParameters(zona)) {
				mqttAlerta a;
				if (checkMinMaxTypeSensor(parametro, Double.parseDouble(doc.getString("Medicao")))) {
					a = new mqttAlerta(doc.get("_id"), parametro.getId(), chooseTipoAlerta(),
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
					if (checkAproximacaoSensor(parametro, Double.parseDouble(doc.getString("Medicao")))) {
						a = new mqttAlerta(doc.get("_id"), parametro.getId(), chooseTipoAlerta(),
								" DE APROXIMA��O DE VALORES", zona, sensor,
								doc.getString("Data") + " " + doc.getString("Hora"),
								Double.parseDouble(doc.getString("Medicao")), 0);
						if (minutesToHaveAlert(parametro, a, lastAlertaAproximacao, culturaIdListAproximacao)) {
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

	/**
	 * @param parametro
	 * @param a
	 */
	public boolean minutesToHaveAlert(mqttParametrosCultura parametro, mqttAlerta a, List<mqttAlerta> lastAlerta,
			List<Integer> culturaIdList) {
		List<mqttAlerta> aux = new ArrayList<>(lastAlerta);
		if (lastAlerta.isEmpty() || !culturaIdList.contains(a.getCulturaId())) {
			lastAlerta.add(a);
			culturaIdList.add(a.getCulturaId());
			System.out.println("Nao havia last alerta!!!!");
			System.out.println("\n              ****new alerta added**** Cultura: " + parametro.getId());
			return true;
		} else {
			for (mqttAlerta alerta : aux) {

				String nowMinus5MiString = LocalDateTime.now().minusSeconds(mqttConstants.minutesToHaveAlert)
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				LocalDateTime nowMinus5Min = LocalDateTime.parse(nowMinus5MiString,
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

				if (alerta.getCulturaId() == a.getCulturaId()) {
					LocalDateTime horaAlerta = LocalDateTime.parse(alerta.getDate(),
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

					if (horaAlerta.isBefore(nowMinus5Min)) {

						System.out.println("VERDADEIRO ALERTA PARA O USER!!!!");
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

//	public static void main(String[] args) {
//		String nowMinus5MiString = LocalDateTime.now().minusSeconds(15)
//				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//		LocalDateTime nowMinus5Min = LocalDateTime.parse(nowMinus5MiString,
//				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//		String nowMinus5MiString2 = LocalDateTime.now().minusSeconds(20)
//				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//		LocalDateTime nowMinus5Min2 = LocalDateTime.parse(nowMinus5MiString2,
//				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//		System.out.println(nowMinus5Min.toString() + "   " + nowMinus5Min2.toString());
//	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		mqttWorker w = new mqttWorker();
		w.connecCloud();
		w.writeSensor("OIII");

	}
}