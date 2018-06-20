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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
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
	private static final String STATE_DIRECT_CONVERSION = "mDirectConversion";
	private static final String STATE_NUMBER_TO_CONVERT = "mNumberToConvertString";

	private EditText mAnswerEditText;
	private Button mCheckButton;
	private Button mChangeDirectionButton;
	private Button mSolutionButton;
	private TextView mNumberToConvertTextView;
	private TextView mTitleTextView;
	protected String mNumberToConvertString;

	protected Random mRandomGenerator;
	protected boolean mDirectConversion = true;

	private InputFilter mNumberFilter = new InputFilter() {
		
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				if (!Character.isDigit(source.charAt(i))
						&& source.charAt(i) != '-') {
					return "";
				}
			}

			return null;
		}
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_numerical_exercise,
				container, false);

		mAnswerEditText = rootView.findViewById(R.id.text_view_answer);
		mChangeDirectionButton = rootView.findViewById(R.id.change);
		mSolutionButton = rootView.findViewById(R.id.seesolution);
		mCheckButton = rootView.findViewById(R.id.checkbutton);
		mNumberToConvertTextView = rootView.findViewById(R.id.numbertoconvert);
		mTitleTextView = rootView.findViewById(R.id.exercisetitle);
		mRandomGenerator = new Random();

		mAnswerEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					checkSolution(mAnswerEditText.getEditableText().toString()
							.trim().toLowerCase(Locale.US));
				}
				return false;
			}
		});

		mCheckButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkSolution(mAnswerEditText.getEditableText().toString()
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
			mNumberToConvertString = savedInstanceState
					.getString(STATE_NUMBER_TO_CONVERT);
			setTrainingMode(!mIsPlaying);
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
		outState.putString(STATE_NUMBER_TO_CONVERT, mNumberToConvertString);
	}

	private void setKeyboardLayout() {
		if (!isResultNumeric()) {
			mAnswerEditText.setFilters((new InputFilter[] {}));
			mAnswerEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		} else {
			// setInputType cannot be used because it doesn't allow the sign
			mAnswerEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
			mAnswerEditText.setFilters(new InputFilter[]{mNumberFilter});
		}
	}

	private void setTitle() {
		mTitleTextView.setText(titleString());
	}

	private void generateRandomQuestion() {
		mNumberToConvertString = generateRandomNumber();
	}

	protected int numberOfBits() {
		return level().numberOfBits();
	}
	
	private void updateUI() {
		mNumberToConvertTextView.setText(mNumberToConvertString);
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
			computeIncorrectQuestion();
		} else {
			// Correct answer
			showAnimationAnswer(true);
			if (mIsPlaying) {
				computeCorrectQuestion();
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
		mAnswerEditText.setText(obtainSolution());
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

	private void resetGameState() {
		clearAnswer();
	}

	private void clearAnswer() {
		mAnswerEditText.setText("");
	}

	/**
	 * Starts the game and sets the UI
	 */
	@Override
	protected void startGame() {
		super.startGame();
		setTrainingMode(false);
		newQuestion();
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
		setTrainingMode(true);
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

}
