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

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.Level;
import es.uniovi.imovil.fcrtrainer.PreferenceUtils;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

public class FloatingPointExerciseFragment extends BaseExerciseFragment {

	private TextView mTvNumberToconvert;
	private TextView mTvTitle;
	private TextView mTvDecimal;
	private TextView mTvSign;
	private TextView mTvExponent;
	private TextView mTvMantissa;
	private EditText mEtDecimal;
	private EditText mEtSign;
	private EditText mEtExponent;
	private EditText mEtMantissa;
	private Button mCheck;
	private Button mToggle;
	private Button mSolution;
	
	boolean mIsBinary = true;
	boolean mConvert = true;
	boolean mGame = false;
	int mPoints = 0;

	float mDecimalValueF = 0.0f;
	int mfAsIntBits;
	String mBitRepresentation;
	String mBitRepresentationDivided;

	private Random mRandom = new Random();

	private static final int GAMEMODE_MAXQUESTIONS = 5;
	private static final long GAME_DURATION_MS = 5 * 1000 * 60; // 5 min

	public static FloatingPointExerciseFragment newInstance() {
		FloatingPointExerciseFragment fragment = new FloatingPointExerciseFragment();
		return fragment;
	}

	public FloatingPointExerciseFragment() {
		setGameDuration(GAME_DURATION_MS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_floatingpoint, container,
				false);

		mTvNumberToconvert = (TextView) rootView.findViewById(R.id.numbertoconvert);
		mTvDecimal = (TextView) rootView.findViewById(R.id.tv_decimal);
		mTvSign = (TextView) rootView.findViewById(R.id.tv_s);
		mTvExponent = (TextView) rootView.findViewById(R.id.tv_exp);
		mTvMantissa = (TextView) rootView.findViewById(R.id.tv_mant);
		mTvTitle = (TextView) rootView.findViewById(R.id.theme);
		mEtDecimal = (EditText) rootView.findViewById(R.id.ed_decimal);
		mEtSign = (EditText) rootView.findViewById(R.id.ed_sign);
		mEtExponent = (EditText) rootView.findViewById(R.id.ed_exponent);
		mEtMantissa = (EditText) rootView.findViewById(R.id.ed_mantissa);
		mCheck = (Button) rootView.findViewById(R.id.btn_check);
		mSolution = (Button) rootView.findViewById(R.id.btn_getsolution);
		mToggle = (Button) rootView.findViewById(R.id.btn_togglebinary);

		newDecimalQuestion();

		mCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkSolutionListener();
			}
		});

		mSolution.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				solutionListener();
			}

		});

		mToggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleListener();
			}
		});

		return rootView;
	}

	private void newDecimalQuestion() {
		generateRandomNumbers();

		mTvNumberToconvert.setText(Float.toString(mDecimalValueF));
		mIsBinary = false;

		mfAsIntBits = Float.floatToRawIntBits(mDecimalValueF);
		mEtDecimal.setVisibility(View.GONE);
		mTvDecimal.setVisibility(View.GONE);

		mEtSign.setText(null);
		mEtExponent.setText(null);
		mEtMantissa.setText(null);
	}

	private void newBinaryQuestion() {
		generateRandomNumbers();

		mBitRepresentationDivided = mBitRepresentation.substring(0, 1)
				+ " "
				+ mBitRepresentation.substring(1, 9)
				+ " "
				+ mBitRepresentation.substring(9);

		mTvNumberToconvert.setText(mBitRepresentationDivided);
		mEtDecimal.setText(null);
	}
	
	private void checkSolutionListener() {
		if (mIsBinary == true) {
			checkSolutionBinaryToDecimal();
		} else {
			checkSolutionDecimalToBinary();
		}
	}

	private void checkSolutionBinaryToDecimal() {
		String userAnswer = mEtDecimal.getEditableText().toString().trim();

		if (Float.parseFloat(userAnswer) == mDecimalValueF) {
			showAnimationAnswer(true);
			if (mGame)
				updateGameState();
			newBinaryQuestion();
		} else {
			showAnimationAnswer(false);
		}
	}

	private void checkSolutionDecimalToBinary() {
		String userAnswer = mEtSign.getEditableText().toString().trim()
				+ mEtExponent.getEditableText().toString().trim()
				+ mEtMantissa.getEditableText().toString().trim();

		if (RemoveTrailingZeroes(mBitRepresentation).equals(
				RemoveTrailingZeroes(userAnswer))) {
			showAnimationAnswer(true);
			if (mGame)
				updateGameState();
			newDecimalQuestion();
		} else {
			showAnimationAnswer(false);
		}
	}

	private void solutionListener() {
		if (mIsBinary) {
			mEtDecimal.setText(Float.toString(mDecimalValueF));
		} else {
			mEtSign.setText(mBitRepresentation.substring(0, 1));
			mEtExponent.setText(mBitRepresentation.substring(1, 9));
			mEtMantissa.setText(mBitRepresentation.substring(9));
		}
	}

	private void toggleListener() {
		if (mIsBinary) {
			newDecimalQuestion();
			mIsBinary = false;
			mTvTitle.setText(R.string.convert_to_iee);
			
			mEtDecimal.setVisibility(View.GONE);
			mTvDecimal.setVisibility(View.GONE);
			mEtSign.setVisibility(View.VISIBLE);
			mEtExponent.setVisibility(View.VISIBLE);
			mEtMantissa.setVisibility(View.VISIBLE);
			mTvSign.setVisibility(View.VISIBLE);
			mTvExponent.setVisibility(View.VISIBLE);
			mTvMantissa.setVisibility(View.VISIBLE);

		} else {
			newBinaryQuestion();
			mIsBinary = true;
			mTvTitle.setText(R.string.convert_from_iee);

			mEtDecimal.setVisibility(View.VISIBLE);
			mTvDecimal.setVisibility(View.VISIBLE);
			mEtSign.setVisibility(View.GONE);
			mEtExponent.setVisibility(View.GONE);
			mEtMantissa.setVisibility(View.GONE);
			mTvSign.setVisibility(View.GONE);
			mTvExponent.setVisibility(View.GONE);
			mTvMantissa.setVisibility(View.GONE);
		}
	}
	
	private String RemoveTrailingZeroes(String bitRepresentation) {
		int lastSignificant = bitRepresentation.length() - 1;

		while (bitRepresentation.charAt(lastSignificant) == '0') {
			lastSignificant--;
		}
		lastSignificant++;
		
		return bitRepresentation.substring(0, lastSignificant);
	}

	protected int numberOfBits() {
		Level level = PreferenceUtils.getLevel(getActivity());
		return level.numberOfBits();
	}	

	public void generateRandomNumbers() {
		int maxIntegerPart = (int) Math.pow(2, numberOfBits() - 1);		
		int intPart = mRandom.nextInt(maxIntegerPart);

		int numberOfBitsForFraction = numberOfBitsFractionalPart();
		int maxFractionalPart = (int) Math.pow(2, numberOfBitsForFraction);
		
		// Calcular el patrón de bits para la parte fraccional como un entero
		int fractionalPartBitPattern = mRandom.nextInt(maxFractionalPart);

		// Desplazarlo a la izquierda para que sea realmente fraccional
		float fractionalPart = fractionalPartBitPattern
				* (float) Math.pow(2, -numberOfBitsForFraction);
		
		mDecimalValueF = intPart + fractionalPart;

		// El cero no es un resultado válido porque no es un número normalizado
		// así que lo cambiamos por 1
		if (mDecimalValueF == 0) {
			mDecimalValueF = 1f;
		}
		
		// Decidir si es positivo o negativo con un 50% de probabilidad
		if (mRandom.nextInt(2) == 0) {
			mDecimalValueF = -mDecimalValueF;
		}
		
		mfAsIntBits = Float.floatToRawIntBits(mDecimalValueF);

		String fAsBinaryString = Integer.toBinaryString(mfAsIntBits);

		// IMPORTANT: Representation of the float number in binary form
		mBitRepresentation = String.format("%32s", fAsBinaryString).replace(' ',
				'0');
	}

	private int numberOfBitsFractionalPart() {
		Level level = PreferenceUtils.getLevel(getActivity());
		return level.numberOfBitsFractionalPart();
	}

	/**
	 * Prepares the layout for the training and game mode.
	 * 
	 * @param training
	 *            true if the change is to the training mode
	 */
	public void setTrainingMode(boolean training) {
		if (training) {
			mGame = false;
			mSolution.setVisibility(View.VISIBLE);
		} else {
			mGame = true;
			resetGameState();
			mSolution.setVisibility(View.GONE);
		}
	}

	/**
	 * Updates the game stats and if all the questions have been asked it calls
	 * the endGame() method.
	 */
	public void updateGameState() {
		mPoints++;
		updateScore(mPoints);
		if (mPoints == GAMEMODE_MAXQUESTIONS) {
			endGame();
		}
	}

	public void resetGameState() {
		mPoints = 0;
	}

	@Override
	protected void startGame() {
		super.startGame();
		setTrainingMode(false);
		updateScore(mPoints);
	}

	@Override
	protected void cancelGame() {
		super.cancelGame();
		setTrainingMode(true);
	}

	@Override
	protected void endGame() {
		int remainingTime = (int) getRemainingTimeMs() / 1000;
		mPoints = mPoints + remainingTime;
		saveScore(mPoints);
		setTrainingMode(true);

		super.endGame();
	}

	@Override
	protected int finalScore() {
		return mPoints;
	}

	@Override
	protected String gameOverMessage() {
		int remainingTime = (int) getRemainingTimeMs() / 1000;
		if (remainingTime > 0) {
			return String.format(
					getString(R.string.gameisoverexp), remainingTime, mPoints);
		} else {
			return String.format(
					getString(R.string.lost_time_over), mPoints);
		}
	}
	
	/**
	 * Saves the score using the Highscore Manager.
	 * 
	 * @param points
	 */
	public void saveScore(int points) {
		String user = getString(R.string.default_user_name);

		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					points, R.string.hexadecimal, new Date(), user, level());
		} catch (JSONException e) {
			Log.v(getClass().getSimpleName(), "Error when saving score");
		}
	}

}
