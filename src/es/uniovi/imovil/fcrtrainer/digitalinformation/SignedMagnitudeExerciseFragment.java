/**

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

public class SignedMagnitudeExerciseFragment extends
		BaseNumericalExerciseFragment {

	private static final int POINTS_FOR_QUESTION = 10;
	private static final long GAME_DURATION_MS = 5 * 1000 * 60; // 5min

	private String mCorrectAnswer;

	public static SignedMagnitudeExerciseFragment newInstance() {
		SignedMagnitudeExerciseFragment fragment = new SignedMagnitudeExerciseFragment();
		return fragment;
	}

	public SignedMagnitudeExerciseFragment() {
		setGameDuration(GAME_DURATION_MS);
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.sign_and_magnitude;
	}

	@Override
	protected boolean isResultNumeric() {
		return true;
	}

	@Override
	protected String generateRandomNumber() {
		int numberOfBitsMagnitude = numberOfBits() - 1;
		int maxMagnitude = (int) (Math.pow(2, numberOfBitsMagnitude) - 1);
		int randomNumber = mRandomGenerator.nextInt(maxMagnitude);
		String magnitudeDecimal = String.valueOf(randomNumber);
		int sign = mRandomGenerator.nextInt(2); // it can be 0 or 1
		String signAsString = (sign == 0) ? "0" : "1";
		String magnitudeBinary = BinaryConverter.binaryToStringWithNbits(
				randomNumber, numberOfBitsMagnitude);

		if (mDirectConversion) {
			if (sign == 0) {
				mCorrectAnswer = magnitudeDecimal;
			} else {
				mCorrectAnswer = "-" + magnitudeDecimal;
			}
			return signAsString + magnitudeBinary;
		} else {
			mCorrectAnswer = signAsString + magnitudeBinary;
			if (sign == 0) {
				return magnitudeDecimal;
			} else {
				return "-" + magnitudeDecimal;
			}
		}
	}

	@Override
	protected String titleString() {
		int formatStringId;
		if (mDirectConversion) {
			formatStringId = R.string.convert_dec_to_sign_and_magnitude;
		} else {
			formatStringId = R.string.convert_sign_and_magnitude_to_dec;
		}
		String formatString = getResources().getString(formatStringId);
		return String.format(formatString, numberOfBits());
	}

	@Override
	protected String obtainSolution() {
		return mCorrectAnswer;
	}

	@Override
	protected boolean isCorrect(String answer) {
		return answer.equals(mCorrectAnswer);
	}

	@Override
	protected int pointsForCorrectAnswer() {
		return POINTS_FOR_QUESTION;
	}

}
