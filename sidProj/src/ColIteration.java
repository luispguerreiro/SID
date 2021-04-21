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
		FindIterable<Document> doc1 = colDestin.find().sort(Sorts.descending("_id"));
		Document last = doc1.first();
		if (last != null) {
			last.toString();
			System.out.println(last.get("_id"));

			List<Document> l1 = colOrigin.find(Filters.gt("_id", last.get("_id"))).into(new ArrayList<>());
			for (Document document : l1) {
				String row, t = "";
				row = document.toJson();
				t = separateDate(row);
				document.clear();
				colDestin.insertOne(document.parse(t));
				System.out.println(document.parse(t));
			}
		}

		FindIterable<Document> doc2 = colDestin.find().sort(Sorts.descending("_id"));
		Document lastBeforeUpdate = doc2.first();

		MongoCursor<Document> cursor = colOrigin.find((Filters.gt("_id", lastBeforeUpdate.get("_id")))).iterator();

		while (true) {
			FindIterable<Document> doc3 = colDestin.find().sort(Sorts.descending("_id"));
			Document doc = doc3.first();

			cursor = colOrigin.find((Filters.gt("_id", doc.get("_id")))).iterator();
			if (cursor.hasNext()) {
				doc = cursor.next();
				String row, t = "";
				row = doc.toJson();
				t = separateDate(row);
				doc.clear();
				colDestin.insertOne(doc.parse(t));
				System.out.println("--->" + doc.parse(t));
			}

		}

	}

}
