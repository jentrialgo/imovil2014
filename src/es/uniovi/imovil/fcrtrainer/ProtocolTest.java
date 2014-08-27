/*

Copyright 2014 Profesores y alumnos de la asignatura Informática Móvil de la EPI de Gijón

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

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
