
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class Connections {

	/**
	 * mongoIniFile -> path to the ini file, sqlIniFile -> path to the ini file
	 * sql_user -> sql user authentication, sql_pwd -> sql user password
	 * 
	 */

	private String mongoIniFile = "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongoReplica.ini";
	// private String mongoIniFile =
	// "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao
	// Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini";
//	private String sqlIniFile = "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini";

	private Constants constants;

	private Connection connection;
	private boolean estado_ligacao;
	private String sql_user = "aluno";
	private String sql_pwd = "aluno";

	public Connections() throws IOException {
		constants = new Constants();
		constants.assignConstants();
//		colIterations();
		connectSql();
		connectToMongoSid();
		connectToMongoGroup();

		for (int i = 0; i < 6; i++) {
			Thread t = new Thread();
			t.start();
		}
	}

	@SuppressWarnings({ "resource" })
	public void connectToMongoSid() {
		String s = "mongodb://";
		if (Constants.sid_mongo_authentication.equals("true")) {
			s = s + Constants.sid_mongo_user + ":" + Constants.sid_mongo_password + "@";
		}
		String str = s + Constants.sid_mongo_address;
		if (Constants.sid_mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		Constants.sid_db = new MongoClient(new MongoClientURI(str)).getDatabase(Constants.sid_mongo_database);
		constants.assignSidCol();
		System.out.println("Conexão estabelecida com sucesso a " + Constants.sid_mongo_address);

		// AQUI TEM DE SER THREADS
//		constants.assignColIterations();
	}

	@SuppressWarnings({ "resource" })
	public void connectToMongoGroup() {
		String s = "mongodb://";

		String str = s + Constants.mongo_address;
		if (!Constants.mongo_replica.equals("false")) {
			if (Constants.mongo_authentication.equals("true")) {
				str = str + "/?replicaSet=" + Constants.mongo_replica + "&authSource=admin";
			} else {
				str = str + "/?replicaSet=" + Constants.mongo_replica;
			}
		} else if (Constants.mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		Constants.db = new MongoClient(new MongoClientURI(str)).getDatabase(Constants.mongo_database);
		constants.assignColGroup();
		
		System.out.println("Conexão estabelecida com sucesso a " + Constants.mongo_address);

		colIterations();
		// AQUI TEM DE SER THREADS
//		constants.assignColIterations();
	}

	public void colIterations() {
//		ColIteration t2 = new ColIteration(Constants.colSidt2, Constants.colt2);
		ColIteration t1 = new ColIteration(Constants.colSidt1, Constants.colt1);
//		ColIteration h1 = new ColIteration(Constants.colSidh1, Constants.colh1);
//		ColIteration h2 = new ColIteration(Constants.colSidh2, Constants.colh2);
//		ColIteration l1 = new ColIteration(Constants.colSidl1, Constants.coll1);
//		ColIteration l2 = new ColIteration(Constants.colSidl2, Constants.coll2);
	}

	public void connectSql() {
		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");// Set driver
			Class.forName("com.mysql.jdbc.Driver");  
//			connection = DriverManager.getConnection("jdbc:mysql://194.210.86.10:3306/g19", "aluno", "aluno");
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/sid_teste", "root", "");
			estado_ligacao = true;
			System.out.println("Ligacao Estabelecida ao sql : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Problemas de ligacao ao sql " + e.getMessage());
		}
	}

	public static void main(String[] args) throws IOException {
		Connections c = new Connections();

//		c.connectSql();
	}

}
