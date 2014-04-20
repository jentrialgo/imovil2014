package es.uniovi.imovil.fcrtrainer;

public class Test 
{

	
	private String question;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private String response;
	
	Test (String q, String o1, String o2, String o3, String o4, String resp)
	{
		this.question=q;
		this.option1=o1;
		this.option2=o2;
		this.option3=o3;
		this.option4=o4;
		this.response=resp;
	}
	
	public String getQuestion()
	{
		return this.question;
	}
	
	public String getOption1()
	{
		return this.option1;
	}
	
	public String getOption2()
	{
		return this.option2;
	}
	
	public String getOption3()
	{
		return this.option3;
	}
	
	public String getOption4()
	{
		return this.option4;
	}
	
	public String getResponse()
	{
		return this.response;
	}
	
	
	
}


