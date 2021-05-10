package jars;
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

public class CloudToMongoLumi implements MqttCallback
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
        final JScrollPane comp2 = new JScrollPane(CloudToMongoLumi.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("cloudToMongoLumi.ini"));
            CloudToMongoLumi.mongo_address = properties.getProperty("mongo_address");
            CloudToMongoLumi.mongo_user = properties.getProperty("mongo_user");
            CloudToMongoLumi.mongo_password = properties.getProperty("mongo_password");
            CloudToMongoLumi.mongo_replica = properties.getProperty("mongo_replica");
            CloudToMongoLumi.cloud_server = properties.getProperty("cloud_server");
            CloudToMongoLumi.cloud_topic = properties.getProperty("cloud_topic");
            CloudToMongoLumi.mongo_host = properties.getProperty("mongo_host");
            CloudToMongoLumi.mongo_database = properties.getProperty("mongo_database");
            CloudToMongoLumi.mongo_authentication = properties.getProperty("mongo_authentication");
            CloudToMongoLumi.mongo_collection = properties.getProperty("mongo_collection");
            CloudToMongoLumi.display_documents = properties.getProperty("display_documents");
        }
        catch (Exception obj) {
            System.out.println("Error reading CloudToMongo.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.inifile wasn't found.", "CloudToMongo", 0);
        }
        new CloudToMongoLumi().connecCloud();
        new CloudToMongoLumi().connectMongo();
    }
    
    public void connecCloud() {
        try {
            (this.mqttclient = new MqttClient(CloudToMongoLumi.cloud_server, "CloudToMongo_" + String.valueOf(new Random().nextInt(100000)) + "_" + CloudToMongoLumi.cloud_topic)).connect();
            this.mqttclient.setCallback((MqttCallback)this);
            this.mqttclient.subscribe(CloudToMongoLumi.cloud_topic);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectMongo() {
        final String s = new String();
        String string = "mongodb://";
        if (CloudToMongoLumi.mongo_authentication.equals("true")) {
            string = string + CloudToMongoLumi.mongo_user + ":" + CloudToMongoLumi.mongo_password + "@";
        }
        String str = string + CloudToMongoLumi.mongo_address;
        if (!CloudToMongoLumi.mongo_replica.equals("false")) {
            if (CloudToMongoLumi.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + CloudToMongoLumi.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + CloudToMongoLumi.mongo_replica;
            }
        }
        else if (CloudToMongoLumi.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        CloudToMongoLumi.db = new MongoClient(new MongoClientURI(str)).getDB(CloudToMongoLumi.mongo_database);
        CloudToMongoLumi.mongocol = CloudToMongoLumi.db.getCollection(CloudToMongoLumi.mongo_collection);
    }
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        try {
            CloudToMongoLumi.mongocol.insert(new DBObject[] { (DBObject)JSON.parse(mqttMessage.toString()) });
            if (CloudToMongoLumi.display_documents.equals("true")) {
                CloudToMongoLumi.documentLabel.append(mqttMessage.toString() + "\n");
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
        CloudToMongoLumi.mongo_user = new String();
        CloudToMongoLumi.mongo_password = new String();
        CloudToMongoLumi.mongo_address = new String();
        CloudToMongoLumi.cloud_server = new String();
        CloudToMongoLumi.cloud_topic = new String();
        CloudToMongoLumi.mongo_host = new String();
        CloudToMongoLumi.mongo_replica = new String();
        CloudToMongoLumi.mongo_database = new String();
        CloudToMongoLumi.mongo_collection = new String();
        CloudToMongoLumi.display_documents = new String();
        CloudToMongoLumi.mongo_authentication = new String();
        CloudToMongoLumi.documentLabel = new JTextArea("\n");
    }
}
