package mqttSensorTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class mqttCentralWork {

	private BlockingQueue<mqttMedicao> medicaoQueue;
	private BlockingQueue<mqttAlerta> alertaQueue;
	private BlockingQueue<mqttCulturaMedicao> culturaMedicaoQueue;

	private mqttSqlDispatcher s;

	private String zona1sensorTLastMedicao;
	private String zona1sensorHLastMedicao;
	private String zona1sensorLLastMedicao;
	private String zona2sensorLLastMedicao;
	private String zona2sensorTLastMedicao;
	private String zona2sensorHLastMedicao;

	private double zona1sensorTMax;
	private double zona1sensorTMin;
	private double zona1sensorHMax;
	private double zona1sensorHMin;
	private double zona1sensorLMax;
	private double zona1sensorLMin;
	private double zona2sensorTMax;
	private double zona2sensorTMin;
	private double zona2sensorHMax;
	private double zona2sensorHMin;
	private double zona2sensorLMax;
	private double zona2sensorLMin;

	private ArrayList<mqttParametrosCultura> parametersZona1 = new ArrayList<>();
	private ArrayList<mqttParametrosCultura> parametersZona2 = new ArrayList<>();

	private mqttConstants constants;

	mqttConnections c;

	public mqttCentralWork() throws IOException {
		constants = new mqttConstants();
		medicaoQueue = new LinkedBlockingQueue<>();
		alertaQueue = new LinkedBlockingQueue<>();
		culturaMedicaoQueue = new LinkedBlockingQueue<>();
		c = new mqttConnections(this);
		s = new mqttSqlDispatcher(c.getConnection(), c.getConnectCloud(), this);
		mqttWorker worker = new mqttWorker(mqttConstants.colt1, "T", 1, this, c.getConnection());
		mqttWorker worker2 = new mqttWorker(mqttConstants.colt2, "T", 2, this, c.getConnection());
		mqttWorker worker3 = new mqttWorker(mqttConstants.colh1, "H", 1, this, c.getConnection());
		mqttWorker worker4 = new mqttWorker(mqttConstants.colh2, "H", 2, this, c.getConnection());
		mqttWorker worker5 = new mqttWorker(mqttConstants.coll1, "L", 1, this, c.getConnection());
		mqttWorker worker6 = new mqttWorker(mqttConstants.coll2, "L", 2, this, c.getConnection());
		System.out.println(parametersZona1.size());
		s.run();

	}

	public mqttConstants getConstants() {
		return constants;
	}

	public synchronized BlockingQueue<mqttMedicao> getQueueMedicao() {
		return medicaoQueue;
	}

	public synchronized BlockingQueue<mqttAlerta> getAlertaQueue() {
		return alertaQueue;
	}

	public synchronized BlockingQueue<mqttCulturaMedicao> getCulturaMedicaoQueue() {
		return culturaMedicaoQueue;
	}

	public double getSensorMin(String sensor, int zona) {
		if (sensor.equals("T")) {
			if (zona == 1)
				return zona1sensorTMin;
			if (zona == 2)
				return zona2sensorTMin;
		}
		if (sensor.equals("L")) {
			if (zona == 1)
				return zona1sensorLMin;
			if (zona == 2)
				return zona2sensorLMin;
		}
		if (sensor.equals("H")) {
			if (zona == 1)
				return zona1sensorHMin;
			if (zona == 2)
				return zona2sensorHMin;
		}
		throw new IllegalArgumentException("erro get sensor min");
	}

	public double getSensorMax(String sensor, int zona) {
		if (sensor.equals("T")) {
			if (zona == 1)
				return zona1sensorTMax;
			if (zona == 2)
				return zona2sensorTMax;
		}
		if (sensor.equals("L")) {
			if (zona == 1)
				return zona1sensorLMax;
			if (zona == 2)
				return zona2sensorLMax;
		}
		if (sensor.equals("H")) {
			if (zona == 1)
				return zona1sensorHMax;
			if (zona == 2)
				return zona2sensorHMax;
		}
		throw new IllegalArgumentException("erro get sensor max");
	}

	public String getSensorLastMedicao(String sensor, int zona) {
		if (sensor.equals("T")) {
			if (zona == 1)
				return zona1sensorTLastMedicao;
			if (zona == 2)
				return zona2sensorTLastMedicao;
		}
		if (sensor.equals("L")) {
			if (zona == 1)
				return zona1sensorLLastMedicao;
			if (zona == 2)
				return zona2sensorLLastMedicao;
		}
		if (sensor.equals("H")) {
			if (zona == 1)
				return zona1sensorHLastMedicao;
			if (zona == 2)
				return zona2sensorHLastMedicao;
		}
		throw new IllegalArgumentException("erro get sensor min");
	}

	public ArrayList<mqttParametrosCultura> getParametersZona1() {
		return parametersZona1;
	}

	public void setParametersZona1(ArrayList<mqttParametrosCultura> parametersZona1) {
		this.parametersZona1 = parametersZona1;
	}

	public ArrayList<mqttParametrosCultura> getParametersZona2() {
		return parametersZona2;
	}

	public void setParametersZona2(ArrayList<mqttParametrosCultura> parametersZona2) {
		this.parametersZona2 = parametersZona2;
	}

	public synchronized ArrayList<mqttParametrosCultura> getParameters(int zona) {
		if (zona == 1)
			return parametersZona1;
		if (zona == 2)
			return parametersZona2;
		throw new IllegalStateException();
	}

	public void setZona1sensorTLastMedicao(String zona1sensorTLastMedicao) {
		this.zona1sensorTLastMedicao = zona1sensorTLastMedicao;
	}

	public void setZona1sensorHLastMedicao(String zona1sensorHLastMedicao) {
		this.zona1sensorHLastMedicao = zona1sensorHLastMedicao;
	}

	public void setZona1sensorLLastMedicao(String zona1sensorLLastMedicao) {
		this.zona1sensorLLastMedicao = zona1sensorLLastMedicao;
	}

	public void setZona2sensorLLastMedicao(String zona2sensorLLastMedicao) {
		this.zona2sensorLLastMedicao = zona2sensorLLastMedicao;
	}

	public void setZona2sensorTLastMedicao(String zona2sensorTLastMedicao) {
		this.zona2sensorTLastMedicao = zona2sensorTLastMedicao;
	}

	public void setZona2sensorHLastMedicao(String zona2sensorHLastMedicao) {
		this.zona2sensorHLastMedicao = zona2sensorHLastMedicao;
	}

	public void setZona1sensorTMax(double zona1sensorTMax) {
		this.zona1sensorTMax = zona1sensorTMax;
	}

	public void setZona1sensorTMin(double zona1sensorTMin) {
		this.zona1sensorTMin = zona1sensorTMin;
	}

	public void setZona1sensorHMax(double zona1sensorHMax) {
		this.zona1sensorHMax = zona1sensorHMax;
	}

	public void setZona1sensorHMin(double zona1sensorHMin) {
		this.zona1sensorHMin = zona1sensorHMin;
	}

	public void setZona1sensorLMax(double zona1sensorLMax) {
		this.zona1sensorLMax = zona1sensorLMax;
	}

	public void setZona1sensorLMin(double zona1sensorLMin) {
		this.zona1sensorLMin = zona1sensorLMin;
	}

	public void setZona2sensorTMax(double zona2sensorTMax) {
		this.zona2sensorTMax = zona2sensorTMax;
	}

	public void setZona2sensorTMin(double zona2sensorTMin) {
		this.zona2sensorTMin = zona2sensorTMin;
	}

	public void setZona2sensorHMax(double zona2sensorHMax) {
		this.zona2sensorHMax = zona2sensorHMax;
	}

	public void setZona2sensorHMin(double zona2sensorHMin) {
		this.zona2sensorHMin = zona2sensorHMin;
	}

	public void setZona2sensorLMax(double zona2sensorLMax) {
		this.zona2sensorLMax = zona2sensorLMax;
	}

	public void setZona2sensorLMin(double zona2sensorLMin) {
		this.zona2sensorLMin = zona2sensorLMin;
	}

	public mqttConnections getC() {
		return c;
	}

	public static void main(String[] args) throws IOException {
		mqttCentralWork cw = new mqttCentralWork();
//		SqlDispatcher s = new SqlDispatcher(cw.getC().getConnection(), cw.getC().getConnectCloud(), cw);
//		Worker worker = new Worker(Constants.colt1, "T1", cw);
//		worker.run();
//		s.run();
	}

}
