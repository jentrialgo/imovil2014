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

public enum Level {
	BEGINNER (4),
	INTERMEDIATE (6),
	PROFICIENCY (8);
	
	private final int mNumberOfBits;
	
	Level(int numberOfBits) {
		mNumberOfBits = numberOfBits;
	}
	
	public int numberOfBits() {
		return mNumberOfBits;
	}
}
