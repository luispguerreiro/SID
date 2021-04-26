import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bson.Document;

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
		MongoCursor<Document> cursor;
		Document last = doc1.first();

		cursor = colOrigin.find((Filters.gt("Data",
				LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")))))
				.iterator();

		//Quando o mongo local está vazio
		if ((last == null)) {
			if (cursor.hasNext()) {
				System.out.println("aqui last era null");
				Document doc5 = cursor.next();
				String t1 = extracted(doc5);
				System.out.println("->" + doc5.parse(t1));
			}
		} else {
			//quando nao está vazio, verifica-se se o ID da medicao de ha 1h existe no mongo local,
			//se nao existir vamos inserir
			if (cursor.hasNext()) {
				Document doc5 = cursor.next();
				FindIterable<Document> docid = colDestin.find(Filters.eq("_id", doc5.get("_id")));
				Document docdoc = docid.first();
				System.out.println("aqui last nao era null mas vamos ver se ja existia la esse doc...");

				if (docdoc != null && !docdoc.get("_id").equals(doc5.get("_id"))) {
					System.out.println("aqui docdoc diferente do cursor de 1h atras");
					System.out.println(last.get("_id") + " --- " + doc5.get("_id"));
					String t1 = extracted(doc5);
					System.out.println("->->" + doc5.parse(t1));
				}
			}
		}

		MongoCursor<Document> cursor2 = null;
		// a partir daqui é sempre a andar :)
		while (true) {

			FindIterable<Document> doc3 = colDestin.find().sort(Sorts.descending("_id"));

			Document doc = doc3.first();
			cursor2 = colOrigin.find((Filters.gt("_id", doc.get("_id")))).iterator();
			if (cursor2.hasNext()) {
				doc = cursor2.next();
				String row, t = "";
				row = doc.toJson();
				t = separateDate(row);
				doc.clear();
				colDestin.insertOne(doc.parse(t));
//				System.out.println("->->->" + doc.parse(t));
			}
		}

	}

	/**
	 * @param doc5
	 * @return
	 */
	public String extracted(Document doc5) {
		String row1, t1 = "";
		row1 = doc5.toJson();
		t1 = separateDate(row1);
		doc5.clear();
		colDestin.insertOne(doc5.parse(t1));
		return t1;
	}

	public static void main(String[] args) {

		System.out.println(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}

}
