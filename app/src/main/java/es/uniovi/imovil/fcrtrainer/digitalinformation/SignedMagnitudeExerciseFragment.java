package es.uniovi.imovil.fcrtrainer.digitalinformation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;

public class SignedMagnitudeExerciseFragment extends
		BaseNumericalExerciseFragment {
	private static final String STATE_CORRECT_ANSWER = "mCorrectAnswer";

	private String mCorrectAnswer;

	public static SignedMagnitudeExerciseFragment newInstance() {
		return new SignedMagnitudeExerciseFragment();
	}

	public SignedMagnitudeExerciseFragment() {
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			return;
		}
		mCorrectAnswer = savedInstanceState.getString(STATE_CORRECT_ANSWER);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(STATE_CORRECT_ANSWER, mCorrectAnswer);
	}

	@Override
	protected Screen associatedExercise() {
		return Screen.SIGN_MAGNITUDE;
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

}
