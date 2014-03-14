package es.uniovi.imovil.fcrtrainer;

public class Exercise {
	private String mName;
	private int mId;
	
	public Exercise(String name, int id) {
		mName = name;
		mId = id;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}
	
	@Override
	public String toString() {
		return mName;
	}
}
