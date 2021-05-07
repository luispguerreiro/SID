package mqttSensorTest;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.conversions.Bson;
import java.util.Date;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.text.SimpleDateFormat;
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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;

// 
// Decompiled by Procyon v0.5.36
// 

public class mqttMongoToCloud implements MqttCallback
{
    static MqttClient mqttclient;
    static DBCollection table;
    static String mongo_user;
    static String mongo_password;
    static String cloud_server;
    static String cloud_topic;
    static String mongo_replica;
    static String mongo_address;
    static String mongo_database;
    static String mongo_collection;
    static String mongo_criteria;
    static String mongo_fieldquery;
    static String mongo_fieldvalue;
    static String delete_document;
    static String loop_query;
    static String create_backup;
    static String backup_collection;
    static String display_documents;
    static String seconds_wait;
    static JTextArea documentLabel;
    static String mongo_authentication;
    
    private static void createWindow() {
        final JFrame frame = new JFrame("Mongo to Cloud");
        frame.setDefaultCloseOperation(3);
        final JLabel comp = new JLabel("Data from mongo: ", 0);
        comp.setPreferredSize(new Dimension(600, 30));
        final JScrollPane comp2 = new JScrollPane(mqttMongoToCloud.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("MongoToCloud.ini"));
            mqttMongoToCloud.mongo_address = properties.getProperty("mongo_address");
            mqttMongoToCloud.mongo_user = properties.getProperty("mongo_user");
            mqttMongoToCloud.mongo_password = properties.getProperty("mongo_password");
            mqttMongoToCloud.mongo_database = properties.getProperty("mongo_database");
            mqttMongoToCloud.mongo_collection = properties.getProperty("mongo_collection");
            mqttMongoToCloud.mongo_fieldquery = properties.getProperty("mongo_fieldquery");
            mqttMongoToCloud.mongo_fieldvalue = properties.getProperty("mongo_fieldvalue");
            mqttMongoToCloud.delete_document = properties.getProperty("delete_document");
            mqttMongoToCloud.create_backup = properties.getProperty("create_backup");
            mqttMongoToCloud.backup_collection = properties.getProperty("backup_collection");
            mqttMongoToCloud.display_documents = properties.getProperty("display_documents");
            mqttMongoToCloud.mongo_authentication = properties.getProperty("mongo_authentication");
            mqttMongoToCloud.mongo_replica = properties.getProperty("mongo_replica");
            mqttMongoToCloud.loop_query = properties.getProperty("loop_query");
            mqttMongoToCloud.seconds_wait = properties.getProperty("delay");
            mqttMongoToCloud.cloud_server = properties.getProperty("cloud_server");
            mqttMongoToCloud.cloud_topic = properties.getProperty("cloud_topic");
        }
        catch (Exception obj) {
            System.out.println("Error reading MongoToCloud.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The MongoToCloud inifile wasn't found.", "Mongo To Cloud2", 0);
        }
        new mqttMongoToCloud().connecCloud();
        new mqttMongoToCloud().jsonToCloud();
    }
    
    protected String getSaltString() {
        final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        while (sb.length() < 18) {
            sb.append(s.charAt((int)(random.nextFloat() * s.length())));
        }
        return sb.toString();
    }
    
    public void connecCloud() {
        try {
            (mqttMongoToCloud.mqttclient = new MqttClient(mqttMongoToCloud.cloud_server, "MongoToCloud" + this.getSaltString() + mqttMongoToCloud.cloud_topic)).connect();
            mqttMongoToCloud.mqttclient.setCallback((MqttCallback)this);
            mqttMongoToCloud.mqttclient.subscribe(mqttMongoToCloud.cloud_topic);
            mqttMongoToCloud.documentLabel.append("Connection To Cloud Suceeded\n");
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void jsonToCloud() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        final String s = new String();
        final String s2 = new String();
        String string = "mongodb://";
        if (mqttMongoToCloud.mongo_authentication.equals("true")) {
            string = string + mqttMongoToCloud.mongo_user + ":" + mqttMongoToCloud.mongo_password + "@";
        }
        String str = string + mqttMongoToCloud.mongo_address;
        if (!mqttMongoToCloud.mongo_replica.equals("false")) {
            if (mqttMongoToCloud.mongo_authentication.equals("true")) {
                str = str + "/?replicaSet=" + mqttMongoToCloud.mongo_replica + "&authSource=admin";
            }
            else {
                str = str + "/?replicaSet=" + mqttMongoToCloud.mongo_replica;
            }
        }
        else if (mqttMongoToCloud.mongo_authentication.equals("true")) {
            str += "/?authSource=admin";
        }
        final MongoDatabase database = new MongoClient(new MongoClientURI(str)).getDatabase(mqttMongoToCloud.mongo_database);
        mqttMongoToCloud.documentLabel.append("Connection To Mongo Suceeded\n");
        final MongoCollection collection = database.getCollection(mqttMongoToCloud.mongo_collection);
        final MongoCollection collection2 = database.getCollection(mqttMongoToCloud.backup_collection);
        final Document document = new Document();
        if (!mqttMongoToCloud.mongo_fieldquery.equals("null")) {
            document.put(mqttMongoToCloud.mongo_fieldquery, (Object)mqttMongoToCloud.mongo_fieldvalue);
        }
        int i = 0;
        int j = 0;
        int k = 0;
        while (i == 0) {
            mqttMongoToCloud.documentLabel.append("loop number ....." + j + "\n");
            final Date date = new Date(System.currentTimeMillis());
            mqttMongoToCloud.documentLabel.append(simpleDateFormat.format(date) + "\n");
            System.out.println("loop number ....." + j + "\n");
            System.out.println(simpleDateFormat.format(date));
            this.writeSensor("{Loop:" + j + "}");
            final FindIterable find = collection.find((Bson)document);
            find.iterator();
            int n = 1;
            final MongoCursor iterator = find.projection(Projections.excludeId()).iterator();
            while (iterator.hasNext()) {
                ++j;
                ++n;
                ++k;
                final Document document2 = new Document();
                final Document document3 = (Document)iterator.next();
                final String string2 = "{id:" + k + ", doc:" + document3.toJson() + "}";
                if (mqttMongoToCloud.display_documents.equals("true")) {
                    mqttMongoToCloud.documentLabel.append(string2 + "\n");
                }
                if (mqttMongoToCloud.create_backup.equals("true")) {
                    collection2.insertOne((Object)document3);
                }
                this.writeSensor(string2);
                if (!mqttMongoToCloud.seconds_wait.equals("0")) {
                    try {
                        Thread.sleep(Integer.parseInt(mqttMongoToCloud.seconds_wait));
                    }
                    catch (Exception ex) {}
                }
            }
            if (mqttMongoToCloud.delete_document.equals("true")) {
                if (!mqttMongoToCloud.mongo_fieldquery.equals("null")) {
                    collection.deleteMany(Filters.eq(mqttMongoToCloud.mongo_fieldquery, (Object)mqttMongoToCloud.mongo_fieldvalue));
                }
                if (mqttMongoToCloud.mongo_fieldquery.equals("null")) {
                    database.getCollection(mqttMongoToCloud.mongo_collection).drop();
                }
            }
            if (!mqttMongoToCloud.loop_query.equals("true")) {
                i = 1;
            }
        }
    }
    
    public void writeSensor(final String s) {
        try {
            final MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(s.getBytes());
            mqttMongoToCloud.mqttclient.publish(mqttMongoToCloud.cloud_topic, mqttMessage);
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void connectionLost(final Throwable t) {
    }
    
    public void deliveryComplete(final IMqttDeliveryToken mqttDeliveryToken) {
    }
    
    public void messageArrived(final String s, final MqttMessage mqttMessage) {
    }
    
    static {
        mqttMongoToCloud.mongo_user = new String();
        mqttMongoToCloud.mongo_password = new String();
        mqttMongoToCloud.cloud_server = new String();
        mqttMongoToCloud.cloud_topic = new String();
        mqttMongoToCloud.mongo_replica = new String();
        mqttMongoToCloud.mongo_address = new String();
        mqttMongoToCloud.mongo_database = new String();
        mqttMongoToCloud.mongo_collection = new String();
        mqttMongoToCloud.mongo_criteria = new String();
        mqttMongoToCloud.mongo_fieldquery = new String();
        mqttMongoToCloud.mongo_fieldvalue = new String();
        mqttMongoToCloud.delete_document = new String();
        mqttMongoToCloud.loop_query = new String();
        mqttMongoToCloud.create_backup = new String();
        mqttMongoToCloud.backup_collection = new String();
        mqttMongoToCloud.display_documents = new String();
        mqttMongoToCloud.seconds_wait = new String();
        mqttMongoToCloud.documentLabel = new JTextArea("\n");
        mqttMongoToCloud.mongo_authentication = new String();
    }
}
