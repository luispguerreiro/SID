import java.awt.desktop.ScreenSleepEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SqlDispatcher implements Runnable {

	private Connection connect;
	private Connection connectCloud;

	private int zona1NumCulturas;
	private int zona2NumCulturas;
	private Thread thread;

	private CentralWork centralWork;

	public SqlDispatcher(Connection connect, Connection connectCloud, CentralWork centralWork) {
		this.connect = connect;
		this.connectCloud = connectCloud;
		this.centralWork = centralWork;
		try {
//			getAllSensorLimits();
			getAllSensorLastMedicao();
			sqlGetCulturas(centralWork.getParametersZona1(), 1);
			sqlGetCulturas(centralWork.getParametersZona2(), 2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		thread = new Thread(this);
//		thread.start();

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
		if (rs.next()) {
			date = rs.getString("Hora");
		} else {
			date = LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

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

	public void sqlGetCulturas(ArrayList<ParametrosCultura> p, int zona) throws SQLException {
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(
				"Select pc.* from parametro_cultura pc, cultura c where pc.Cultura_IdCultura= c.IdCultura	and c.zona="
						+ zona + " and c.Estado=1");
//		if (rs.next() == false)
//			System.out.println("Não existem culturas na base de dados!");
		while (rs.next()) {
			numCulturasIterator(zona);
			String id = rs.getString("Cultura_IdCultura");
			p.add(new ParametrosCultura(rs.getInt("Cultura_IdCultura"), rs.getDouble("Temp_Min"),
					rs.getDouble("Temp_Max"), rs.getDouble("Humidade_Min"), rs.getDouble("Humidade_Max"),
					rs.getDouble("Luminosidade_Min"), rs.getDouble("Luminosidade_Max")));
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

	public void checkCampoAlterado(ArrayList<ParametrosCultura> p, int zona) throws SQLException {
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
					p.add(new ParametrosCultura(rs.getInt("Cultura_IdCultura"), rs.getDouble("Temp_Min"),
							rs.getDouble("Temp_Max"), rs.getDouble("Humidade_Min"), rs.getDouble("Humidade_Max"),
							rs.getDouble("Luminosidade_Min"), rs.getDouble("Luminosidade_Max")));
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

			Statement stmt;
			try {
				if (!centralWork.getQueueMedicao().isEmpty()) {
					stmt = connect.createStatement();
					Medicao medicao = centralWork.getQueueMedicao().poll();
					String id = medicao.getId();
					String timeStamp = medicao.getTimestamp();
					Double medi = medicao.getLeitura();
					String sensor = medicao.getSensorLetter();
					int zona = medicao.getZonaInt();
					String s = "INSERT INTO `sid`.`medicao` (`IdMedicao`, `Hora`, `Leitura`, `Sensor`, `Zona`) "
							+ "VALUES ('" + id + "', '" + timeStamp + "', " + medi + ", '" + sensor + "', " + zona
							+ ")";
					int rs = stmt.executeUpdate(s);
					System.out.println(s);

					insertCulturaMedicao(stmt, id, zona); // insert into cultura_medicao for that zona

				}
				if (!centralWork.getAlertaQueue().isEmpty()) {

					insertAlerta(); // insert into alerta

				}
			} catch (SQLException e) {
			}
			try { // sempre que insere, vem aqui e verifica se existe algum campo alterado a 1 na
					// tabela e atualiza esses parametrosCultura
				checkCampoAlterado(centralWork.getParameters(1), 1);
				checkCampoAlterado(centralWork.getParameters(2), 2);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * @param stmt
	 * @param id
	 * @param zona
	 * @throws SQLException
	 */
	public void insertCulturaMedicao(Statement stmt, String id, int zona) throws SQLException {
		String culturaMedicao;
		for (ParametrosCultura parameter : centralWork.getParameters(zona)) {
			culturaMedicao = "INSERT INTO `sid`.`cultura_medicao` (`Medicao_IdMedicao`, `Cultura_IdCultura`) VALUES ('"
					+ id + "', " + parameter.getId() + ")";

			int cMedicao = stmt.executeUpdate(culturaMedicao);
		}
	}

	/**
	 * @throws SQLException
	 */
	public void insertAlerta() throws SQLException {
		Statement stmt;
		stmt = connect.createStatement();
		Alerta alerta = centralWork.getAlertaQueue().poll();
		String idMedicao = alerta.getMedicaoId();
		int idCultura = alerta.getCulturaId();
		String horaEscrita = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String tipoAlerta = alerta.getTipoAlerta();
		String mensagem = alerta.getMensagem();
		int zona = alerta.getZona();
		String sensor = alerta.getSensor();
		String hora = alerta.getDate();
		int enviarAlerta = alerta.getEnviarAlerta();
		double leitura = alerta.getMedicao();
		String s = "INSERT INTO `sid`.`alerta` (`Medicao_IdMedicao`, `Cultura_IdCultura`, `IdAlerta`, `Hora_Escrita`, `TipoAlerta`, "
				+ "`Mensagem`, `Zona`, `Sensor`,`Hora`, `Leitura`, `Cultura`, `Email`, `enviarAlerta`) VALUES ('"
				+ idMedicao + "', " + idCultura + ", null, '" + horaEscrita + "', '" + tipoAlerta + "', '" + mensagem
				+ "', " + zona + ", '" + sensor + "', '" + hora + "', " + leitura + ", null, null, " + enviarAlerta
				+ ")";
		String medicao = "INSERT INTO `sid`.`medicao` (`IdMedicao`, `Hora`, `Leitura`, `Sensor`, `Zona`) " + "VALUES ('"
				+ idMedicao + "', '" + hora + "', " + leitura + ", '" + sensor + "', " + zona + ")";
		System.out.println("ALERTA:" + s);

		try {
			int rs = stmt.executeUpdate(medicao);
			insertCulturaMedicao(stmt, idMedicao, zona);
			int rs3 = stmt.executeUpdate(s);
		} catch (SQLException e) {
			try {
				int rs3 = stmt.executeUpdate(s);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
