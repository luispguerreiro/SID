package SID.mongo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;


public class CloudToMongo implements MqttCallback {
    
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
    
    static {
        CloudToMongo.mongo_user = new String();
        CloudToMongo.mongo_password = new String();
        CloudToMongo.mongo_address = new String();
        CloudToMongo.cloud_server = new String();
        CloudToMongo.cloud_topic = new String();
        CloudToMongo.mongo_host = new String();
        CloudToMongo.mongo_replica = new String();
        CloudToMongo.mongo_database = new String();
        CloudToMongo.mongo_collection = new String();
        CloudToMongo.display_documents = new String();
        CloudToMongo.mongo_authentication = new String();
        CloudToMongo.documentLabel = new JTextArea("\n");
    }
    
    private static void createWindow() {
        final JFrame frame = new JFrame("Cloud to Mongo");
        frame.setDefaultCloseOperation(3);
        final JLabel comp = new JLabel("Data from broker: ", 0);
        comp.setPreferredSize(new Dimension(600, 30));
        final JScrollPane comp2 = new JScrollPane(CloudToMongo.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("C:\\Users\\henri\\Dropbox\\iscte\\3ºAno\\2ºSemestre\\Proj Integracao Sistemas Inf Distribuidos\\dbtools(3)\\dbtools\\CloudToMongo.ini"));
            CloudToMongo.mongo_address = properties.getProperty("mongo_address");
            CloudToMongo.mongo_user = properties.getProperty("mongo_user");
            CloudToMongo.mongo_password = properties.getProperty("mongo_password");
            CloudToMongo.mongo_replica = properties.getProperty("mongo_replica");
            CloudToMongo.cloud_server = properties.getProperty("cloud_server");
            CloudToMongo.cloud_topic = properties.getProperty("cloud_topic");
            CloudToMongo.mongo_host = properties.getProperty("mongo_host");
            CloudToMongo.mongo_database = properties.getProperty("mongo_database");
            CloudToMongo.mongo_authentication = properties.getProperty("mongo_authentication");
            CloudToMongo.mongo_collection = properties.getProperty("mongo_collection");
            CloudToMongo.display_documents = properties.getProperty("display_documents");
        }
        catch (Exception obj) {
            System.out.println("Error reading CloudToMongo.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.ini file wasn't found.", "CloudToMongo", 0);
        }
        new CloudToMongo().connecCloud();
        new CloudToMongo().connectMongo();
    }
    
    public void connecCloud() {
        try {
            (this.mqttclient = new MqttClient(CloudToMongo.cloud_server, "CloudToMongo_" + 
String.valueOf(new Random().nextInt(100000)) + "_" + CloudToMongo.cloud_topic)).connect();
            this.mqttclient.setCallback((MqttCallback)this);
            this.mqttclient.subscribe(CloudToMongo.cloud_topic);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectMongo() {
        final String s = new String();
        String string = "mongodb://";
        if (CloudToMongo.mongo_authentication.equals("true")) {
            string = string + CloudToMongo.mongo_user + ":" + CloudToMongo.mongo_password + "@";
        }
        String str = string + CloudToMongo.mongo_address;
        if (!CloudToMongo.mongo_replica.equals("false")) {
            if (CloudToMongo.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + CloudToMongo.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + CloudToMongo.mongo_replica;
            }
        }
        else if (CloudToMongo.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        db = new MongoClient(new MongoClientURI(str)).getDatabase(mongo_database);        
        
        CloudToMongo.mongocol = CloudToMongo.db.getCollection(CloudToMongo.mongo_collection);
    }
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        try {
        	
        	String test = new String(mqttMessage.toString());
        	
        	String[] campos= test.split("(?=,)");
        	
        	String[] dataComplete= campos[3].split("T");
        	String hora= dataComplete[1].replace("Z", "");
        	String data=dataComplete[0].concat("\", \"Hora\" :  \"" + hora);
        	campos[3]= data;
        	String j="";
        	String t= String.join(j, campos);
        	
            CloudToMongo.mongocol.insert(new DBObject[] { (DBObject)JSON.parse(t) });
            if (CloudToMongo.display_documents.equals("true")) {
				
                CloudToMongo.documentLabel.append(t + "\n");
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
  
}