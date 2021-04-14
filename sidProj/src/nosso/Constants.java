package nosso;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Constants {

	private String mongoIniFile = "C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongoReplica.ini";
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
	
	private Connection connection;
	private boolean estado_ligacao;
	private String sql_user = "aluno";
	private String sql_pwd = "aluno";

	public void assignConstants() {
		try {
			mongoProperties.load(new FileInputStream(mongoIniFile));
//			SqlProperties.load(new FileInputStream(sqlIniFile));

			mongo_address = mongoProperties.getProperty("mongo_address");
			mongo_authentication = mongoProperties.getProperty("mongo_authentication");
			mongo_database = mongoProperties.getProperty("mongo_database");
			mongo_replica = mongoProperties.getProperty("mongo_replica");
			mongo_collectiont1 = mongoProperties.getProperty("mongo_collectiont1a");
			mongo_collectiont2 = mongoProperties.getProperty("mongo_collectiont2a");
//			mongo_collectionh1 = mongoProperties.getProperty("mongo_collectionh1");
//			mongo_collectionh2 = mongoProperties.getProperty("mongo_collectionh2");
//			mongo_collectionl1 = mongoProperties.getProperty("mongo_collectionl1");
//			mongo_collectionl2 = mongoProperties.getProperty("mongo_collectionl2");

			sid_mongo_user = mongoProperties.getProperty("sid_mongo_user");
			sid_mongo_password = mongoProperties.getProperty("sid_mongo_password");
			sid_mongo_database = mongoProperties.getProperty("sid_mongo_database");
			sid_mongo_address = mongoProperties.getProperty("sid_mongo_address");
			sid_mongo_authentication = mongoProperties.getProperty("sid_mongo_authentication");
			sidt1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidt1");
			sidt2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidt2");
			sidh1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidh1");
			sidh2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidh2");
			sidl1_mongo_collection = mongoProperties.getProperty("mongo_collection_sidl1");
			sidl2_mongo_collection = mongoProperties.getProperty("mongo_collection_sidl2");

		} catch (IOException obj) {
			System.out.println("Error reading ini file " + obj);
		}
	}
	
	public void assignSidCol() {
		colSidt1 = sid_db.getCollection(sidt1_mongo_collection);
		colSidt2 = sid_db.getCollection(sidt2_mongo_collection);
		colSidh1 = sid_db.getCollection(sidh1_mongo_collection);
		colSidh2 = sid_db.getCollection(sidh2_mongo_collection);
		colSidl1 = sid_db.getCollection(sidl1_mongo_collection);
		colSidl2 = sid_db.getCollection(sidl2_mongo_collection);
	}
	
	public void assignColGroup() {
		colt1 = db.getCollection(mongo_collectiont1);
		colt2 = db.getCollection(mongo_collectiont2);
//		colh1 = db.getCollection(mongo_collectionh1);
//		colh2 = db.getCollection(mongo_collectionh2);
//		coll1 = db.getCollection(mongo_collectionl1);
//		coll2 = db.getCollection(mongo_collectionl2);
	}
	
	
	public static void main(String[] args) {
		Constants c = new Constants();
		c.assignConstants();
		c.assignColGroup();
		System.out.println(mongo_address);
	}

}
