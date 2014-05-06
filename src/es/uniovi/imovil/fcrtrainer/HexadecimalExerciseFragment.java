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

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.app.AlertDialog;
import android.os.Bundle;
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

public class HexadecimalExerciseFragment extends BaseExerciseFragment {
	private EditText etAnswer;
	private Button bCheck;
	private Button bChange;
	private Button bSolution;
	private TextView tvNumberToConvert;
	private TextView tvTitle;
	private TextView tvPoints;
	private int numberToConvert;
	private boolean tohex = true;
	private boolean game = false;
	private int pointsCounter = 0;
	private static final int MAX_NUMBER_TO_CONVERT = 512;
	private static final int GAMEMODE_MAXQUESTIONS = 5;
	private Random randomGenerator;

	public static HexadecimalExerciseFragment newInstance() {

		HexadecimalExerciseFragment fragment = new HexadecimalExerciseFragment();
		return fragment;
	}

	public HexadecimalExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_hexadecimal, container,
				false);

		etAnswer = (EditText) rootView.findViewById(R.id.answer);
		bChange = (Button) rootView.findViewById(R.id.change);
		bSolution = (Button) rootView.findViewById(R.id.seesolution);
		bCheck = (Button) rootView.findViewById(R.id.checkbutton);
		tvNumberToConvert = (TextView) rootView
				.findViewById(R.id.numbertoconvert);
		tvTitle = (TextView) rootView.findViewById(R.id.exercisetitle);
		tvPoints = (TextView) rootView.findViewById(R.id.tvpoints);
		randomGenerator = new Random();

		etAnswer.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					if (tohex)
						isCorrect(etAnswer.getEditableText().toString().trim()
								.toLowerCase(Locale.US));
					else
						isCorrect(etAnswer.getEditableText().toString().trim());
				}
				return false;
			}
		});

		bCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tohex)
					isCorrect(etAnswer.getEditableText().toString().trim()
							.toLowerCase(Locale.US));
				else
					isCorrect(etAnswer.getEditableText().toString().trim());
			}
		});

		bChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tohex ^= true;
				if (tohex) {
					setKeyboardLayout();
					setTitle();
				} else {
					setKeyboardLayout();
					setTitle();
				}
				generateRandomNumber();
			}
		});

		bSolution.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSolution();
			}
		});

		if (savedInstanceState != null) {
			tohex = savedInstanceState.getBoolean("tohex");
			numberToConvert = savedInstanceState.getInt("numbertoconvert");
			updateUI();
			setKeyboardLayout();
			setTitle();
		} else
			generateRandomNumber();

		return rootView;
	}
	
	public void setKeyboardLayout(){
		if(tohex) etAnswer.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		else etAnswer.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
	}
	
	public void setTitle(){
		if(tohex) tvTitle.setText(getResources().getString(R.string.convert_to_hex));
		else tvTitle.setText(getResources().getString(R.string.convert_to_bin));
	}

	public void generateRandomNumber() {
		numberToConvert = randomGenerator.nextInt(MAX_NUMBER_TO_CONVERT);
		updateUI();
	}

	public void updateUI() {
		if (tohex)
			tvNumberToConvert.setText(Integer.toBinaryString(numberToConvert));
		else
			tvNumberToConvert.setText(Integer.toHexString(numberToConvert)
					.toUpperCase(Locale.US));
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
	public void isCorrect(String answer) {

		etAnswer.setText("");

		if (tohex) {
			if (answer.equals(Integer.toHexString(numberToConvert))) {
				showAnimationAnswer(true);
				if (game)
					updateGameState();
				generateRandomNumber();
			} else
				showAnimationAnswer(false);
		} else {
			if (answer.equals(Integer.toBinaryString(numberToConvert))) {
				showAnimationAnswer(true);
				if (game)
					updateGameState();
				generateRandomNumber();
			} else
				showAnimationAnswer(false);
		}
	}

	public void showSolution() {
		if (tohex)
			etAnswer.setText(Integer.toHexString(numberToConvert));
		else
			etAnswer.setText(Integer.toBinaryString(numberToConvert));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("tohex", tohex);
		outState.putInt("numbertoconvert", numberToConvert);
	}

	/**
	 * Prepares the layout for the training and game mode.
	 * 
	 * @param training
	 *            true if the change is to the training mode
	 */
	public void setTrainingMode(boolean training) {
		if (training) {
			game = false;
			bSolution.setVisibility(View.VISIBLE);
			tvPoints.setVisibility(View.GONE);
		} else {
			game = true;
			resetGameState();
			bSolution.setVisibility(View.GONE);
			tvPoints.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Updates the game stats and if all the questions have been asked it calls
	 * the endGame() method.
	 */
	public void updateGameState() {
		pointsCounter++;
		updatePoints(pointsCounter);
		if (pointsCounter == GAMEMODE_MAXQUESTIONS)
			endGame();
	}

	public void resetGameState() {
		pointsCounter = 0;
		tvPoints.setVisibility(View.GONE);
	}

	/**
	 * Updates the points in the UI
	 * @param points
	 */
	public void updatePoints(int points) {
		tvPoints.setText(getString(R.string.points) + String.valueOf(points));
	}

	/**
	 * Starts the game and sets the UI
	 */
	@Override
	void startGame() {
		super.startGame();
		showGameAnimation();
		setTrainingMode(false);
		updatePoints(pointsCounter);
	}

	/**
	 * Called when the user cancels the game
	 */
	@Override
	void cancelGame() {
		super.cancelGame();
		setTrainingMode(true);
	}

	/**
	 * Called when the game ends
	 */
	@Override
	void endGame() {
		super.endGame();

		int remainingTime = (int) getRemainingTimeMs() / 1000;
		pointsCounter = pointsCounter + remainingTime;

		showEndGameDialog(remainingTime);

		saveScore(pointsCounter);
		setTrainingMode(true);
	}

	/**
	 * Shows a dialog with the game stats when the game is over
	 * 
	 * @param remainingTime
	 */
	public void showEndGameDialog(int remainingTime) {
		AlertDialog.Builder abuilder = new AlertDialog.Builder(getActivity());
		abuilder.setTitle(getString(R.string.game_over));

		if (remainingTime > 0)
			abuilder.setMessage(String.format(
					getString(R.string.gameisoverexp), remainingTime,
					pointsCounter));
		else
			abuilder.setMessage(String.format(
					getString(R.string.lost_time_over), pointsCounter));

		abuilder.create().show();
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
					points, R.string.hexadecimal, new Date(), user);
		} catch (JSONException e) {
			Log.v(getClass().getSimpleName(), "Error when saving score");
		}
	}

}