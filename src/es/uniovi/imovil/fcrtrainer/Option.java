package es.uniovi.imovil.fcrtrainer;

public class Option {
	private String mOption;
	private int mId;
	
	public Option(String option, int id) {
		mOption = option;
		mId = id;
	}
	
	public String getOption() {
		return mOption;
	}
	public void setOption(String mOption) {
		this.mOption = mOption;
	}

	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}
	
	@Override
	public String toString() {
		return mOption;
	}
}
