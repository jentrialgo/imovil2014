package es.uniovi.imovil.fcrtrainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class ProtocolExerciseFragment extends BaseExerciseFragment {

	static public int NUMBER_OF_ANSWERS = 4;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int REST_FOR_FAIL = 3;
	private static final int MAX_QUESTIONS = 5;
	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final int DB_VERSION = 2;
	private static final long GAME_DURATION_MS = 3 * 1000; // 2 minutos de
															// juego.

	private ArrayList<ProtocolTest> mTestList = null;
	private View mRootView;
	private View mCardView;
	private RadioButton[] mRadioButtonAnswers;
	private Button mSeeSolutionButton;
	private Button mCheckSolutionButton;
	private TextView mQuestion;
	private ProtocolTest mTest;
	private RadioGroup mRadioGroup;
	private int mCurrentQuestionCounter = 1;
	private boolean mWon = false;
	private int mPoints;
	private int mPartialPoints;
	private int mTotalFails = 0;
	private boolean mGameMode = false;
	private boolean mFlag;

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
		mSeeSolutionButton = (Button) mRootView.findViewById(R.id.seesolution);
		mCheckSolutionButton = (Button) mRootView
				.findViewById(R.id.checkbutton);

		// Manejador del evento de mostrar solución (solo en modo
		// entrenamiento).
		mSeeSolutionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mFlag = false;
				int i = 0;
				while (i < NUMBER_OF_ANSWERS) {
					if ((mRadioButtonAnswers[i].getText().equals(mTest
							.getResponse())) && (!mFlag)) {
						mRadioButtonAnswers[i].setChecked(true);
						mFlag = true;
					}
					i++;
				}

			}
		});
		// Manejador del botón de comprobación de respuesta.
		mCheckSolutionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mFlag = false;
				int index = 0;
				while ((index < NUMBER_OF_ANSWERS) && (!mFlag)) {
					if (mRadioButtonAnswers[index].isChecked()) {
						checkIfButtonClickedIsCorrectAnswer(index);
						mFlag = true;
					}
					index++;
				}
				if (!mGameMode)
					newQuestion();
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
		newQuestion();
		return mRootView;
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
		boolean response = false;
		if (mRadioButtonAnswers[index].getText().equals(mTest.getResponse())) {
			response = true;
		} else {
			response = false;
		}
		super.showAnimationAnswer(response);
		// only create new Question if user has right input
		if (this.mGameMode) {
			if (response) {
				gameModeControl();
				newQuestion();
			} else if (mTotalFails < 3) {
				mTotalFails++;
				mPartialPoints = mPartialPoints - REST_FOR_FAIL;
			} else {
				mPartialPoints = 0;
				gameModeControl();
				newQuestion();
			}
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

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.points);
		tvPoints.setText(getResources().getString(R.string.points) + " "
				+ String.valueOf(p));
	}

	@Override
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);

		// set starting points of textview
		updatePointsTextView(0);

		super.startGame();
		updateToGameMode();
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	private void updateToGameMode() {
		mGameMode = true;

		newQuestion();

		Button solution = (Button) mRootView.findViewById(R.id.seesolution);
		solution.setVisibility(View.GONE);
		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		mGameMode = false;

		Button solution = (Button) mRootView.findViewById(R.id.seesolution);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.GONE);
	}

	private void gameModeControl() {
		increasePoints(mPartialPoints);

		if (mCurrentQuestionCounter >= MAX_QUESTIONS) {
			this.mWon = true;
			this.endGame();
		}

		if (mCurrentQuestionCounter < MAX_QUESTIONS
				&& getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			this.mWon = false;
			this.endGame();
		}
		mCurrentQuestionCounter++;
		mTotalFails = 0;
	}

	@Override
	void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;
		this.mPoints = (int) (this.mPoints + remainingTimeInSeconds);

		if (this.mWon)
			savePoints();

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();

	}

	private void increasePoints(int val) {
		mPoints = mPoints + val;
		updatePointsTextView(mPoints);
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (mWon) {
			message = getResources().getString(R.string.won) + " "
					+ getResources().getString(R.string.points) + " "
					+ this.mPoints;
		}

		Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.game_over));
		alert.setMessage(message);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// nothing to do...
			}
		});
		alert.show();

	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPoints, R.string.protocol, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		mPoints = 0;
		mCurrentQuestionCounter = 0;
		mWon = false;
		updatePointsTextView(0);
	}

}
