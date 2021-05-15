package mqttSensorTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class mqttConstants {

	private final String configFile = "Configurations.ini";
	private final String defaultFile = "NaoAlterar.ini";

	private final Properties appProperties = new Properties();
	private final Properties mongoProperties = new Properties();

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

	static int secondsToHaveAlert;
	static int variacaoParaAnomalos;
	static int numeroMedicoesToleraveis;
	static double percentagemAviso;
	static int medicoes_backupTime;
	static String anomalies_to_notifications;
	static String sqlUserName;
	static String sqlPassword;
	static String sql_DBName;

	public mqttConstants() {
		try {
			appProperties.load(new FileInputStream(configFile));
			mongoProperties.load(new FileInputStream(defaultFile));
			assignAdminChoices();
			assignConstants();
			Logger loggerMongo = Logger.getLogger("org.mongodb.driver");
			loggerMongo.setLevel(Level.WARNING);
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

	public void assignAdminChoices() {
		secondsToHaveAlert = Integer.parseInt(appProperties.getProperty("secondsToHaveAlert"));
		percentagemAviso = Double.parseDouble(appProperties.getProperty("percentagemAviso"));
		variacaoParaAnomalos = Integer.parseInt(appProperties.getProperty("variacaoParaAnomalos"));
		numeroMedicoesToleraveis = Integer.parseInt(appProperties.getProperty("numeroMedicoesToleraveis"));
		anomalies_to_notifications = appProperties.getProperty("anomalies_to_notifications");
		medicoes_backupTime = Integer.parseInt(appProperties.getProperty("medicoes_backupTime"));
		sqlUserName = appProperties.getProperty("sqlUserName");
		sqlPassword = appProperties.getProperty("sqlPassword");
		sql_DBName = appProperties.getProperty("sql_DBName");
	}

	public static void main(String[] args) {
		mqttConstants c = new mqttConstants();
		c.assignConstants();
		System.out.println(mongo_address);
	}

}
