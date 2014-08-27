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

public class TwosComplementExerciseFragment extends
		BaseNumericalExerciseFragment {
	private static final int POINTS_FOR_QUESTION = 10;

	private int mNumberOfBits = 6;
	private int mNumberToConvert;

	public static TwosComplementExerciseFragment newInstance() {
		TwosComplementExerciseFragment fragment = new TwosComplementExerciseFragment();
		return fragment;
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.twoscomplement;
	}

	@Override
	protected boolean isResultNumeric() {
		return true;
	}

	@Override
	protected String titleString() {
		int formatStringId;
		if (mDirectConversion) {
			formatStringId = R.string.convert_dec_to_twos_complement;
		} else {
			formatStringId = R.string.convert_twos_complement_to_dec;
		}
		String formatString = getResources().getString(formatStringId);
		return String.format(formatString, mNumberOfBits);
	}

	@Override
	protected String generateRandomNumber() {
		int min = (int) -(Math.pow(2, mNumberOfBits - 1));
		int max = (int) (Math.pow(2, mNumberOfBits - 1)) - 1;

		mNumberToConvert = mRandomGenerator.nextInt(max - min + 1) + min;

		if (mDirectConversion) {
			return Integer.toString(mNumberToConvert);
		} else {
			return BinaryConverter.binaryToStringWithNbits(mNumberToConvert,
					mNumberOfBits);
		}
	}

	@Override
	protected String obtainSolution() {
		if (mDirectConversion) {
			return BinaryConverter.binaryToStringWithNbits(mNumberToConvert,
					mNumberOfBits);
		} else {
			return Integer.toString(mNumberToConvert);
		}
	}

	@Override
	protected boolean isCorrect(String answer) {
		return answer.equals(obtainSolution());
	}

	@Override
	protected int pointsForCorrectAnswer() {
		return POINTS_FOR_QUESTION;
	}

}
