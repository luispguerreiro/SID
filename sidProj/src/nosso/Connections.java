package nosso;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bson.Document;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.mysql.cj.xdevapi.JsonArray;

public class Connections {

	/**
	 * mongoIniFile -> path to the ini file, sqlIniFile -> path to the ini file
	 * sql_user -> sql user authentication, sql_pwd -> sql user password
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
		ColIterationtest t2 = new ColIterationtest(Constants.colSidt2, Constants.colt2);
		ColIterationtest t1 = new ColIterationtest(Constants.colSidt1, Constants.colt1);
		ColIterationtest h1 = new ColIterationtest(Constants.colSidh1, Constants.colh1);
		ColIterationtest h2 = new ColIterationtest(Constants.colSidh2, Constants.colh2);
		ColIterationtest l1 = new ColIterationtest(Constants.colSidl1, Constants.coll1);
		ColIterationtest l2 = new ColIterationtest(Constants.colSidl2, Constants.coll2);
	}

	public void connectSql() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();// Set driver
//			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sid_teste", "root", "");
			connection = DriverManager.getConnection("jdbc:mysql://194.210.86.10:3306/aluno_g19", sql_user, sql_pwd);
			estado_ligacao = true;
			System.out.println("Ligacao Estabelecida ao sql : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println("Problemas de ligacao ao sql " + e.getMessage());
		}
	}

	public static void main(String[] args) throws IOException {
		Connections c = new Connections();

//		c.connectSql();
	}

}
