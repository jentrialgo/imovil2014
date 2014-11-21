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
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.Level;
import es.uniovi.imovil.fcrtrainer.PreferenceUtils;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class BaseNumericalExerciseFragment extends
		BaseExerciseFragment {
	private static final int GAMEMODE_MAXQUESTIONS = 5;

	private static final String STATE_DIRECT_CONVERSION = "mDirectConversion";
	private static final String STATE_NUMBER_TO_CONVERT = "mNumberToConvert";

	private EditText mAnswerTextView;
	private Button mCheckButton;
	private Button mChangeDirectionButton;
	private Button mSolutionButton;
	private TextView mNumberToConvertTextView;
	private TextView mTitleTextView;
	private TextView mPointsTextView;

	protected String mNumberToConvert;
	private int mQuestionCounter = 0;
	private int mPoints = 0;

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
		mPointsTextView = (TextView) rootView.findViewById(R.id.tvpoints);
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
		} else {
			generateRandomQuestion();
		}

		updateUI();
		setKeyboardLayout();
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
		Level level = PreferenceUtils.getLevel(getActivity());
		return level.numberOfBits();
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
		if (isCorrect(answer)) {
			showAnimationAnswer(true);
			if (mIsPlaying) {
				handleCorrectAnswer();
			}
			newQuestion();
		} else
			showAnimationAnswer(false);
	}

	private void newQuestion() {
		clearAnswer();
		generateRandomQuestion();
		updateUI();
	}

	private void showSolution() {
		mAnswerTextView.setText(obtainSolution());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_DIRECT_CONVERSION, mDirectConversion);
		outState.putString(STATE_NUMBER_TO_CONVERT, mNumberToConvert);
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
			mPointsTextView.setVisibility(View.GONE);
		} else {
			mIsPlaying = true;
			resetGameState();
			mSolutionButton.setVisibility(View.GONE);
			mPointsTextView.setVisibility(View.VISIBLE);
		}
		clearAnswer();
	}

	/**
	 * Updates the game stats and if all the questions have been asked it calls
	 * the endGame() method.
	 */
	private void handleCorrectAnswer() {
		mPoints += pointsForCorrectAnswer();
		updatePoints(mPoints);

		mQuestionCounter++;
		if (mQuestionCounter == GAMEMODE_MAXQUESTIONS) {
			endGame();
		}
	}

	private void resetGameState() {
		mQuestionCounter = 0;
		mPoints = 0;
		mPointsTextView.setVisibility(View.GONE);
		clearAnswer();
	}

	private void clearAnswer() {
		mAnswerTextView.setText("");
	}

	/**
	 * Updates the points in the UI
	 * 
	 * @param points
	 */
	private void updatePoints(int points) {
		mPointsTextView.setText(getString(R.string.points)
				+ String.valueOf(points));
	}

	/**
	 * Starts the game and sets the UI
	 */
	@Override
	protected void startGame() {
		super.startGame();
		setTrainingMode(false);
		newQuestion();
		updatePoints(mPoints);
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
		super.endGame();

		int remainingTime = (int) getRemainingTimeMs() / 1000;
		mPoints = mPoints + remainingTime;

		showEndGameDialog(remainingTime);

		saveScore(mPoints);
		setTrainingMode(true);
	}

	/**
	 * Shows a dialog with the game stats when the game is over
	 * 
	 * @param remainingTime
	 */
	private void showEndGameDialog(int remainingTime) {
		AlertDialog.Builder abuilder = new AlertDialog.Builder(getActivity());
		abuilder.setTitle(getString(R.string.game_over));

		if (remainingTime > 0)
			abuilder.setMessage(String.format(
					getString(R.string.gameisoverexp), remainingTime, mPoints));
		else
			abuilder.setMessage(String.format(
					getString(R.string.lost_time_over), mPoints));

		abuilder.create().show();
	}

	/**
	 * Saves the score using the Highscore Manager.
	 * 
	 * @param points
	 */
	private void saveScore(int points) {
		String user = getString(R.string.default_user_name);

		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					points, obtainExerciseId(), new Date(), user);
		} catch (JSONException e) {
			Log.v(getClass().getSimpleName(), "Error when saving score");
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
