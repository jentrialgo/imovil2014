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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;

public class TwosComplementExerciseFragment extends
		BaseNumericalExerciseFragment {
	private static final String STATE_NUMBER_TO_CONVERT = "mNumberToConvert";

	private int mNumberToConvert;

	public static TwosComplementExerciseFragment newInstance() {
		return new TwosComplementExerciseFragment();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			return;
		}
		mNumberToConvert = savedInstanceState.getInt(STATE_NUMBER_TO_CONVERT);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_NUMBER_TO_CONVERT, mNumberToConvert);
	}

	@Override
	protected Screen associatedExercise() {
		return Screen.TWOS_COMPLEMENT;
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
		return String.format(formatString, numberOfBits());
	}

	@Override
	protected String generateRandomNumber() {
		int numberOfBits = numberOfBits();
		int min = (int) -(Math.pow(2, numberOfBits - 1));
		int max = (int) (Math.pow(2, numberOfBits - 1)) - 1;

		mNumberToConvert = mRandomGenerator.nextInt(max - min + 1) + min;

		if (mDirectConversion) {
			return Integer.toString(mNumberToConvert);
		} else {
			return BinaryConverter.binaryToStringWithNbits(mNumberToConvert,
					numberOfBits);
		}
	}

	@Override
	protected String obtainSolution() {
		if (mDirectConversion) {
			return BinaryConverter.binaryToStringWithNbits(mNumberToConvert,
					numberOfBits());
		} else {
			return Integer.toString(mNumberToConvert);
		}
	}

	@Override
	protected boolean isCorrect(String answer) {
		return answer.equals(obtainSolution());
	}

}
