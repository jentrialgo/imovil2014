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

import es.uniovi.imovil.fcrtrainer.R;


/**
 * Ejercicio que, en su modo directo, pregunta por la traducci�n de binario a
 * decimal y en el modo inverso, lo contrario
 */
public class BinaryExerciseFragment extends BaseNumericalExerciseFragment {

	private BinaryConverter binaryConverter = new BinaryConverter();

	public static BinaryExerciseFragment newInstance() {
		BinaryExerciseFragment fragment = new BinaryExerciseFragment();

		return fragment;
	}

	public BinaryExerciseFragment() {
	}
	
	private String convertToDecimal(String textToDecimal) {
		return BinaryConverter.convertBinaryToDecimal(textToDecimal);
	}

	private String convertToBinary(String textToBinary) {
		return BinaryConverter.convertDecimalToBinary(textToBinary);
	}

	private boolean checkDecimalAnswer(String answer) {
		String answerConverted = convertToBinary(answer);
		return answerConverted.equals(mNumberToConvert);
	}

	private boolean checkBinaryAnswer(String answer) {
		// get answer without zeros in front to compare it easy.
		answer = BinaryConverter.deleteStartingZeroesFromBinaryInput(answer);

		// Convert Question to binary
		String questionConverted = convertToBinary(mNumberToConvert);
		return answer.equals(questionConverted);
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.binary;
	}

	@Override
	protected boolean isResultNumeric() {
		return true;
	}

	@Override
	protected String titleString() {
		int formatStringId;
		if (mDirectConversion) {
			formatStringId = R.string.convert_dec_to_bin;
		} else {
			formatStringId = R.string.convert_bin_to_dec;
		}
		String formatString = getResources().getString(formatStringId);
		return String.format(formatString, numberOfBits());
	}

	@Override
	protected String generateRandomNumber() {
		int number = binaryConverter.createRandomNumber(numberOfBits());

		if (mDirectConversion) {
			return String.valueOf(number);
		} else {
			return BinaryConverter.convertDecimalToBinary(number);
		}
	}

	@Override
	protected String obtainSolution() {
		if (mDirectConversion) {
			return convertToBinary(mNumberToConvert);
		} else {
			return convertToDecimal(mNumberToConvert);
		}
	}

	@Override
	protected boolean isCorrect(String answer) {
		if (mDirectConversion) {
			return checkBinaryAnswer(answer);
		} else {
			return checkDecimalAnswer(answer);
		}
	}
}
