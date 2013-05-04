

public class ModelParameter {

	double infectPro;
	double recoverPro;
	
	public ModelParameter(double infectPro, double recoverPro){
		this.infectPro = infectPro;
		this.recoverPro = recoverPro;
	}
	
	public double getInfectPro() {
		return infectPro;
	}
	public void setInfectPro(double infectPro) {
		this.infectPro = infectPro;
	}
	public double getRecoverPro() {
		return recoverPro;
	}
	public void setRecoverPro(double recoverPro) {
		this.recoverPro = recoverPro;
	}
}
