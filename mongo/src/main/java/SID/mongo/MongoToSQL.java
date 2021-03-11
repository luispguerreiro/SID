package SID.mongo;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoToSQL implements Runnable {

	private String DBName = "humtemp";
	private String ColName = "humidtemper";

	// private int isMigrated;
	private String id;
	private String temperature;
	private String humidity;
	private String date;
	private String time;

	private Connection connection;
	private boolean estado_ligacao;

	private String user = "DBA";
	private String password = "sql";
	private ResultSet resultSet;

	private MongoClient mongoClient;
	private MongoDatabase database;
	private MongoCollection<Document> col;

	private List dataList;

	private Thread thread;
	private int intervalTime;

//	public MongoToSQL(int intervalTime) {
//		this.intervalTime = intervalTime;
//		this.thread=new Thread (this);
//	}
//
//	public MongoToSQL() {
//		this.thread=new Thread (this);
//	}

	public void connectSql() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();// Set driver
//			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sid_teste", "root", "");
			connection = DriverManager.getConnection("jdbc:mysql://194.210.86.10:3306/aluno_g19", "aluno", "aluno");
			estado_ligacao = true;
			System.out.println("Ligacao Estabelecida : " + estado_ligacao);
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println("Problemas de ligacao " + e.getMessage());
		}
	}

	public void connectMongo() {
		final String s = new String();
		String string = "mongodb://";
		if (CloudToMongo.mongo_authentication.equals("true")) {
			string = string + CloudToMongo.mongo_user + ":" + CloudToMongo.mongo_password + "@";
		}
		String str = string + CloudToMongo.mongo_address;
		if (!CloudToMongo.mongo_replica.equals("false")) {
			if (CloudToMongo.mongo_authentication.equals("true")) {
				str = str + "/?replicaSet=" + CloudToMongo.mongo_replica + "&authSource=admin";
			} else {
				str = str + "/?replicaSet=" + CloudToMongo.mongo_replica;
			}
		} else if (CloudToMongo.mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		CloudToMongo.db = new MongoClient(new MongoClientURI(str)).getDatabase(CloudToMongo.mongo_database);
		CloudToMongo.mongocol = (DBCollection) CloudToMongo.db.getCollection(CloudToMongo.mongo_collection);
	}

	public static void main(String[] args) throws ClassNotFoundException {
		try {
			final Properties properties = new Properties();
			properties.load(new FileInputStream(
					"C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini"));
			CloudToMongo.mongo_address = properties.getProperty("mongo_address");
			CloudToMongo.mongo_user = properties.getProperty("mongo_user");
			CloudToMongo.mongo_password = properties.getProperty("mongo_password");
			CloudToMongo.mongo_replica = properties.getProperty("mongo_replica");
			CloudToMongo.cloud_server = properties.getProperty("cloud_server");
			CloudToMongo.cloud_topic = properties.getProperty("cloud_topic");
			CloudToMongo.mongo_host = properties.getProperty("mongo_host");
			CloudToMongo.mongo_database = properties.getProperty("mongo_database");
			CloudToMongo.mongo_authentication = properties.getProperty("mongo_authentication");
			CloudToMongo.mongo_collection = properties.getProperty("mongo_collection");
			CloudToMongo.display_documents = properties.getProperty("display_documents");
		} catch (Exception obj) {
			System.out.println("Error reading CloudToMongo.ini file " + obj);
		}
//		new MongoToSQL().connectMongo();
		new MongoToSQL().connectSql();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
