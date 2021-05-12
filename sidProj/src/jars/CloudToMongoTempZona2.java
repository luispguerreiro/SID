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

public class CloudToMongoTempZona2 implements MqttCallback
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
        final JScrollPane comp2 = new JScrollPane(CloudToMongoTempZona2.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("cloudToMongo.ini"));
            CloudToMongoTempZona2.mongo_address = properties.getProperty("mongo_address");
            CloudToMongoTempZona2.mongo_user = properties.getProperty("mongo_user");
            CloudToMongoTempZona2.mongo_password = properties.getProperty("mongo_password");
            CloudToMongoTempZona2.mongo_replica = properties.getProperty("mongo_replica");
            CloudToMongoTempZona2.cloud_server = properties.getProperty("cloud_server");
            CloudToMongoTempZona2.cloud_topic = properties.getProperty("cloud_topic");
            CloudToMongoTempZona2.mongo_host = properties.getProperty("mongo_host");
            CloudToMongoTempZona2.mongo_database = properties.getProperty("mongo_database");
            CloudToMongoTempZona2.mongo_authentication = properties.getProperty("mongo_authentication");
            CloudToMongoTempZona2.mongo_collection = properties.getProperty("mongo_collection");
            CloudToMongoTempZona2.display_documents = properties.getProperty("display_documents");
        }
        catch (Exception obj) {
            System.out.println("Error reading CloudToMongo.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.inifile wasn't found.", "CloudToMongo", 0);
        }
        new CloudToMongoTempZona2().connecCloud();
        new CloudToMongoTempZona2().connectMongo();
    }
    
    public void connecCloud() {
        try {
            (this.mqttclient = new MqttClient(CloudToMongoTempZona2.cloud_server, "CloudToMongo_" + String.valueOf(new Random().nextInt(100000)) + "_" + CloudToMongoTempZona2.cloud_topic)).connect();
            this.mqttclient.setCallback((MqttCallback)this);
            this.mqttclient.subscribe(CloudToMongoTempZona2.cloud_topic);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectMongo() {
        final String s = new String();
        String string = "mongodb://";
        if (CloudToMongoTempZona2.mongo_authentication.equals("true")) {
            string = string + CloudToMongoTempZona2.mongo_user + ":" + CloudToMongoTempZona2.mongo_password + "@";
        }
        String str = string + CloudToMongoTempZona2.mongo_address;
        if (!CloudToMongoTempZona2.mongo_replica.equals("false")) {
            if (CloudToMongoTempZona2.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + CloudToMongoTempZona2.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + CloudToMongoTempZona2.mongo_replica;
            }
        }
        else if (CloudToMongoTempZona2.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        CloudToMongoTempZona2.db = new MongoClient(new MongoClientURI(str)).getDB(CloudToMongoTempZona2.mongo_database);
        CloudToMongoTempZona2.mongocol = CloudToMongoTempZona2.db.getCollection(CloudToMongoTempZona2.mongo_collection);
    }
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        try {
            CloudToMongoTempZona2.mongocol.insert(new DBObject[] { (DBObject)JSON.parse(mqttMessage.toString()) });
            if (CloudToMongoTempZona2.display_documents.equals("true")) {
                CloudToMongoTempZona2.documentLabel.append(mqttMessage.toString() + "\n");
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
        CloudToMongoTempZona2.mongo_user = new String();
        CloudToMongoTempZona2.mongo_password = new String();
        CloudToMongoTempZona2.mongo_address = new String();
        CloudToMongoTempZona2.cloud_server = new String();
        CloudToMongoTempZona2.cloud_topic = new String();
        CloudToMongoTempZona2.mongo_host = new String();
        CloudToMongoTempZona2.mongo_replica = new String();
        CloudToMongoTempZona2.mongo_database = new String();
        CloudToMongoTempZona2.mongo_collection = new String();
        CloudToMongoTempZona2.display_documents = new String();
        CloudToMongoTempZona2.mongo_authentication = new String();
        CloudToMongoTempZona2.documentLabel = new JTextArea("\n");
    }
}
