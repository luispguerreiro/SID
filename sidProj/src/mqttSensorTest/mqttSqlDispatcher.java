package mqttSensorTest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class mqttSqlDispatcher implements Runnable, MqttCallback {

	private MqttClient mqttclient;

	private Connection connect;
	private Connection connectCloud;

	private int zona1NumCulturas;
	private int zona2NumCulturas;
	private Thread thread;

	private mqttCentralWork centralWork;
	
	private long time;

	public mqttSqlDispatcher(Connection connect, Connection connectCloud, mqttCentralWork centralWork) {
		this.connect = connect;
		this.connectCloud = connectCloud;
		this.centralWork = centralWork;
		time = centralWork.getStart();
		try {
			getAllSensorLimits();
			getAllSensorLastMedicao();
			sqlGetCulturas(centralWork.getParametersZona1(), 1);
			sqlGetCulturas(centralWork.getParametersZona2(), 2);
			connectmqttCloud("G19_Medicao_T");
			connectmqttCloud("G19_Alerta_T");
			connectmqttCloud("G19_Medicao_L");
			connectmqttCloud("G19_Alerta_L");
			connectmqttCloud("G19_Medicao_H");
			connectmqttCloud("G19_Alerta_H");
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		thread = new Thread(this);
//		thread.start();

	}

	public mqttSqlDispatcher() {
	}

	public void connectmqttCloud(String topic) {
		try {
			(this.mqttclient = new MqttClient("tcp://broker.mqtt-dashboard.com:1883",
					"CloudToSQL" + String.valueOf(new Random().nextInt(100000)) + topic)).connect();
			this.mqttclient.setCallback((MqttCallback) this);
			this.mqttclient.subscribe(topic);
		} catch (MqttException ex) {
			ex.printStackTrace();
		}
	}

	public double cloudGetSensorMinimumLimits(int zona, String tipoSensor) throws SQLException {
		Statement stmt = connectCloud.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT limiteinferior FROM sensor WHERE idzona=" + zona + " AND tipo= " + tipoSensor);
		double limit = 0;
		while (rs.next()) {
			limit = rs.getInt("limiteinferior");
		}
		return limit;
	}

	public int cloudGetSensorMaximumLimits(int zona, String tipoSensor) throws SQLException {
		Statement stmt = connectCloud.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT limitesuperior FROM sensor WHERE idzona=" + zona + " AND tipo= " + tipoSensor);
		int limit = 0;
		while (rs.next()) {
			limit = rs.getInt("limitesuperior");
		}
		return limit;
	}

	public String sqlGetLastMedicao(int zona, String tipoSensor) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery("select Hora from medicao where medicao.sensor= '" + tipoSensor
				+ "' and medicao.zona " + " = " + zona + " order by IdMedicao desc LIMIT 0, 1");
		String date = "";
		String nowMinus5MiString = LocalDateTime.now().minusMinutes(mqttConstants.medicoes_backupTime)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LocalDateTime nowMinus5Min = LocalDateTime.parse(nowMinus5MiString,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (rs.next()) {
			LocalDateTime horaMedicao = LocalDateTime.parse(rs.getString("Hora"),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

			if (horaMedicao.isAfter(nowMinus5Min))
				date = horaMedicao.toString();
			else
				date = nowMinus5Min.toString();
		} else {
			date = nowMinus5Min.toString();

		}
		return date;

	}

	public void getAllSensorLastMedicao() throws SQLException {
		centralWork.setZona1sensorTLastMedicao(sqlGetLastMedicao(1, "T"));
		centralWork.setZona1sensorHLastMedicao(sqlGetLastMedicao(1, "H"));
		centralWork.setZona1sensorLLastMedicao(sqlGetLastMedicao(1, "L"));
		centralWork.setZona2sensorLLastMedicao(sqlGetLastMedicao(2, "L"));
		centralWork.setZona2sensorHLastMedicao(sqlGetLastMedicao(2, "H"));
		centralWork.setZona2sensorTLastMedicao(sqlGetLastMedicao(2, "T"));

	}

	public void getAllSensorLimits() throws SQLException {
		centralWork.setZona1sensorTMin(cloudGetSensorMinimumLimits(1, "'T'"));
		centralWork.setZona1sensorHMin(cloudGetSensorMinimumLimits(1, "'H'"));
		centralWork.setZona1sensorLMin(cloudGetSensorMinimumLimits(1, "'L'"));
		centralWork.setZona2sensorTMin(cloudGetSensorMinimumLimits(2, "'T'"));
		centralWork.setZona2sensorHMin(cloudGetSensorMinimumLimits(2, "'H'"));
		centralWork.setZona2sensorLMin(cloudGetSensorMinimumLimits(2, "'L'"));
		centralWork.setZona1sensorTMax(cloudGetSensorMaximumLimits(1, "'T'"));
		centralWork.setZona1sensorHMax(cloudGetSensorMaximumLimits(1, "'H'"));
		centralWork.setZona1sensorLMax(cloudGetSensorMaximumLimits(1, "'L'"));
		centralWork.setZona2sensorTMax(cloudGetSensorMaximumLimits(2, "'T'"));
		centralWork.setZona2sensorHMax(cloudGetSensorMaximumLimits(2, "'H'"));
		centralWork.setZona2sensorLMax(cloudGetSensorMaximumLimits(2, "'L'"));
		System.out.println("**Já guardei todos os valores max e min dos sensores!**");
	}

	public void sqlGetCulturas(ArrayList<mqttParametrosCultura> p, int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"Select pc.* from parametro_cultura pc, cultura c where pc.Cultura_IdCultura= c.IdCultura	and c.zona="
						+ zona + " and c.Estado=1");
//		if (rs.next() == false)
//			System.out.println("Não existem culturas na base de dados!");
		while (rs.next()) {
			numCulturasIterator(zona);
			String id = rs.getString("Cultura_IdCultura");
			p.add(new mqttParametrosCultura(rs.getInt("Cultura_IdCultura"), rs.getDouble("Temp_Min"),
					rs.getDouble("Temp_Max"), rs.getDouble("Luminosidade_Min"), rs.getDouble("Luminosidade_Max"),
					rs.getDouble("Humidade_Min"), rs.getDouble("Humidade_Max")));
		}
	}

	// cada vez que vai escrever verifica se existem mais ou menos culturas e
	// escreve por cima do vetor de parametros
	public void getSQLNumberCulturas(int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT count(Cultura_IdCultura) as num from parametro_cultura pc , cultura c where pc.Cultura_IdCultura= c.IdCultura	and c.zona="
						+ zona);
		while (rs.next()) {
			System.out.println("-->" + rs.getInt("num"));
			if (getNumCulturas(zona) != rs.getInt("num")) {
				System.out.println(getNumCulturas(zona) + " " + rs.getInt("num"));
				centralWork.getParameters(zona).clear();
				sqlGetCulturas(centralWork.getParameters(zona), zona);
			} else
				System.out.println("numero culturas igual ao anterior");
		}
	}

	public void checkCampoAlterado(ArrayList<mqttParametrosCultura> p, int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		Statement stmt2 = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"select pc.* from parametro_cultura pc, cultura where Alterado=1 and Cultura_IdCultura=cultura.IdCultura"
						+ " and cultura.Zona=" + zona);
		while (rs.next()) {
			for (int i = 0; i < p.size(); i++) {
				System.out.println("**************ID********" + p.get(i).getId());
				if (p.get(i).getId() == rs.getInt("Cultura_IdCultura")) {
					System.out.println("**************ID********" + p.get(i).getId());
					p.remove(i);
					p.add(new mqttParametrosCultura(rs.getInt("Cultura_IdCultura"), rs.getDouble("Temp_Min"),
							rs.getDouble("Temp_Max"), rs.getDouble("Luminosidade_Min"),
							rs.getDouble("Luminosidade_Max"), rs.getDouble("Humidade_Min"),
							rs.getDouble("Humidade_Max")));
					String s = "UPDATE `sid`.`parametro_cultura` SET `Alterado` = '0' WHERE (`Cultura_IdCultura` = "
							+ rs.getInt("Cultura_IdCultura") + ")";
					int t = stmt2.executeUpdate(s);
					System.out
							.println("----------------ALTERADO----------------\n ID:" + rs.getInt("Cultura_IdCultura"));
				}
			}
		}
	}

	public void numCulturasIterator(int zona) {
		if (zona == 1)
			++zona1NumCulturas;
		if (zona == 2)
			++zona2NumCulturas;
	}

	public int getNumCulturas(int zona) {
		if (zona == 1)
			return zona1NumCulturas;
		if (zona == 2)
			return zona2NumCulturas;
		throw new IllegalStateException();

	}

	public void setNumCulturas(int zona, int newNumber) {
		if (zona == 1)
			zona1NumCulturas = newNumber;
		if (zona == 2)
			zona2NumCulturas = newNumber;
	}

	@Override
	public void run() {

		try {
//			getSQLNumberCulturas(1);
//			getSQLNumberCulturas(2);
			thread.sleep(2000);
//		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
//				System.out.println("INICIO: " + time + "\t\t ATUAL: " + System.currentTimeMillis() + "diferenca " + (time-System.currentTimeMillis()));
				
				checkCampoAlterado(centralWork.getParameters(1), 1);
				checkCampoAlterado(centralWork.getParameters(2), 2);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void connectionLost(Throwable cause) {

	}
	

	

	@Override
	public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
		Statement stmt;
		try {
			stmt = connect.createStatement();
			int rs = stmt.executeUpdate(mqttMessage.toString());

			System.out.println(mqttMessage.toString());
//			System.out.println("INICIO: " + time + "\t\t ATUAL: " + System.currentTimeMillis() + "diferenca " + (time-System.currentTimeMillis()));
			
		} catch (Exception x) {
//			x.printStackTrace();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public static void main(String[] args) {
		mqttSqlDispatcher s = new mqttSqlDispatcher();
//		s.connecCloud();
	}
}
