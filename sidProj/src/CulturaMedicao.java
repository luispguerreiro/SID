
public class CulturaMedicao {
	
	private String IdMedicao;
	private int IdCultura;
	
	public CulturaMedicao(String IdMedicao, int IdCultura) {
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
