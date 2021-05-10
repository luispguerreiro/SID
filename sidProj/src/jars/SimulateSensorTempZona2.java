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

public class SimulateSensorTempZona2 implements MqttCallback
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
        final JScrollPane comp2 = new JScrollPane(SimulateSensorTempZona2.documentLabel, 22, 32);
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
            properties.load(new FileInputStream("SimulateSensorTempZona2.ini"));
            SimulateSensorTempZona2.display_documents = properties.getProperty("display_documents");
            SimulateSensorTempZona2.cloud_server = properties.getProperty("cloud_server");
            SimulateSensorTempZona2.cloud_topic = properties.getProperty("cloud_topic");
            SimulateSensorTempZona2.delay = properties.getProperty("delay");
            SimulateSensorTempZona2.zona = properties.getProperty("Zona");
            SimulateSensorTempZona2.sensor = properties.getProperty("Sensor");
            SimulateSensorTempZona2.valorInicial = properties.getProperty("ValorInicial");
            SimulateSensorTempZona2.limiteInferior = properties.getProperty("LimiteInferior");
            SimulateSensorTempZona2.limiteSuperior = properties.getProperty("LimiteSuperior");
            SimulateSensorTempZona2.variacao = properties.getProperty("Variacao");
            SimulateSensorTempZona2.medicoesIguais = properties.getProperty("MedicoesIguais");
            SimulateSensorTempZona2.medicoesEntreSalto = properties.getProperty("MedicoesEntreSalto");
            SimulateSensorTempZona2.valorSalto = properties.getProperty("ValorSalto");
            SimulateSensorTempZona2.medicoesSalto = properties.getProperty("MedicoesSalto");
        }
        catch (Exception obj) {
            System.out.println("Error reading SimulateSensor.ini file " + obj);
            JOptionPane.showMessageDialog(null, "The SimulateSensor inifile wasn't found.", "Send Cloud Sequencial ID", 0);
        }
        new SimulateSensorTempZona2().connecCloud();
        new SimulateSensorTempZona2().generateData();
    }
    
    public void connecCloud() {
        try {
            (SimulateSensorTempZona2.mqttclient = new MqttClient(SimulateSensorTempZona2.cloud_server, "SimulateSensor" + this.getSaltString() + SimulateSensorTempZona2.cloud_topic)).connect();
            SimulateSensorTempZona2.mqttclient.setCallback((MqttCallback)this);
            SimulateSensorTempZona2.mqttclient.subscribe(SimulateSensorTempZona2.cloud_topic);
            SimulateSensorTempZona2.documentLabel.append("Connection To Cloud Suceeded\n");
        }
        catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    
    public void generateData() {
        final double double1 = Double.parseDouble(SimulateSensorTempZona2.variacao);
        final double double2 = Double.parseDouble(SimulateSensorTempZona2.medicoesEntreSalto);
        final double double3 = Double.parseDouble(SimulateSensorTempZona2.limiteSuperior);
        final double double4 = Double.parseDouble(SimulateSensorTempZona2.limiteInferior);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        final String s = new String();
        int n = 0;
        int n2 = 0;
        int n3 = 1;
        double double5 = Double.parseDouble(SimulateSensorTempZona2.valorInicial);
        final Date date = new Date(System.currentTimeMillis());
        while (true) {
            final String string = "{" + "Zona: \"" + SimulateSensorTempZona2.zona + "\", " + "Sensor: \"" + SimulateSensorTempZona2.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + double5 + "\" " + "}";
            SimulateSensorTempZona2.documentLabel.append(string + "\n");
            this.writeSensor(string);
            if (!SimulateSensorTempZona2.delay.equals("0")) {
                try {
                    Thread.sleep(Integer.parseInt(SimulateSensorTempZona2.delay));
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
            if (n3 == 4 && n > Integer.parseInt(SimulateSensorTempZona2.medicoesIguais)) {
                n3 = 0;
                n = 0;
            }
            if (n3 == 3 && n > Integer.parseInt(SimulateSensorTempZona2.medicoesIguais)) {
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
                final double d = double5 + Double.parseDouble(SimulateSensorTempZona2.valorSalto);
                for (int i = 0; i < Integer.parseInt(SimulateSensorTempZona2.medicoesSalto); ++i) {
                    final String string2 = "{" + "Zona: \"" + SimulateSensorTempZona2.zona + "\", " + "Sensor: \"" + SimulateSensorTempZona2.sensor + "\", " + "Data: \"" + simpleDateFormat.format(new Date(System.currentTimeMillis())) + "\", " + "Medicao: \"" + d + "\" " + "}";
                    SimulateSensorTempZona2.documentLabel.append(string2 + "\n");
                    this.writeSensor(string2);
                    if (!SimulateSensorTempZona2.delay.equals("0")) {
                        try {
                            Thread.sleep(Integer.parseInt(SimulateSensorTempZona2.delay));
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
            SimulateSensorTempZona2.mqttclient.publish(SimulateSensorTempZona2.cloud_topic, mqttMessage);
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
        SimulateSensorTempZona2.cloud_server = new String();
        SimulateSensorTempZona2.cloud_topic = new String();
        SimulateSensorTempZona2.delay = new String();
        SimulateSensorTempZona2.zona = new String();
        SimulateSensorTempZona2.sensor = new String();
        SimulateSensorTempZona2.limiteInferior = new String();
        SimulateSensorTempZona2.limiteSuperior = new String();
        SimulateSensorTempZona2.valorInicial = new String();
        SimulateSensorTempZona2.variacao = new String();
        SimulateSensorTempZona2.medicoesIguais = new String();
        SimulateSensorTempZona2.medicoesEntreSalto = new String();
        SimulateSensorTempZona2.valorSalto = new String();
        SimulateSensorTempZona2.medicoesSalto = new String();
        SimulateSensorTempZona2.display_documents = new String();
        SimulateSensorTempZona2.documentLabel = new JTextArea("\n");
    }
}
