package outroGrupo;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class ColIteration {

	private MongoCollection<Document> colOrigin;
	private MongoCollection<Document> colDestin;


	public ColIteration(MongoCollection<Document> colOrigin, MongoCollection<Document> colDestin) {
		this.colDestin = colDestin;
		this.colOrigin = colOrigin;
		run();

	}

	public void run() {
		MongoCursor<Document> cursor = colOrigin.find().iterator();
//		colDestin.drop();
		BasicDBObject document = new BasicDBObject();
//		colDestin.deleteMany(document);
System.out.println("OLA");
		Document doc = new Document();
		
		while (cursor.hasNext()) {
			doc = cursor.next();
			String row, t = "";
			row = doc.toJson();
//			t = separateDate(row);
			doc.clear();
			System.out.println(doc.parse(row));
//			Connections.colDestin.insertOne(doc.parse(t));
			colDestin.insertOne(doc.parse(row));

		}

	}

	public String separateDate(String s) {
		String[] campos = s.split("(?=,)");

		String[] dataComplete = campos[3].split("T");
		String hora = dataComplete[1].replace("Z", "");
		String data = dataComplete[0].concat("\", \"Hora\" :  \"" + hora);
		campos[3] = data;
		String j = "";
		String t = String.join(j, campos);
		return t;

	}

}
