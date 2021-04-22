
public class ParametrosCultura {

	private final int id;
	private double temp_min;
	private double temp_max;
	private double humidade_min;
	private double humidade_max;
	private double luminosidade_min;
	private double luminosidade_max;
	
	private boolean alterado;

	public ParametrosCultura(int id, double d, double e, double f, double g,
			double h, double i) {
		this.id = id;
		this.temp_min = d;
		this.temp_max = e;
		this.humidade_min = f;
		this.humidade_max = g;
		this.luminosidade_min = h;
		this.luminosidade_max = i;
		alterado = false;
	}
	
	public String tostring() {
		
		return (id + " " + temp_min + " " + temp_max + " " + humidade_min + " " + humidade_max + " " + luminosidade_min + " " + luminosidade_max );
		
	}
	
	public void setAlterado(boolean alterado) {
		this.alterado = alterado;
	}
	
	public boolean getAlterado(){
		return alterado;
	}

	public void setHumidade_max(double humidade_max) {
		this.humidade_max = humidade_max;
	}

	public void setHumidade_min(double humidade_min) {
		this.humidade_min = humidade_min;
	}

	public void setLuminosidade_max(double luminosidade_max) {
		this.luminosidade_max = luminosidade_max;
	}

	public void setLuminosidade_min(double luminosidade_min) {
		this.luminosidade_min = luminosidade_min;
	}

	public void setTemp_max(double temp_max) {
		this.temp_max = temp_max;
	}

	public void setTemp_min(double temp_min) {
		this.temp_min = temp_min;
	}

	public double getHumidade_max() {
		return humidade_max;
	}

	public double getHumidade_min() {
		return humidade_min;
	}

	public int getId() {
		return id;
	}

	public double getLuminosidade_max() {
		return luminosidade_max;
	}

	public double getLuminosidade_min() {
		return luminosidade_min;
	}

	public double getTemp_max() {
		return temp_max;
	}

	public double getTemp_min() {
		return temp_min;
	}
	
	

}
