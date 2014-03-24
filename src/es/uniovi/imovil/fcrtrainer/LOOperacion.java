package es.uniovi.imovil.fcrtrainer;

public class LOOperacion {
	private String mOperacion;
	private int mId;
	
	public LOOperacion(String operacion, int id) {
		mOperacion = operacion;
		mId = id;
	}
	
	public String getOperacion() {
		return mOperacion;
	}
	public void setOperacion(String operacion) {
		this.mOperacion = operacion;
	}

	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}

	@Override
	public String toString() {
		return mOperacion;
	}
	
}
