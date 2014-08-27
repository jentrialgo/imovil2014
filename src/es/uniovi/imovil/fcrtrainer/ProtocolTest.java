package es.uniovi.imovil.fcrtrainer;

public class ProtocolTest 
{
	private String mQuestion;
	private String[] mOption;
	private String mResponse;
	
	ProtocolTest (String q, String o1, String o2, String o3,String o4, String resp)
	{
		this.mQuestion=q;
		this.mOption = new String[ProtocolExerciseFragment.NUMBER_OF_ANSWERS];
		this.mOption[0]=o1;
		this.mOption[1]=o2;
		this.mOption[2]=o3;
		this.mOption[3]=o4;
		this.mResponse=resp;
	}
	
	public String getQuestion()
	{
		return this.mQuestion;
	}
	
	public String getOption(int index)
	{
		return mOption[index];
	}
	
	public String getResponse()
	{
		return this.mResponse;
	}
	
}
