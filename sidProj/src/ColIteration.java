import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

public class ColIteration implements Runnable {

	private MongoCollection<Document> colOrigin;
	private MongoCollection<Document> colDestin;

	private Thread thread;

	public ColIteration(MongoCollection<Document> colOrigin, MongoCollection<Document> colDestin) {
		this.colDestin = colDestin;
		this.colOrigin = colOrigin;

		thread = new Thread(this);
		thread.start();

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

	@Override
	public void run() {
		MongoCursor<Document> cursorFirst = colDestin.find().iterator();
		FindIterable<Document> doc1 = colDestin.find().sort(Sorts.descending("Data"));
//		System.out.println(doc1.first().get("_id"));
		Document o = doc1.first();
		if (o != null) {
			o.toString();

//		FindIterable<Document> currentVersionDocumentIterable =   
//		    colDestin.find().sort(new Document("_id", -1)).limit(1);
//		System.out.println(colDestin.find().sort(new Document("_id", -1)).limit(1));

			List<Document> l1 = colOrigin.find(Filters.gte("_id", o.get("_id"))).into(new ArrayList<>());
			for (Document document : l1) {
				System.out.println("ol2");
				System.out.println(document.toJson());
			}
		}
//		colDestin.drop();
			BasicDBObject document = new BasicDBObject();
			MongoCursor<Document> cursor = colOrigin.find().iterator();
		colDestin.deleteMany(document);
		Document doc = new Document();
		while (true) {
			doc = cursor.next();
			String row, t = "";
			row = doc.toJson();
			t = separateDate(row);
			doc.clear();
//			System.out.println(doc.parse(t));
//			Connections.colDestin.insertOne(doc.parse(t));
			colDestin.insertOne(doc.parse(t));

		}

	}
	
}


