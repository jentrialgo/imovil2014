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
import java.util.Random;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class BaseNumericalExerciseFragment extends
		BaseExerciseFragment {
	private static final String STATE_DIRECT_CONVERSION = "mDirectConversion";
	private static final String STATE_NUMBER_TO_CONVERT = "mNumberToConvert";
	private static final String STATE_QUESTION_COUNTER = "mQuestionCounter";

	private static final int GAMEMODE_MAXQUESTIONS = 5;

	private EditText mAnswerTextView;
	private Button mCheckButton;
	private Button mChangeDirectionButton;
	private Button mSolutionButton;
	private TextView mNumberToConvertTextView;
	private TextView mTitleTextView;
	protected String mNumberToConvert;
	private int mQuestionCounter = 0;

	protected Random mRandomGenerator;
	protected boolean mDirectConversion = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_numerical_exercise,
				container, false);

		mAnswerTextView = (EditText) rootView.findViewById(R.id.answer);
		mChangeDirectionButton = (Button) rootView.findViewById(R.id.change);
		mSolutionButton = (Button) rootView.findViewById(R.id.seesolution);
		mCheckButton = (Button) rootView.findViewById(R.id.checkbutton);
		mNumberToConvertTextView = (TextView) rootView
				.findViewById(R.id.numbertoconvert);
		mTitleTextView = (TextView) rootView.findViewById(R.id.exercisetitle);
		mRandomGenerator = new Random();

		mAnswerTextView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					checkSolution(mAnswerTextView.getEditableText().toString()
							.trim().toLowerCase(Locale.US));
				}
				return false;
			}
		});

		// Set the focus on the edit text and show the keyboard
		if (mAnswerTextView.requestFocus()) {
			getActivity().getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}

		mCheckButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkSolution(mAnswerTextView.getEditableText().toString()
						.trim().toLowerCase(Locale.US));
			}
		});

		mChangeDirectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDirectConversion ^= true;
				setKeyboardLayout();
				newQuestion();
			}
		});

		mSolutionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSolution();
			}
		});

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			mDirectConversion = savedInstanceState
					.getBoolean(STATE_DIRECT_CONVERSION);
			mNumberToConvert = savedInstanceState
					.getString(STATE_NUMBER_TO_CONVERT);
			mQuestionCounter = savedInstanceState
					.getInt(STATE_QUESTION_COUNTER);
		} else {
			generateRandomQuestion();
		}

		updateUI();
		setKeyboardLayout();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_DIRECT_CONVERSION, mDirectConversion);
		outState.putString(STATE_NUMBER_TO_CONVERT, mNumberToConvert);
		outState.putInt(STATE_QUESTION_COUNTER, mQuestionCounter);
	}

	private void setKeyboardLayout() {
		if (!isResultNumeric()) {
			mAnswerTextView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		} else {
			// setInputType cannot be used because it doesn't allow the sign
			mAnswerTextView.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
	}

	private void setTitle() {
		mTitleTextView.setText(titleString());
	}

	private void generateRandomQuestion() {
		mNumberToConvert = generateRandomNumber();
	}

	protected int numberOfBits() {
		return level().numberOfBits();
	}
	
	private void updateUI() {
		mNumberToConvertTextView.setText(mNumberToConvert);
		setTitle();
	}

	/**
	 * Checks if the answer is correct. If the game mode is selected checks for
	 * the number of questions that already has been asked, in case that number
	 * is equal to the max number allowed calls endGame(). Also it adds a point
	 * for each correct answer in that mode.
	 * 
	 * @param answer
	 *            the user input
	 */
	private void checkSolution(String answer) {
		if (answer.equals("") || !isCorrect(answer)) {
			showAnimationAnswer(false);
		} else {
			// Correct answer
			showAnimationAnswer(true);
			if (mIsPlaying) {
				handleCorrectAnswer();
			}
			newQuestion();
		}
	}

	private void newQuestion() {
		clearAnswer();
		generateRandomQuestion();
		updateUI();
	}

	private void showSolution() {
		mAnswerTextView.setText(obtainSolution());
	}

	/**
	 * Prepares the layout for the training and game mode.
	 * 
	 * @param training
	 *            true if the change is to the training mode
	 */
	private void setTrainingMode(boolean training) {
		if (training) {
			mIsPlaying = false;
			mSolutionButton.setVisibility(View.VISIBLE);
			mChangeDirectionButton.setVisibility(View.VISIBLE);
		} else {
			mIsPlaying = true;
			resetGameState();
			mSolutionButton.setVisibility(View.GONE);
			mChangeDirectionButton.setVisibility(View.GONE);
		}
		clearAnswer();
	}

	/**
	 * Updates the game stats and if all the questions have been asked it calls
	 * the endGame() method.
	 */
	private void handleCorrectAnswer() {
		updateScore(score() + pointsForCorrectAnswer());

		mQuestionCounter++;
		if (mQuestionCounter == GAMEMODE_MAXQUESTIONS) {
			endGame();
		}
	}

	private void resetGameState() {
		mQuestionCounter = 0;
		updateScore(0);
		clearAnswer();
	}

	private void clearAnswer() {
		mAnswerTextView.setText("");
	}

	/**
	 * Starts the game and sets the UI
	 */
	@Override
	protected void startGame() {
		super.startGame();
		setTrainingMode(false);
		newQuestion();
		updateScore(0);
	}

	/**
	 * Called when the user cancels the game
	 */
	@Override
	protected void cancelGame() {
		super.cancelGame();
		setTrainingMode(true);
	}

	/**
	 * Called when the game ends
	 */
	@Override
	protected void endGame() {
		int remainingTime = (int) getRemainingTimeMs() / 1000;
		updateScore(score() + remainingTime);

		super.endGame();

		saveScore();
		setTrainingMode(true);
	}

	@Override
	protected String gameOverMessage() {
		int remainingTime = (int) getRemainingTimeMs() / 1000;
		if (remainingTime > 0) {
			return String.format(
					getString(R.string.gameisoverexp), remainingTime, score());
		} else {
			return String.format(
					getString(R.string.lost_time_over), score());
		}
	}

	/**
	 * Debe retornar la constante usada en FragmentFactory para lanzar el
	 * fragmento, por ejemplo, R.string.logic_gate
	 */
	protected abstract int obtainExerciseId();

	/**
	 * Debe retornar true si el resultado está compuesto sólo de números entre 0
	 * y 9 y el separador de decimales
	 */
	protected abstract boolean isResultNumeric();

	/**
	 * Debe retornar una cadena con el título del ejercicio actual
	 */
	protected abstract String titleString();

	/**
	 * Generar un número aleatorio teniendo en cuenta la dirección actual del
	 * ejercicio. Debe estar formateado como una cadena para poder imprimirlo
	 */
	protected abstract String generateRandomNumber();

	/**
	 * Debe retornar una cadena con la solución
	 */
	protected abstract String obtainSolution();

	/**
	 * Debe retornar true si la solución es correcta
	 */
	protected abstract boolean isCorrect(String answer);

	/**
	 * Must return the number of points awarded when the answer is right
	 */
	protected abstract int pointsForCorrectAnswer();

}
