package mqttSensorTest;

public class mqttCulturaMedicao {
	
	private String IdMedicao;
	private int IdCultura;
	
	public mqttCulturaMedicao(String IdMedicao, int IdCultura) {
		this.IdCultura=IdCultura;
		this.IdMedicao=IdMedicao;
	}

	public int getIdCultura() {
		return IdCultura;
	}
	
	public String getIdMedicao() {
		return IdMedicao;
	}
}
