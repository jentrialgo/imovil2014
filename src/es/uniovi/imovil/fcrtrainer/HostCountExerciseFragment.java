package es.uniovi.imovil.fcrtrainer;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HostCountExerciseFragment extends BaseExerciseFragment {

	private static final int RANDOM_NUMBER_LIMIT = 30;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 10 * 1000 * 60; // 10min

	private boolean mWon = false;
	private boolean mGameMode = false;
	private int mPoints;
	private int mCurrentQuestionCounter = 1;

	private View mRootView;
	private Button mBtnCheck;
	private Button mBtnSolution;
	private String[] mHostCountQuestions;
	private String[] mHostCountAnswers;
	private TextView mQuestion;
	EditText mAnswer;
	int mRandomNumberQuestion;

	// Constructor
	public HostCountExerciseFragment() {
	}

	public static HostCountExerciseFragment newInstance() {
		HostCountExerciseFragment fragment = new HostCountExerciseFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_host_count, container,
				false);

		mBtnCheck = (Button) mRootView.findViewById(R.id.btnCheckAnswer);
		mBtnSolution = (Button) mRootView.findViewById(R.id.btnSolution);
		mQuestion = (TextView) mRootView.findViewById(R.id.question);
		mAnswer = (EditText) mRootView.findViewById(R.id.answer);
		mHostCountQuestions = getResources().getStringArray(
				R.array.host_count_questions);
		mHostCountAnswers = getResources().getStringArray(
				R.array.host_count_answers);
		mRandomNumberQuestion = generateRandomNumber();
		// Carga una de las preguntas del array
		newQuestion();

		mBtnCheck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer((mAnswer.getText().toString()));
			}
		});

		mBtnSolution.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showSolution(mRandomNumberQuestion);
			}
		});

		return mRootView;
	}

	public void newQuestion() {
		mQuestion.setText(mHostCountQuestions[mRandomNumberQuestion]);
	}

	private int generateRandomNumber() {
		Random rn = new Random();

		// Funcion nextInt devuelve un numero aleatorio entre [0, limite)
		int x = rn.nextInt(RANDOM_NUMBER_LIMIT);
		return x;

	}

	public void checkAnswer(String a) {
		// Si la respuesta es correcta, genera otra nueva pregunta y borra
		// respuesta
		if ((a.toString().equals(mHostCountAnswers[mRandomNumberQuestion]
				.toString()))) {
			showAnimationAnswer(true);
			mRandomNumberQuestion = generateRandomNumber();
			mQuestion.setText(mHostCountQuestions[mRandomNumberQuestion]);
			if (this.mGameMode) {
				gameModeControl();
			}
			mAnswer.setText("");

		} else
			showAnimationAnswer(false);

	}

	// Método para mostrar la solución
	public void showSolution(int numberOfQuestion) {
		mAnswer.setText(mHostCountAnswers[numberOfQuestion]);
	}

	public void startGame() {
		setGameDuration(GAME_DURATION_MS);

		// set starting points of textview
		updatePointsTextView(0);

		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		mGameMode = true;

		newQuestion();

		Button solution = (Button) mRootView.findViewById(R.id.btnSolution);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		mGameMode = false;

		Button solution = (Button) mRootView.findViewById(R.id.btnSolution);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.INVISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;
		// every remaining second gives one extra point.
		this.mPoints = (int) (this.mPoints + remainingTimeInSeconds);

		if (this.mWon)
			savePoints();

		dialogGameOver();
		super.endGame();
		updateToTrainMode();
		reset();
	}

	private void reset() {
		this.mPoints = 0;
		this.mCurrentQuestionCounter = 0;
		this.mWon = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPoints, R.string.host_count, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void gameModeControl() {
		increasePoints(POINTS_FOR_QUESTION);

		if (mCurrentQuestionCounter >= MAX_QUESTIONS) {
			// won
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
	}

	private void increasePoints(int val) {
		this.mPoints = this.mPoints + val;
		updatePointsTextView(this.mPoints);
	}

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.points);
		tvPoints.setText(getResources().getString(R.string.points) + " "
				+ String.valueOf(p));
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (this.mWon) {
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

}
