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

package es.uniovi.imovil.fcrtrainer.digitalinformation;

import java.util.Locale;

import es.uniovi.imovil.fcrtrainer.R;

public class HexadecimalExerciseFragment extends BaseNumericalExerciseFragment {
	private static final int MAX_NUMBER_TO_CONVERT = 512;
	private static final int POINTS_FOR_QUESTION = 10;
	private int mNumberToConvert;

	public static HexadecimalExerciseFragment newInstance() {

		HexadecimalExerciseFragment fragment = new HexadecimalExerciseFragment();
		return fragment;
	}

	public HexadecimalExerciseFragment() {
	}

	protected int obtainExerciseId() {
		return R.string.hexadecimal;
	}

	@Override
	protected boolean isResultNumeric() {
		if (mDirectConversion) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected String titleString() {
		if (mDirectConversion) {
			return getResources().getString(R.string.convert_bin_to_hex);
		} else {
			return getResources().getString(R.string.convert_hex_to_bin);
		}
	}

	@Override
	protected String obtainSolution() {
		if (mDirectConversion) {
			return Integer.toHexString(mNumberToConvert).toUpperCase(Locale.US);
		} else {
			return Integer.toBinaryString(mNumberToConvert);
		}
	}

	@Override
	protected int pointsForCorrectAnswer() {
		return POINTS_FOR_QUESTION;
	}

	@Override
	protected String generateRandomNumber() {
		mNumberToConvert = mRandomGenerator.nextInt(MAX_NUMBER_TO_CONVERT);
		if (mDirectConversion) {
			return Integer.toBinaryString(mNumberToConvert);
		} else {
			return Integer.toHexString(mNumberToConvert).toUpperCase(Locale.US);
		}
	}

	@Override
	protected boolean isCorrect(String answer) {
		return obtainSolution().equals(answer.toUpperCase(Locale.US));
	}

}