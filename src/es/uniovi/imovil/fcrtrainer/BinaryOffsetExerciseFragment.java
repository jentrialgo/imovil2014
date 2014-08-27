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

public class BinaryOffsetExerciseFragment extends BaseNumericalExerciseFragment {

	private static final int POINTS_FOR_QUESTION = 10;
	private int mNumberOfBits = 6;
	private int mOffset = 32;
	private String mCorrectAnswer;

	public static BinaryOffsetExerciseFragment newInstance() {

		BinaryOffsetExerciseFragment fragment = new BinaryOffsetExerciseFragment();
		return fragment;
	}

	public BinaryOffsetExerciseFragment() {
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.offset_binary;
	}

	@Override
	protected boolean isResultNumeric() {
		return true;
	}

	@Override
	protected String titleString() {
		int formatStringId;
		if (mDirectConversion) {
			formatStringId = R.string.convert_dec_to_bin_offset;
		} else {
			formatStringId = R.string.convert_bin_offset_to_dec;
		}
		String formatString = getResources().getString(formatStringId);
		return String.format(formatString, mOffset, mNumberOfBits);
	}

	@Override
	protected String generateRandomNumber() {
		int maxInNaturalBinary = ((int) Math.pow(2, mNumberOfBits));
		int numberInOffset = mRandomGenerator.nextInt(maxInNaturalBinary);

		if (mDirectConversion) {
			mCorrectAnswer = BinaryConverter.binaryToStringWithNbits(
					numberInOffset, mNumberOfBits);
			return Integer.toString(numberInOffset - mOffset);
		} else {
			mCorrectAnswer = Integer.toString(numberInOffset - mOffset);
			return BinaryConverter.binaryToStringWithNbits(numberInOffset,
					mNumberOfBits);
		}
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
