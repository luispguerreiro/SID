import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlDispatcher implements Runnable {

	private Connection connect;
	private Connection connectCloud;

	private int zona1sensorTMax = 50;
	private int zona1sensorTMin = 0;
	private int zona1sensorHMax = 50;
	private int zona1sensorHMin = 0;
	private int zona1sensorLMax = 50;
	private int zona1sensorLMin = 0;
	private int zona2sensorTMax = 50;
	private int zona2sensorTMin = 0;
	private int zona2sensorHMax = 50;
	private int zona2sensorHMin = 0;
	private int zona2sensorLMax = 50;
	private int zona2sensorLMin = 0;

	private ArrayList<ParametrosCultura> parametersZona1 = new ArrayList<>();
	private ArrayList<ParametrosCultura> parametersZona2 = new ArrayList<>();

	private int zona1NumCulturas;
	private int zona2NumCulturas;
	private Thread thread;

	public SqlDispatcher(Connection connect) {
		this.connect = connect;
//		connectSqlCloud();
		try {
			sqlGetCulturas(parametersZona1, 1);
			sqlGetCulturas(parametersZona2, 2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
		thread.start();

	}

	public void connectSqlCloud() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connectCloud = DriverManager.getConnection("jdbc:mysql://194.210.86.10:3306/g19", "aluno", "aluno");
			boolean estado_ligacao = true;
			System.out.println("Ligacao Estabelecida ao sql : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Problemas de ligacao ao sql " + e.getMessage());
		}
	}

	public void cloudGetSensorLimits() throws SQLException {
		Statement stmt = connectCloud.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT Cultura_IdCultura FROM parametro_cultura");
		while (rs.next()) {
			String i = rs.getString("Cultura_IdCultura");
			System.out.println(i + "\n");
		}
	}

	public void sqlGetCulturas(ArrayList<ParametrosCultura> p, int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"Select pc.* from parametro_cultura pc, cultura c where pc.Cultura_IdCultura= c.IdCultura	and c.zona="
						+ zona);
//		if (rs.next() == false)
//
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		// TODO Auto-generated method stub

	}

}
