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

import android.content.Context;

public enum Level {
	BEGINNER(4, 1, 1),
	INTERMEDIATE(6, 2, 2),
	PROFICIENCY(8, 3, 4);

	private final int mNumberOfBits; // for numerical exercises
	private final int mNumberOfBitsFractionalPart; // for floating point
	private final int mScoreMultiplier;

	Level(int numberOfBits, int numberOfBitsFractonalPart, int scoreMultiplier) {
		mNumberOfBits = numberOfBits;
		mNumberOfBitsFractionalPart = numberOfBitsFractonalPart;
		mScoreMultiplier = scoreMultiplier;
	}

	public int numberOfBits() {
		return mNumberOfBits;
	}

	public int numberOfBitsFractionalPart() {
		return mNumberOfBitsFractionalPart;
	}
	
	public static Level fromString(Context context, String string) {
		String[] levelNames = context.getResources().getStringArray(
				R.array.pref_level_values);
		for (int i = 0; i < levelNames.length; i++) {
			if (string.equalsIgnoreCase(levelNames[i])) {
				return Level.values()[i];
			}
		}

		throw new IllegalArgumentException();
	}
	
	public String toString(Context context) {
		switch (this) {
		case BEGINNER:
			return context.getString(R.string.pref_level1_name);
		case INTERMEDIATE:
			return context.getString(R.string.pref_level2_name);
		case PROFICIENCY:
			return context.getString(R.string.pref_level3_name);
		}

		throw new IllegalArgumentException();
	}

	public int scoreMultiplier() {
		return mScoreMultiplier;
	}
}
