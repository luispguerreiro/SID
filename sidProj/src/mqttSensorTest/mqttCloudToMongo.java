package mqttSensorTest;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import com.mongodb.util.JSON;
import com.mongodb.DBObject;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.mongodb.MongoClientURI;
import org.eclipse.paho.client.mqttv3.MqttException;
import java.util.Random;
import javax.swing.JOptionPane;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import com.mongodb.DBCollection;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;

// 
// Decompiled by Procyon v0.5.36
// 

public class mqttCloudToMongo implements MqttCallback
{
    MqttClient mqttclient;
    static MongoClient mongoClient;
    static DB db;
    static DBCollection mongocol;
    static String mongo_user;
    static String mongo_password;
    static String mongo_address;
    static String cloud_server;
    static String cloud_topic;
    static String mongo_host;
    static String mongo_replica;
    static String mongo_database;
    static String mongo_collection;
    static String display_documents;
    static String mongo_authentication;
    static JTextArea documentLabel;
    
    private static void createWindow() {
        final JFrame frame = new JFrame("Cloud to Mongo");
        frame.setDefaultCloseOperation(3);
        final JLabel comp = new JLabel("Data from broker: ", 0);
        comp.setPreferredSize(new Dimension(600, 30));
        final JScrollPane comp2 = new JScrollPane(mqttCloudToMongo.documentLabel, 22, 32);
        comp2.setPreferredSize(new Dimension(600, 200));
        final JButton comp3 = new JButton("Stop the program");
        frame.getContentPane().add(comp, "First");
        frame.getContentPane().add(comp2, "Center");
        frame.getContentPane().add(comp3, "Last");
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        comp3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                System.exit(0);
            }
        });
    }
    
    public static void main(final String[] array) {
        createWindow();
        try {
            final Properties properties = new Properties();
            properties.load(new FileInputStream("C:\\Users\\henri\\Downloads\\dbtools(3) (1)\\dbtools\\CloudToMongo.ini"));
            mqttCloudToMongo.mongo_address = properties.getProperty("mongo_address");
            mqttCloudToMongo.mongo_user = properties.getProperty("mongo_user");
            mqttCloudToMongo.mongo_password = properties.getProperty("mongo_password");
            mqttCloudToMongo.mongo_replica = properties.getProperty("mongo_replica");
            mqttCloudToMongo.cloud_server = properties.getProperty("cloud_server");
            mqttCloudToMongo.cloud_topic = properties.getProperty("cloud_topic");
            mqttCloudToMongo.mongo_host = properties.getProperty("mongo_host");
            mqttCloudToMongo.mongo_database = properties.getProperty("mongo_database");
            mqttCloudToMongo.mongo_authentication = properties.getProperty("mongo_authentication");
            mqttCloudToMongo.mongo_collection = properties.getProperty("mongo_collection");
            mqttCloudToMongo.display_documents = properties.getProperty("display_documents");
        }
        catch (Exception obj) {
            System.out.println("Error reading CloudToMongo.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.inifile wasn't found.", "CloudToMongo", 0);
        }
        new mqttCloudToMongo().connecCloud();
        new mqttCloudToMongo().connectMongo();
    }
    
    public void connecCloud() {
        try {
            (this.mqttclient = new MqttClient(mqttCloudToMongo.cloud_server, "CloudToMongo_" + String.valueOf(new Random().nextInt(100000)) + "_" + mqttCloudToMongo.cloud_topic)).connect();
            this.mqttclient.setCallback((MqttCallback)this);
            this.mqttclient.subscribe(mqttCloudToMongo.cloud_topic);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectMongo() {
        final String s = new String();
        String string = "mongodb://";
        if (mqttCloudToMongo.mongo_authentication.equals("true")) {
            string = string + mqttCloudToMongo.mongo_user + ":" + mqttCloudToMongo.mongo_password + "@";
        }
        String str = string + mqttCloudToMongo.mongo_address;
        if (!mqttCloudToMongo.mongo_replica.equals("false")) {
            if (mqttCloudToMongo.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + mqttCloudToMongo.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + mqttCloudToMongo.mongo_replica;
            }
        }
        else if (mqttCloudToMongo.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        mqttCloudToMongo.db = new MongoClient(new MongoClientURI(str)).getDB(mqttCloudToMongo.mongo_database);
        mqttCloudToMongo.mongocol = mqttCloudToMongo.db.getCollection(mqttCloudToMongo.mongo_collection);
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
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        try {
            mqttCloudToMongo.mongocol.insert(new DBObject[] { (DBObject)JSON.parse(mqttMessage.toString()) });
            if (mqttCloudToMongo.display_documents.equals("true")) {
                mqttCloudToMongo.documentLabel.append(mqttMessage.toString() + "\n");
            }
        }
        catch (Exception x) {
            System.out.println(x);
        }
    }
    
    public void connectionLost(final Throwable t) {
    }
    
    public void deliveryComplete(final IMqttDeliveryToken mqttDeliveryToken) {
    }
    
    static {
        mqttCloudToMongo.mongo_user = new String();
        mqttCloudToMongo.mongo_password = new String();
        mqttCloudToMongo.mongo_address = new String();
        mqttCloudToMongo.cloud_server = new String();
        mqttCloudToMongo.cloud_topic = new String();
        mqttCloudToMongo.mongo_host = new String();
        mqttCloudToMongo.mongo_replica = new String();
        mqttCloudToMongo.mongo_database = new String();
        mqttCloudToMongo.mongo_collection = new String();
        mqttCloudToMongo.display_documents = new String();
        mqttCloudToMongo.mongo_authentication = new String();
        mqttCloudToMongo.documentLabel = new JTextArea("\n");
    }
}
