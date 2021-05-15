package mqttSensorTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;

public class mqttConnections {

	/**
	 * mongoIniFile -> path to the ini file, sqlIniFile -> path to the ini file
	 * sql_user -> sql user authentication, sql_pwd -> sql user password
	 * 
	 */

	@SuppressWarnings("unused")
	private mqttConstants constants;

	private Connection connection;
	private Connection connectCloud;
	private boolean estado_ligacao;
	private mqttCentralWork cw;
	
	public mqttConnections(mqttCentralWork cw) throws IOException {
		this.cw=cw;
		constants = new mqttConstants();
		connectSql();
		connectSqlCloud();
		connectToMongoSid();
		connectToMongoGroup();
	}

	@SuppressWarnings({ "resource" })
	public void connectToMongoSid() {
		String s = "mongodb://";
		if (mqttConstants.sid_mongo_authentication.equals("true")) {
			s = s + mqttConstants.sid_mongo_user + ":" + mqttConstants.sid_mongo_password + "@";
		}
		String str = s + mqttConstants.sid_mongo_address;
		if (mqttConstants.sid_mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		mqttConstants.sid_db = new MongoClient(new MongoClientURI(str)).getDatabase(mqttConstants.sid_mongo_database);
		cw.getConstants().assignSidCol();
		System.out.println("Conexão estabelecida com sucesso a " + mqttConstants.sid_mongo_address);
	}

	@SuppressWarnings({ "resource" })
	public void connectToMongoGroup() {
		String s = "mongodb://";

		String str = s + mqttConstants.mongo_address;
		if (!mqttConstants.mongo_replica.equals("false")) {
			if (mqttConstants.mongo_authentication.equals("true")) {
				str = str + "/?replicaSet=" + mqttConstants.mongo_replica + "&authSource=admin";
			} else {
				str = str + "/?replicaSet=" + mqttConstants.mongo_replica;
			}
		} else if (mqttConstants.mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		mqttConstants.db = new MongoClient(new MongoClientURI(str)).getDatabase(mqttConstants.mongo_database);
		cw.getConstants().assignColGroup();
		System.out.println("Conexão estabelecida com sucesso a " + mqttConstants.mongo_address);

		colIterations();
	}

	
	public void colIterations() {
		mqttColIteration t2 = new mqttColIteration(mqttConstants.colSidt2, mqttConstants.colt2);
		mqttColIteration t1 = new mqttColIteration(mqttConstants.colSidt1, mqttConstants.colt1);
		mqttColIteration h1 = new mqttColIteration(mqttConstants.colSidh1, mqttConstants.colh1);
		mqttColIteration h2 = new mqttColIteration(mqttConstants.colSidh2, mqttConstants.colh2);
		mqttColIteration l1 = new mqttColIteration(mqttConstants.colSidl1, mqttConstants.coll1);
		mqttColIteration l2 = new mqttColIteration(mqttConstants.colSidl2, mqttConstants.coll2);
	}

	public void connectSql() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");  
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + mqttConstants.sql_DBName, mqttConstants.sqlUserName, mqttConstants.sqlPassword);
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
