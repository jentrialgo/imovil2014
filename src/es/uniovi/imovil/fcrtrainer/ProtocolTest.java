package es.uniovi.imovil.fcrtrainer;

public class ProtocolTest 
{

	
	private String question;
	private String[] option;
	private String response;
	
	ProtocolTest (String q, String o1, String o2, String o3,String o4, String resp)
	{
		this.question=q;
		this.option = new String[ProtocolExerciseFragment.NUMBER_OF_ANSWERS];
		this.option[0]=o1;
		this.option[1]=o2;
		this.option[2]=o3;
		this.option[3]=o4;
		this.response=resp;
	}
	
	public String getQuestion()
	{
		return this.question;
	}
	
	public String getOption(int index)
	{
		return option[index];
	}
	
	public String getResponse()
	{
		return this.response;
	}
	


}





