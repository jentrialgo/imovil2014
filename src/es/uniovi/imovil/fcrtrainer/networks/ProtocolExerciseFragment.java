/*


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

package es.uniovi.imovil.fcrtrainer.networks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

// TODO: Necesita que se traduzcan las preguntas de la base de datos a ingl�s
public class ProtocolExerciseFragment extends BaseExerciseFragment {

	public static final int NUMBER_OF_ANSWERS = 4;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int PENALIZATION_PER_FAIL = 3;
	private static final int MAX_QUESTIONS = 5;
	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final int DB_VERSION = 2;
	private static final long GAME_DURATION_MS = 60 * 1000; // 1 min

	private ArrayList<ProtocolTest> mTestList = null;
	private View mRootView;
	private View mCardView;
	private RadioButton[] mRadioButtonAnswers;
	private Button mShowSolutionButton;
	private Button mCheckSolutionButton;
	private TextView mQuestion;
	private ProtocolTest mTest;
	private RadioGroup mRadioGroup;
	private int mCurrentQuestionCounter = 1;
	private int mPartialPoints;
	private int mTotalFails = 0;

	public static ProtocolExerciseFragment newInstance() {
		ProtocolExerciseFragment fragment = new ProtocolExerciseFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_protocol, container,
				false);
		ProtocolDataBaseHelper db = new ProtocolDataBaseHelper(
				this.getActivity(), DB_NAME, null, DB_VERSION);
		mQuestion = (TextView) mRootView.findViewById(R.id.exerciseQuestion);
		mCardView = (RelativeLayout) mRootView.findViewById(R.id.card);
		mRadioGroup = new RadioGroup(getActivity());
		addAnswerRadioButtons();
		mShowSolutionButton = (Button) mRootView.findViewById(R.id.seesolution);
		mCheckSolutionButton = (Button) mRootView
				.findViewById(R.id.checkbutton);

		// Manejador del evento de mostrar solución (solo en modo
		// entrenamiento).
		mShowSolutionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
					if ((mRadioButtonAnswers[i].getText().equals(
							mTest.getResponse()))) {
						mRadioButtonAnswers[i].setChecked(true);
						return;
					}
				}
			}
		});
		// Manejador del botón de comprobación de respuesta.
		mCheckSolutionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				boolean checkedButtonFound = false;
				int index = 0;
				while ((index < NUMBER_OF_ANSWERS) && (!checkedButtonFound)) {
					if (mRadioButtonAnswers[index].isChecked()) {
						checkIfButtonClickedIsCorrectAnswer(index);
						checkedButtonFound = true;
					}
					index++;
				}
				if (!checkedButtonFound) {
					showAnimationAnswer(false);
				}
			}
		});
		try {
			db.createDataBase();
			db.openDataBase();
		} catch (IOException e) {
			// TODO: extraer cadenas a recursos
			Toast.makeText(this.getActivity(),
					"Error al abrir la base de datos", Toast.LENGTH_LONG)
					.show();
		}

		mTestList = db.loadData();
		
		if (savedInstanceState != null) {
			newQuestion();
		}

		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			return;
		}

		if (mIsPlaying) {
			mShowSolutionButton.setVisibility(View.GONE);
		}
		
		// TODO: get mTestList
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// TODO: save mTestList
	}

	private void addAnswerRadioButtons() {
		mRadioButtonAnswers = new RadioButton[NUMBER_OF_ANSWERS];
		for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
			addAnswerRadioButton(i, mRadioGroup); // Añadir respueta al array.
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_LEFT);
		TextView question = (TextView) mRootView
				.findViewById(R.id.exerciseQuestion); // Obtener pregunta.
		params.addRule(RelativeLayout.BELOW, question.getId()); // Debajo de la
																// pregunta.
		((RelativeLayout) mCardView).addView(mRadioGroup, params);
	}

	private void addAnswerRadioButton(int index, RadioGroup rg) {
		mRadioButtonAnswers[index] = new RadioButton(this.getActivity());
		rg.addView(mRadioButtonAnswers[index]);
	}

	private void checkIfButtonClickedIsCorrectAnswer(int index) {
		boolean correct = false;
		if (mRadioButtonAnswers[index].getText().equals(mTest.getResponse())) {
			correct = true;
		} else {
			correct = false;
		}
		super.showAnimationAnswer(correct);

		if (correct) {
			if (mIsPlaying) {
				gameModeControl();
			}
			newQuestion();
		} else if (mTotalFails < 3) {
			mTotalFails++;
			mPartialPoints = mPartialPoints - PENALIZATION_PER_FAIL;
		} else {
			mPartialPoints = 0;
			gameModeControl();
			newQuestion();
		}
	}

	private void newQuestion() {
		mRadioGroup.clearCheck();
		mPartialPoints = POINTS_FOR_QUESTION; // Puntos parciales (al fallar se
												// resta 2 puntos).
		mTest = mTestList.get((int) (Math.random() * (14 - 0)) + 0);

		// Mostrar pregunta y opciones.
		mQuestion.setText(mTest.getQuestion());
		for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
			mRadioButtonAnswers[i].setText(mTest.getOption(i));
		}
	}

	@Override
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);

		super.startGame();
		updateToGameMode();
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	private void updateToGameMode() {
		newQuestion();

		Button solution = (Button) mRootView.findViewById(R.id.seesolution);
		solution.setVisibility(View.GONE);
	}

	private void updateToTrainMode() {
		mShowSolutionButton.setVisibility(View.VISIBLE);
	}

	protected void gameModeControl() {
		updateScore(score() + POINTS_FOR_QUESTION);
	
		if (mCurrentQuestionCounter >= MAX_QUESTIONS
				|| getRemainingTimeMs() <= 0) {
			endGame();
		}
		mCurrentQuestionCounter++;
		mTotalFails = 0;
	}

	@Override
	protected void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;
		updateScore(score() + remainingTimeInSeconds);

		savePoints();

		super.endGame();

		updateToTrainMode();

		reset();
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					score(), R.string.protocol, new Date(), username,
					level());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		updateScore(0);
		mCurrentQuestionCounter = 0;
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

}
