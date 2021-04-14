package outroGrupo;

import org.bson.Document;
import nosso.ColIterationtest;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBPublisher {

	private MongoDatabase sid_db;
	private String sid_mongo_user;
	private String sid_mongo_password;
	private static String sid_mongo_address;
	private String sid_mongo_database;
	private String sid_mongo_authentication;
	private String sidt1_mongo_collection;
	private String sidt2_mongo_collection;
	private String sidh1_mongo_collection;
	private String sidh2_mongo_collection;
	private String sidl1_mongo_collection;
	private String sidl2_mongo_collection;
	private MongoCollection<Document> colSidt1;
	private MongoCollection<Document> colSidt2;
	private MongoCollection<Document> colSidh1;
	private MongoCollection<Document> colSidh2;
	private MongoCollection<Document> colSidl1;
	private MongoCollection<Document> colSidl2;

	private MongoDatabase db;
	private String mongo_database;
	private String mongo_authentication;
	private String mongo_address;
	private String mongo_replica;
	private String mongo_collectionZona1;
	private String mongo_collectionZona2;
	private MongoCollection<Document> colZona1;
	private MongoCollection<Document> colZona2;
	

	public MongoDBPublisher() {
		atributteVariables();
		connectToMongoSid();
		connectToMongoGroup();
		assignCol();
		colIterations();
		System.out.println(sid_mongo_database);
	}

	/**
	 * 
	 */
	public void atributteVariables() {
		sid_mongo_user = "aluno";
		sid_mongo_password = "aluno";
		sid_mongo_address = "194.210.86.10:27017";
		sid_mongo_database = "sid2021";
		sid_mongo_authentication = "true";
		sidt1_mongo_collection = "sensort1";
		sidt2_mongo_collection = "sensort2";
		sidh1_mongo_collection = "sensorh1";
		sidh2_mongo_collection = "sensorh2";
		sidl1_mongo_collection = "sensorl1";
		sidl2_mongo_collection = "sensorl2";

		mongo_database = "EstufaDB";
		mongo_authentication = "false";
		mongo_address = "localhost:27027,localhost:25017,localhost:23017";
		mongo_replica = "true";
		mongo_collectionZona1 = "Zona1";
		mongo_collectionZona2 = "Zona2";

	}

	public void assignCol() {
		colSidt1 = sid_db.getCollection(sidt1_mongo_collection);
		colSidt2 = sid_db.getCollection(sidt2_mongo_collection);
		colSidh1 = sid_db.getCollection(sidh1_mongo_collection);
		colSidh2 = sid_db.getCollection(sidh2_mongo_collection);
		colSidl1 = sid_db.getCollection(sidl1_mongo_collection);
		colSidl2 = sid_db.getCollection(sidl2_mongo_collection);
		
		colZona1=db.getCollection(mongo_collectionZona1);
		colZona2=db.getCollection(mongo_collectionZona2);
		
	}

	public void connectToMongoSid() {
		String s = "mongodb://";
		if (sid_mongo_authentication.equals("true")) {
			s = s + sid_mongo_user + ":" + sid_mongo_password + "@";
		}
		String str = s + sid_mongo_address;
		if (sid_mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		sid_db = new MongoClient(new MongoClientURI(str)).getDatabase(sid_mongo_database);
		System.out.println("Conexão estabelecida com sucesso a " + sid_mongo_address);

	}

	public void connectToMongoGroup() {
		String s = "mongodb://";

		String str = s + mongo_address;
		if (!mongo_replica.equals("false")) {
			if (mongo_authentication.equals("true")) {
				str = str + "/?replicaSet=" + mongo_replica + "&authSource=admin";
			} else {
				str = str + "/?replicaSet=" + mongo_replica;
			}
		} else if (mongo_authentication.equals("true")) {
			str += "/?authSource=admin";
		}
		db = new MongoClient(new MongoClientURI(str)).getDatabase(mongo_database);

		System.out.println("Conexão estabelecida com sucesso a " + mongo_address);

//		colIterations();
	}
	
	public void colIterations() {
		ColIterationtest c = new ColIterationtest(colSidt1, colZona1);
		
	}

	public static void main(String[] args) {
		MongoDBPublisher m = new MongoDBPublisher();

	}

}
