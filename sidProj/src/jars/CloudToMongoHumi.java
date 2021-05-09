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

public class CloudToMongoHumi implements MqttCallback
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
        final JScrollPane comp2 = new JScrollPane(CloudToMongoHumi.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("cloudToMongoHumi.ini"));
            CloudToMongoHumi.mongo_address = properties.getProperty("mongo_address");
            CloudToMongoHumi.mongo_user = properties.getProperty("mongo_user");
            CloudToMongoHumi.mongo_password = properties.getProperty("mongo_password");
            CloudToMongoHumi.mongo_replica = properties.getProperty("mongo_replica");
            CloudToMongoHumi.cloud_server = properties.getProperty("cloud_server");
            CloudToMongoHumi.cloud_topic = properties.getProperty("cloud_topic");
            CloudToMongoHumi.mongo_host = properties.getProperty("mongo_host");
            CloudToMongoHumi.mongo_database = properties.getProperty("mongo_database");
            CloudToMongoHumi.mongo_authentication = properties.getProperty("mongo_authentication");
            CloudToMongoHumi.mongo_collection = properties.getProperty("mongo_collection");
            CloudToMongoHumi.display_documents = properties.getProperty("display_documents");
        }
        catch (Exception obj) {
            System.out.println("Error reading CloudToMongo.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The CloudToMongo.inifile wasn't found.", "CloudToMongo", 0);
        }
        new CloudToMongoHumi().connecCloud();
        new CloudToMongoHumi().connectMongo();
    }
    
    public void connecCloud() {
        try {
            (this.mqttclient = new MqttClient(CloudToMongoHumi.cloud_server, "CloudToMongo_" + String.valueOf(new Random().nextInt(100000)) + "_" + CloudToMongoHumi.cloud_topic)).connect();
            this.mqttclient.setCallback((MqttCallback)this);
            this.mqttclient.subscribe(CloudToMongoHumi.cloud_topic);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectMongo() {
        final String s = new String();
        String string = "mongodb://";
        if (CloudToMongoHumi.mongo_authentication.equals("true")) {
            string = string + CloudToMongoHumi.mongo_user + ":" + CloudToMongoHumi.mongo_password + "@";
        }
        String str = string + CloudToMongoHumi.mongo_address;
        if (!CloudToMongoHumi.mongo_replica.equals("false")) {
            if (CloudToMongoHumi.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + CloudToMongoHumi.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + CloudToMongoHumi.mongo_replica;
            }
        }
        else if (CloudToMongoHumi.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        CloudToMongoHumi.db = new MongoClient(new MongoClientURI(str)).getDB(CloudToMongoHumi.mongo_database);
        CloudToMongoHumi.mongocol = CloudToMongoHumi.db.getCollection(CloudToMongoHumi.mongo_collection);
    }
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) throws Exception {
        try {
            CloudToMongoHumi.mongocol.insert(new DBObject[] { (DBObject)JSON.parse(mqttMessage.toString()) });
            if (CloudToMongoHumi.display_documents.equals("true")) {
                CloudToMongoHumi.documentLabel.append(mqttMessage.toString() + "\n");
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
        CloudToMongoHumi.mongo_user = new String();
        CloudToMongoHumi.mongo_password = new String();
        CloudToMongoHumi.mongo_address = new String();
        CloudToMongoHumi.cloud_server = new String();
        CloudToMongoHumi.cloud_topic = new String();
        CloudToMongoHumi.mongo_host = new String();
        CloudToMongoHumi.mongo_replica = new String();
        CloudToMongoHumi.mongo_database = new String();
        CloudToMongoHumi.mongo_collection = new String();
        CloudToMongoHumi.display_documents = new String();
        CloudToMongoHumi.mongo_authentication = new String();
        CloudToMongoHumi.documentLabel = new JTextArea("\n");
    }
}
