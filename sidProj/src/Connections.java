
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

public class Connections {

	/**
	 * mongoIniFile -> path to the ini file, sqlIniFile -> path to the ini file
	 * sql_user -> sql user authentication, sql_pwd -> sql user password
	 * 
	 */


	@SuppressWarnings("unused")
	private Constants constants;

	private Connection connection;
	private Connection connectCloud;
	private boolean estado_ligacao;
	
	private CentralWork cw;
	


	public Connections(CentralWork cw) throws IOException {
		this.cw=cw;
		constants = new Constants();
		connectSql();
		connectSqlCloud(); //comentado enquanto stor tem o sql cloud desligado
		connectToMongoSid();
		connectToMongoGroup();
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
		cw.getConstants().assignSidCol();
		System.out.println("Conexão estabelecida com sucesso a " + Constants.sid_mongo_address);

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
		cw.getConstants().assignColGroup();
		System.out.println("Conexão estabelecida com sucesso a " + Constants.mongo_address);

		colIterations();
	}

	
	public void colIterations() {
		ColIteration t2 = new ColIteration(Constants.colSidt2, Constants.colt2);
		ColIteration t1 = new ColIteration(Constants.colSidt1, Constants.colt1);
		ColIteration h1 = new ColIteration(Constants.colSidh1, Constants.colh1);
		ColIteration h2 = new ColIteration(Constants.colSidh2, Constants.colh2);
		ColIteration l1 = new ColIteration(Constants.colSidl1, Constants.coll1);
		ColIteration l2 = new ColIteration(Constants.colSidl2, Constants.coll2);
	}

	public void connectSql() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");  
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + Constants.sql_DBName, Constants.sqlUserName, Constants.sqlPassword);
			estado_ligacao = true;
			System.out.println("Ligacao Estabelecida ao sql : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Problemas de ligacao ao sql " + e.getMessage());
		}
	}
	
	
	
	public void connectSqlCloud() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connectCloud = DriverManager.getConnection("jdbc:mysql://194.210.86.10:3306/sid2021", "aluno", "aluno");
			boolean estado_ligacao = true;
			System.out.println("Ligacao Estabelecida ao sql : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Problemas de ligacao ao sql " + e.getMessage());
		}
	}
	
	public Connection getConnectCloud() {
		return connectCloud;
	}
	
	public Connection getConnection() {
		return connection;
	}
}
