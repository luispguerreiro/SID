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

public class SimulateSensorLumi implements MqttCallback
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
        final JScrollPane comp2 = new JScrollPane(SimulateSensorLumi.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("SimulateSensorLumi.ini"));
            SimulateSensorLumi.display_documents = properties.getProperty("display_documents");
            SimulateSensorLumi.cloud_server = properties.getProperty("cloud_server");
            SimulateSensorLumi.cloud_topic = properties.getProperty("cloud_topic");
            SimulateSensorLumi.delay = properties.getProperty("delay");
            SimulateSensorLumi.zona = properties.getProperty("Zona");
            SimulateSensorLumi.sensor = properties.getProperty("Sensor");
            SimulateSensorLumi.valorInicial = properties.getProperty("ValorInicial");
            SimulateSensorLumi.limiteInferior = properties.getProperty("LimiteInferior");
            SimulateSensorLumi.limiteSuperior = properties.getProperty("LimiteSuperior");
            SimulateSensorLumi.variacao = properties.getProperty("Variacao");
            SimulateSensorLumi.medicoesIguais = properties.getProperty("MedicoesIguais");
            SimulateSensorLumi.medicoesEntreSalto = properties.getProperty("MedicoesEntreSalto");
            SimulateSensorLumi.valorSalto = properties.getProperty("ValorSalto");
            SimulateSensorLumi.medicoesSalto = properties.getProperty("MedicoesSalto");
        }
        catch (Exception obj) {
            System.out.println("Error reading SimulateSensor.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The SimulateSensor inifile wasn't found.", "Send Cloud Sequencial ID", 0);
        }
        new SimulateSensorLumi().connecCloud();
        new SimulateSensorLumi().generateData();
    }
    
    public void connecCloud() {
        try {
            (SimulateSensorLumi.mqttclient = new MqttClient(SimulateSensorLumi.cloud_server, "SimulateSensor" + this.getSaltString() + SimulateSensorLumi.cloud_topic)).connect();
            SimulateSensorLumi.mqttclient.setCallback((MqttCallback)this);
            SimulateSensorLumi.mqttclient.subscribe(SimulateSensorLumi.cloud_topic);
            SimulateSensorLumi.documentLabel.append("Connection To Cloud Suceeded\n");
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void generateData() {
        final double double1 = Double.parseDouble(SimulateSensorLumi.variacao);
        final double double2 = Double.parseDouble(SimulateSensorLumi.medicoesEntreSalto);
        final double double3 = Double.parseDouble(SimulateSensorLumi.limiteSuperior);
        final double double4 = Double.parseDouble(SimulateSensorLumi.limiteInferior);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String s = new String();
        int n = 0;
        int n2 = 0;
        int n3 = 1;
        double double5 = Double.parseDouble(SimulateSensorLumi.valorInicial);
        final Date date = new Date(System.currentTimeMillis());
        while (true) {
            final String string = "{" + "Zona: \"" + SimulateSensorLumi.zona + "\", " + "Sensor: \"" + SimulateSensorLumi.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + double5 + "\" " + "}";
            SimulateSensorLumi.documentLabel.append(string + "\n");
            this.writeSensor(string);
            if (!SimulateSensorLumi.delay.equals("0")) {
                try {
                    Thread.sleep(Integer.parseInt(SimulateSensorLumi.delay));
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
            if (n3 == 4 && n > Integer.parseInt(SimulateSensorLumi.medicoesIguais)) {
                n3 = 0;
                n = 0;
            }
            if (n3 == 3 && n > Integer.parseInt(SimulateSensorLumi.medicoesIguais)) {
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
                final double d = double5 + Double.parseDouble(SimulateSensorLumi.valorSalto);
                for (int i = 0; i < Integer.parseInt(SimulateSensorLumi.medicoesSalto); ++i) {
                    final String string2 = "{" + "Zona: \"" + SimulateSensorLumi.zona + "\", " + "Sensor: \"" + SimulateSensorLumi.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + d + "\" " + "}";
                    SimulateSensorLumi.documentLabel.append(string2 + "\n");
                    this.writeSensor(string2);
                    if (!SimulateSensorLumi.delay.equals("0")) {
                        try {
                            Thread.sleep(Integer.parseInt(SimulateSensorLumi.delay));
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
            SimulateSensorLumi.mqttclient.publish(SimulateSensorLumi.cloud_topic, mqttMessage);
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
        SimulateSensorLumi.cloud_server = new String();
        SimulateSensorLumi.cloud_topic = new String();
        SimulateSensorLumi.delay = new String();
        SimulateSensorLumi.zona = new String();
        SimulateSensorLumi.sensor = new String();
        SimulateSensorLumi.limiteInferior = new String();
        SimulateSensorLumi.limiteSuperior = new String();
        SimulateSensorLumi.valorInicial = new String();
        SimulateSensorLumi.variacao = new String();
        SimulateSensorLumi.medicoesIguais = new String();
        SimulateSensorLumi.medicoesEntreSalto = new String();
        SimulateSensorLumi.valorSalto = new String();
        SimulateSensorLumi.medicoesSalto = new String();
        SimulateSensorLumi.display_documents = new String();
        SimulateSensorLumi.documentLabel = new JTextArea("\n");
    }
}
