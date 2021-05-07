package mqttSensorTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class mqttConstants {

//	private String mongoIniFile = "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongoReplica.ini";
	private String mongoIniFile = "CloudToMongoReplica.ini";
	// private String mongoIniFile =
	// "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao
	// Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini";
//	private String sqlIniFile = "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini";

	private final Properties mongoProperties = new Properties();
	private final Properties SqlProperties = new Properties();

	static MongoDatabase sid_db;
	static String sid_mongo_user;
	static String sid_mongo_password;
	static String sid_mongo_address;
	static String sid_mongo_replica;
	static String sid_mongo_database;
	static String sidt1_mongo_collection;
	static String sidt2_mongo_collection;
	static String sidh1_mongo_collection;
	static String sidh2_mongo_collection;
	static String sidl1_mongo_collection;
	static String sidl2_mongo_collection;
	static String sid_mongo_authentication;
	static MongoCollection<Document> colSidt1;
	static MongoCollection<Document> colSidt2;
	static MongoCollection<Document> colSidh1;
	static MongoCollection<Document> colSidh2;
	static MongoCollection<Document> colSidl1;
	static MongoCollection<Document> colSidl2;

	static MongoDatabase db;
	static String mongo_collectiont1;
	static String mongo_collectiont2;
	static String mongo_collectionh1;
	static String mongo_collectionh2;
	static String mongo_collectionl1;
	static String mongo_collectionl2;
	static String mongo_database;
	static String mongo_authentication;
	static String mongo_address;
	static String mongo_replica;
	static MongoCollection<Document> colt1;
	static MongoCollection<Document> colt2;
	static MongoCollection<Document> colh1;
	static MongoCollection<Document> colh2;
	static MongoCollection<Document> coll1;
	static MongoCollection<Document> coll2;

	static int minutesToHaveAlert;
	static int variacaoParaAnomalos;
	static int numeroMedicoesToleraveis;
	static double percentagemAviso;

	private Connection connection;
	private boolean estado_ligacao;
	private String sql_user = "aluno";
	private String sql_pwd = "aluno";

	public mqttConstants() {
		try {
			mongoProperties.load(new FileInputStream(mongoIniFile));
			assignAdminChoices();
			assignConstants();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void assignConstants() {
		mqttConstants.mongo_address = mongoProperties.getProperty("mongo_address");
		mqttConstants.mongo_authentication = mongoProperties.getProperty("mongo_authentication");
		mqttConstants.mongo_database = mongoProperties.getProperty("mongo_database");
		mqttConstants.mongo_replica = mongoProperties.getProperty("mongo_replica");
		mqttConstants.mongo_collectiont1 = mongoProperties.getProperty("mongo_collectiont1");
		mqttConstants.mongo_collectiont2 = mongoProperties.getProperty("mongo_collectiont2");
		mqttConstants.mongo_collectionh1 = mongoProperties.getProperty("mongo_collectionh1");
		mqttConstants.mongo_collectionh2 = mongoProperties.getProperty("mongo_collectionh2");
		mqttConstants.mongo_collectionl1 = mongoProperties.getProperty("mongo_collectionl1");
		mqttConstants.mongo_collectionl2 = mongoProperties.getProperty("mongo_collectionl2");

		mqttConstants.sid_mongo_user = mongoProperties.getProperty("sid_mongo_user");
		mqttConstants.sid_mongo_password = mongoProperties.getProperty("sid_mongo_password");
		mqttConstants.sid_mongo_database = mongoProperties.getProperty("sid_mongo_database");
		mqttConstants.sid_mongo_address = mongoProperties.getProperty("sid_mongo_address");
		mqttConstants.sid_mongo_authentication = mongoProperties.getProperty("sid_mongo_authentication");
		mqttConstants.sidt1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidt1");
		mqttConstants.sidt2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidt2");
		mqttConstants.sidh1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidh1");
		mqttConstants.sidh2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidh2");
		mqttConstants.sidl1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidl1");
		mqttConstants.sidl2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidl2");
	}

	public void assignSidCol() {
		mqttConstants.colSidt1 = mqttConstants.sid_db.getCollection(mqttConstants.sidt1_mongo_collection);
		mqttConstants.colSidt2 = mqttConstants.sid_db.getCollection(mqttConstants.sidt2_mongo_collection);
		mqttConstants.colSidh1 = mqttConstants.sid_db.getCollection(mqttConstants.sidh1_mongo_collection);
		mqttConstants.colSidh2 = mqttConstants.sid_db.getCollection(mqttConstants.sidh2_mongo_collection);
		mqttConstants.colSidl1 = mqttConstants.sid_db.getCollection(mqttConstants.sidl1_mongo_collection);
		mqttConstants.colSidl2 = mqttConstants.sid_db.getCollection(mqttConstants.sidl2_mongo_collection);
	}

	public void assignColGroup() {
		mqttConstants.colt1 = mqttConstants.db.getCollection(mqttConstants.mongo_collectiont1);
		mqttConstants.colt2 = mqttConstants.db.getCollection(mqttConstants.mongo_collectiont2);
		mqttConstants.colh1 = mqttConstants.db.getCollection(mqttConstants.mongo_collectionh1);
		mqttConstants.colh2 = mqttConstants.db.getCollection(mqttConstants.mongo_collectionh2);
		mqttConstants.coll1 = mqttConstants.db.getCollection(mqttConstants.mongo_collectionl1);
		mqttConstants.coll2 = mqttConstants.db.getCollection(mqttConstants.mongo_collectionl2);
	}

	public void assignColIterations() {
		mqttColIteration t2 = new mqttColIteration(colSidt2, colt2);
		mqttColIteration t1 = new mqttColIteration(colSidt1, colt1);
		mqttColIteration h1 = new mqttColIteration(colSidh1, colh1);
		mqttColIteration h2 = new mqttColIteration(colSidh2, colh2);
		mqttColIteration l1 = new mqttColIteration(colSidl1, coll1);
		mqttColIteration l2 = new mqttColIteration(colSidl2, coll2);
	}

	public void assignAdminChoices() {
		minutesToHaveAlert = Integer.parseInt(mongoProperties.getProperty("minutesToHaveAlert"));
		percentagemAviso = Double.parseDouble(mongoProperties.getProperty("percentagemAviso"));
		variacaoParaAnomalos = Integer.parseInt(mongoProperties.getProperty("variacaoParaAnomalos"));
		numeroMedicoesToleraveis = Integer.parseInt(mongoProperties.getProperty("numeroMedicoesToleraveis"));
	}

	public static void main(String[] args) {
		mqttConstants c = new mqttConstants();
		c.assignConstants();
		System.out.println(mongo_address);
	}

}
