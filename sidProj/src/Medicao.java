
public class Medicao {

	private String timestamp;
	private double leitura;
	private String sensor;
	private String zona;

	public Medicao(String data, String hora, double leitura, String sensor, String zona) {
		this.timestamp = (data + " " + hora);
		this.leitura = leitura;
		this.sensor = sensor;
		this.zona = zona;
	}

	public String getTimestamp() {
		return timestamp;
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
