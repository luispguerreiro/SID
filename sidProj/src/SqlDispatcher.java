import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlDispatcher implements Runnable {

	private Connection connect;
	private Connection connectCloud;

	private int zona1sensorTMax;
	private int zona1sensorTMin;
	private int zona1sensorHMax;
	private int zona1sensorHMin;
	private int zona1sensorLMax;
	private int zona1sensorLMin;
	private int zona2sensorTMax;
	private int zona2sensorTMin;
	private int zona2sensorHMax;
	private int zona2sensorHMin;
	private int zona2sensorLMax;
	private int zona2sensorLMin;

	private ArrayList<ParametrosCultura> parametersZona1 = new ArrayList<>();
	private ArrayList<ParametrosCultura> parametersZona2 = new ArrayList<>();

	private int zona1NumCulturas;
	private int zona2NumCulturas;
	private Thread thread;

	private CentralWork centralWork;

	public SqlDispatcher(Connection connect, Connection connectCloud, CentralWork centralWork) {
		this.connect = connect;
		this.connectCloud = connectCloud;
		this.centralWork = centralWork;
		try {
			getAllSensorLimits();
			sqlGetCulturas(parametersZona1, 1);
			sqlGetCulturas(parametersZona2, 2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		thread = new Thread(this);
//		thread.start();

	}

	public int cloudGetSensorMinimumLimits(int zona, String tipo) throws SQLException {
		Statement stmt = connectCloud.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT limiteinferior FROM sensor WHERE idzona=" + zona + " AND tipo= " + tipo);
		int limit = 0;
		while (rs.next()) {
			limit = rs.getInt("limiteinferior");
		}
		return limit;
	}

	public int cloudGetSensorMaximumLimits(int zona, String tipo) throws SQLException {
		Statement stmt = connectCloud.createStatement();
		ResultSet rs = stmt
				.executeQuery("SELECT limitesuperior FROM sensor WHERE idzona=" + zona + " AND tipo= " + tipo);
		int limit = 0;
		while (rs.next()) {
			limit = rs.getInt("limitesuperior");
		}
		return limit;
	}

	public void getAllSensorLimits() throws SQLException {
		zona1sensorTMin = cloudGetSensorMinimumLimits(1, "'T'");
		zona1sensorHMin = cloudGetSensorMinimumLimits(1, "'H'");
		zona1sensorLMin = cloudGetSensorMinimumLimits(1, "'L'");
		zona2sensorTMin = cloudGetSensorMinimumLimits(2, "'T'");
		zona2sensorHMin = cloudGetSensorMinimumLimits(2, "'H'");
		zona2sensorLMin = cloudGetSensorMinimumLimits(2, "'L'");
		zona1sensorTMax = cloudGetSensorMaximumLimits(1, "'T'");
		zona1sensorHMax = cloudGetSensorMaximumLimits(1, "'H'");
		zona1sensorLMax = cloudGetSensorMaximumLimits(1, "'L'");
		zona2sensorTMax = cloudGetSensorMaximumLimits(2, "'T'");
		zona2sensorHMax = cloudGetSensorMaximumLimits(2, "'H'");
		zona2sensorLMax = cloudGetSensorMaximumLimits(2, "'L'");
	}

	public void sqlGetCulturas(ArrayList<ParametrosCultura> p, int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"Select pc.* from parametro_cultura pc, cultura c where pc.Cultura_IdCultura= c.IdCultura	and c.zona="
						+ zona);
//		if (rs.next() == false)
//			System.out.println("Não existem culturas na base de dados!");
		while (rs.next()) {
			numCulturasIterator(zona);
			String id = rs.getString("Cultura_IdCultura");
			p.add(new ParametrosCultura(rs.getInt("Cultura_IdCultura"), rs.getDouble("Temp_Min"),
					rs.getDouble("Temp_Max"), rs.getDouble("Luminosidade_Min"), rs.getDouble("Luminosidade_Max"),
					rs.getDouble("Humidade_Min"), rs.getDouble("Huminade_Max")));
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
				getParameters(zona).clear();
				sqlGetCulturas(getParameters(zona), zona);
			} else
				System.out.println("numero culturas igual ao anterior");
		}
	}

	// select Hora from medicao order by IdMedicao desc limit 1;

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

	public ArrayList<ParametrosCultura> getParameters(int zona) {
		if (zona == 1)
			return parametersZona1;
		if (zona == 2)
			return parametersZona2;
		throw new IllegalStateException();
	}

	@Override
	public void run() {

		try {
			getSQLNumberCulturas(1);
			getSQLNumberCulturas(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		while (true) {
			Statement stmt;
			try {
				if (!centralWork.getQueue().isEmpty()) {
					stmt = connect.createStatement();
					Medicao medicao = centralWork.getQueue().poll();
					String timeStamp = medicao.getTimestamp();
					Double medi =  medicao.getLeitura();
					int sensor = medicao.getSensorInt();
					int zona = medicao.getZonaInt();
					String s = "INSERT INTO `sid`.`medicao` (`IdMedicao`, `Hora`, `Leitura`, `Sensor`, `Zona`) "
							+ "VALUES (null, '"  +timeStamp  + "', " + medi + ", " + sensor + ", " + zona + ")";
					System.out.println(s);
					int rs = stmt.executeUpdate(s);
				}
			} catch (SQLException e) {
				e.printStackTrace();

			}
		}

	}

	public int getZona1sensorTMax() {
		return zona1sensorTMax;
	}

	public int getZona1sensorTMin() {
		return zona1sensorTMin;
	}

	public int getZona1sensorHMax() {
		return zona1sensorHMax;
	}

	public int getZona1sensorHMin() {
		return zona1sensorHMin;
	}

	public int getZona1sensorLMax() {
		return zona1sensorLMax;
	}

	public int getZona1sensorLMin() {
		return zona1sensorLMin;
	}

	public int getZona2sensorTMax() {
		return zona2sensorTMax;
	}

	public int getZona2sensorTMin() {
		return zona2sensorTMin;
	}

	public int getZona2sensorHMax() {
		return zona2sensorHMax;
	}

	public int getZona2sensorHMin() {
		return zona2sensorHMin;
	}

	public int getZona2sensorLMax() {
		return zona2sensorLMax;
	}

	public int getZona2sensorLMin() {
		return zona2sensorLMin;
	}

	public static void main(String[] args) {

	}

}
