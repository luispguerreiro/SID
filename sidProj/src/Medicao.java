
public class Medicao {

	private String timestamp;
	private double leitura;
	private String sensor;
	private String zona;
	private Object id;

	public Medicao(Object id, String data, String hora, double leitura, String sensor, String zona) {
		this.timestamp = (data + " " + hora);
		this.leitura = leitura;
		this.sensor = sensor;
		this.zona = zona;
		this.id=id;
	}

	public String getTimestamp() {
		return timestamp;
	}
	
	public String getId() {
		return id.toString();
	}

	public int getZonaInt() {
		return Integer.parseInt(zona.substring(1));

	}
	
	public String getSensorLetter() {
		return sensor.substring(0,1);
	}

	public double getLeitura() {
		return leitura;
	}

	public String getSensor() {
		return sensor;
	}
	
	public static void main(String[] args) {
		
		System.out.println(("T1").substring(0,1));
	}

}
