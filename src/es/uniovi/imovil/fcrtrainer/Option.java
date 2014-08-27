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
