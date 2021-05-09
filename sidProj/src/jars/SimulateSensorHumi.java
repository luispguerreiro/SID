package jars;
import java.util.Random;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.eclipse.paho.client.mqttv3.MqttException;
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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;

// 
// Decompiled by Procyon v0.5.36
// 

public class SimulateSensorHumi implements MqttCallback
{
    static MqttClient mqttclient;
    static String cloud_server;
    static String cloud_topic;
    static String delay;
    static String zona;
    static String sensor;
    static String limiteInferior;
    static String limiteSuperior;
    static String valorInicial;
    static String variacao;
    static String medicoesIguais;
    static String medicoesEntreSalto;
    static String valorSalto;
    static String medicoesSalto;
    static String display_documents;
    static JTextArea documentLabel;
    
    private static void createWindow() {
        final JFrame frame = new JFrame("SimulateSensorHumi");
        frame.setDefaultCloseOperation(3);
        final JLabel comp = new JLabel("Data to Cloud: ", 0);
        comp.setPreferredSize(new Dimension(600, 30));
        final JScrollPane comp2 = new JScrollPane(SimulateSensorHumi.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("SimulateSensorHumi.ini"));
            SimulateSensorHumi.display_documents = properties.getProperty("display_documents");
            SimulateSensorHumi.cloud_server = properties.getProperty("cloud_server");
            SimulateSensorHumi.cloud_topic = properties.getProperty("cloud_topic");
            SimulateSensorHumi.delay = properties.getProperty("delay");
            SimulateSensorHumi.zona = properties.getProperty("Zona");
            SimulateSensorHumi.sensor = properties.getProperty("Sensor");
            SimulateSensorHumi.valorInicial = properties.getProperty("ValorInicial");
            SimulateSensorHumi.limiteInferior = properties.getProperty("LimiteInferior");
            SimulateSensorHumi.limiteSuperior = properties.getProperty("LimiteSuperior");
            SimulateSensorHumi.variacao = properties.getProperty("Variacao");
            SimulateSensorHumi.medicoesIguais = properties.getProperty("MedicoesIguais");
            SimulateSensorHumi.medicoesEntreSalto = properties.getProperty("MedicoesEntreSalto");
            SimulateSensorHumi.valorSalto = properties.getProperty("ValorSalto");
            SimulateSensorHumi.medicoesSalto = properties.getProperty("MedicoesSalto");
        }
        catch (Exception obj) {
            System.out.println("Error reading SimulateSensor.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The SimulateSensor inifile wasn't found.", "Send Cloud Sequencial ID", 0);
        }
        new SimulateSensorHumi().connecCloud();
        new SimulateSensorHumi().generateData();
    }
    
    public void connecCloud() {
        try {
            (SimulateSensorHumi.mqttclient = new MqttClient(SimulateSensorHumi.cloud_server, "SimulateSensor" + this.getSaltString() + SimulateSensorHumi.cloud_topic)).connect();
            SimulateSensorHumi.mqttclient.setCallback((MqttCallback)this);
            SimulateSensorHumi.mqttclient.subscribe(SimulateSensorHumi.cloud_topic);
            SimulateSensorHumi.documentLabel.append("Connection To Cloud Suceeded\n");
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void generateData() {
        final double double1 = Double.parseDouble(SimulateSensorHumi.variacao);
        final double double2 = Double.parseDouble(SimulateSensorHumi.medicoesEntreSalto);
        final double double3 = Double.parseDouble(SimulateSensorHumi.limiteSuperior);
        final double double4 = Double.parseDouble(SimulateSensorHumi.limiteInferior);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String s = new String();
        int n = 0;
        int n2 = 0;
        int n3 = 1;
        double double5 = Double.parseDouble(SimulateSensorHumi.valorInicial);
        final Date date = new Date(System.currentTimeMillis());
        while (true) {
            final String string = "{" + "Zona: \"" + SimulateSensorHumi.zona + "\", " + "Sensor: \"" + SimulateSensorHumi.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + double5 + "\" " + "}";
            SimulateSensorHumi.documentLabel.append(string + "\n");
            this.writeSensor(string);
            if (!SimulateSensorHumi.delay.equals("0")) {
                try {
                    Thread.sleep(Integer.parseInt(SimulateSensorHumi.delay));
                }
                catch (Exception ex) {}
            }
            if (n3 == 1 && double5 > double3) {
                n3 = 4;
            }
            if (n3 == 0 && double5 < double4) {
                n3 = 3;
            }
            if (n3 > 2) {
                ++n;
            }
            if (n3 == 4 && n > Integer.parseInt(SimulateSensorHumi.medicoesIguais)) {
                n3 = 0;
                n = 0;
            }
            if (n3 == 3 && n > Integer.parseInt(SimulateSensorHumi.medicoesIguais)) {
                n3 = 1;
                n = 0;
            }
            if (n3 == 1) {
                double5 += double1;
            }
            if (n3 == 0) {
                double5 -= double1;
            }
            if (++n2 > double2) {
                n2 = 0;
                final double d = double5 + Double.parseDouble(SimulateSensorHumi.valorSalto);
                for (int i = 0; i < Integer.parseInt(SimulateSensorHumi.medicoesSalto); ++i) {
                    final String string2 = "{" + "Zona: \"" + SimulateSensorHumi.zona + "\", " + "Sensor: \"" + SimulateSensorHumi.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + d + "\" " + "}";
                    SimulateSensorHumi.documentLabel.append(string2 + "\n");
                    this.writeSensor(string2);
                    if (!SimulateSensorHumi.delay.equals("0")) {
                        try {
                            Thread.sleep(Integer.parseInt(SimulateSensorHumi.delay));
                        }
                        catch (Exception ex2) {}
                    }
                }
            }
        }
    }
    
    public void writeSensor(final String s) {
        try {
            final MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(s.getBytes());
            SimulateSensorHumi.mqttclient.publish(SimulateSensorHumi.cloud_topic, mqttMessage);
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
    
    protected String getSaltString() {
        final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        while (sb.length() < 18) {
            sb.append(s.charAt((int)(random.nextFloat() * s.length())));
        }
        return sb.toString();
    }
    
    static {
        SimulateSensorHumi.cloud_server = new String();
        SimulateSensorHumi.cloud_topic = new String();
        SimulateSensorHumi.delay = new String();
        SimulateSensorHumi.zona = new String();
        SimulateSensorHumi.sensor = new String();
        SimulateSensorHumi.limiteInferior = new String();
        SimulateSensorHumi.limiteSuperior = new String();
        SimulateSensorHumi.valorInicial = new String();
        SimulateSensorHumi.variacao = new String();
        SimulateSensorHumi.medicoesIguais = new String();
        SimulateSensorHumi.medicoesEntreSalto = new String();
        SimulateSensorHumi.valorSalto = new String();
        SimulateSensorHumi.medicoesSalto = new String();
        SimulateSensorHumi.display_documents = new String();
        SimulateSensorHumi.documentLabel = new JTextArea("\n");
    }
}
