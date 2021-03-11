package SID.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import org.ini4j.*;

public class MongoCloudToMongo {
	
	public static void main(String[] args) {
		
		Wini ini;
		try {
			ini = new Wini(new File("C:\\Users\\luisg\\Desktop\\Faculdade\\3º Ano\\SID\\ConnectToCloud.ini"));
			String mongo_address = ini.get("Momgo", "mongo_address");
			String authentication = ini.get("Mongo", "mongo_authentication");
			String user = ini.get("Mongo", "mongo_user");
			String password = ini.get("Mongo", "mongo_password");
			String database = ini.get("Mongo", "mongo_database");
			String collection1 = ini.get("Mongo", "mongo_collection1");
			String collection2 = ini.get("Mongo", "mongo_collection2");
			String collection3 = ini.get("Mongo", "mongo_collection3");
			String collection4 = ini.get("Mongo", "mongo_collection4");
			String collection5 = ini.get("Mongo", "mongo_collection5");
			String collection6 = ini.get("Mongo", "mongo_collection6");
			
			System.out.println(collection3);
		} catch (InvalidFileFormatException e) {
			System.out.println("Invalid file format" + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Exception" + e.getMessage());
		}
		
	}
	
}
