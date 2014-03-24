package es.uniovi.imovil.fcrtrainer;

public class LOEntrada {

	private String mEntrada;
	private int mId;
	
	public LOEntrada(String entrada, int id) {
		mEntrada = entrada;
		mId = id;
	}
	
	public String getEntrada() {
		return mEntrada;
	}
	public void setEntrada(String entrada) {
		this.mEntrada = entrada;
	}

	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}
	
	@Override
	public String toString() {
		return mEntrada;
	}

}
