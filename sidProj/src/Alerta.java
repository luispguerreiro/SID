import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class Alerta {

	private int culturaId;
	private String tipoAlerta;
	private String mensagem;
	private int zona;
	private String sensor;
	private String date;
	private double medicao;
	private Object medicaoId;

	public Alerta(Object medicaoId, int culturaId, String tipoAlerta, String mensagem, int zona, String sensor,
			String date, double medicao) {
		this.medicaoId = medicaoId;
		this.culturaId = culturaId;
		this.tipoAlerta = tipoAlerta;
		this.mensagem = mensagem;
		this.zona = zona;
		this.sensor = sensor;
		this.date = date;
		this.medicao = medicao;
		writeMensagem(mensagem);
	}
	
	public void writeMensagem(String m) {
		mensagem = ("ALERTA"+m+": A cultura " + culturaId + " da zona " + zona + " atingiu uma medição de " + medicao + " no parâmetro " + tipoAlerta + " às " + date); 
	}

	public String getMedicaoId() {
		return medicaoId.toString();
	}

	public void setMedicaoId(Object medicaoId) {
		this.medicaoId = medicaoId;
	}

	public void setCulturaId(int culturaId) {
		this.culturaId = culturaId;
	}

	public void setTipoAlerta(String tipoAlerta) {
		this.tipoAlerta = tipoAlerta;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public void setZona(int zona) {
		this.zona = zona;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setMedicao(double medicao) {
		this.medicao = medicao;
	}

	public int getCulturaId() {
		return culturaId;
	}

	public String getTipoAlerta() {
		return tipoAlerta;
	}

	public String getMensagem() {
		return mensagem;
	}

	public int getZona() {
		return zona;
	}

	public String getSensor() {
		return sensor;
	}

	public String getDate() {
		return date;
	}

	public double getMedicao() {
		return medicao;
	}
	
	public static void main(String[] args) {
		System.out.println(LocalDateTime.now().minusSeconds(30).toString());
	}

}
