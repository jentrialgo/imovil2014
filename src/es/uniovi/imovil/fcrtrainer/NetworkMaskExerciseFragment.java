package es.uniovi.imovil.fcrtrainer;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class NetworkMaskExerciseFragment extends BaseExerciseFragment implements
		OnClickListener {
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 5 * 1000 * 60;

	private TextView mQuestionTitle;
	private EditText mSolution;
	private Button mButtonR;
	private Button mButtonC;
	private String[] mQuestions;
	private String[] mAnswers;
	private ImageView mImage;

	private int mCurrentQuestion;
	private Random mRandom;
	private Handler mHandler;
	private boolean mGameMode = false;
	private int mPoints;
	private int mCurrentQuestionCounter = 1;
	private boolean mWon = false;
	private View mRootView;

	public static NetworkMaskExerciseFragment newInstance() {
		NetworkMaskExerciseFragment fragment = new NetworkMaskExerciseFragment();
		return fragment;
	}

	public NetworkMaskExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_networkmask, container,
				false);
		mRandom = new Random();
		mCurrentQuestion = mRandom.nextInt(MAX_QUESTIONS);
		mQuestions = new String[MAX_QUESTIONS];
		mAnswers = new String[MAX_QUESTIONS];
		mHandler = new Handler();
		mImage = (ImageView) mRootView.findViewById(R.id.resultimage);
		mQuestionTitle = (TextView) mRootView.findViewById(R.id.TextEnun2);
		mSolution = (EditText) mRootView.findViewById(R.id.respuesta);
		mButtonR = (Button) mRootView.findViewById(R.id.button1);
		mButtonC = (Button) mRootView.findViewById(R.id.button2);
		mQuestions = getResources().getStringArray(R.array.preguntas);

		mAnswers = getResources().getStringArray(R.array.respuestas);

		mQuestionTitle.setText(mQuestions[mCurrentQuestion]);
		mButtonR.setOnClickListener(this);
		mButtonC.setOnClickListener(this);

		return mRootView;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			if (mSolution.getText().toString()
					.equals(mAnswers[mCurrentQuestion])) {
				// Toast.makeText(getActivity(), "correcto", 2000).show();
				// imagen.setImageResource(R.drawable.correct);
				// imagen.setVisibility(View.VISIBLE);
				if (mGameMode) {
					gameModeControl();
				}
				showAnimationAnswer(true);
				mHandler.postDelayed(new Runnable() {
					public void run() {
						mImage.setImageResource(0);
						mCurrentQuestion = mRandom.nextInt(5);
						mQuestionTitle.setText(mQuestions[mCurrentQuestion]);
						mSolution.setText("");
					}
				}, 1500);
			} else {
				showAnimationAnswer(false);
				mHandler.postDelayed(new Runnable() {
					public void run() {
						mImage.setImageResource(0);
						mCurrentQuestion = mRandom.nextInt(5);
						mQuestionTitle.setText(mQuestions[mCurrentQuestion]);
						mSolution.setText("");
					}
				}, 1500);

			}

		}

		if (v.getId() == R.id.button2) {
			mSolution.setText(mAnswers[mCurrentQuestion]);

			mHandler.postDelayed(new Runnable() {
				public void run() {

					mCurrentQuestion = mRandom.nextInt(5);
					mQuestionTitle.setText(mQuestions[mCurrentQuestion]);
					mSolution.setText("");
				}
			}, 1500);

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

	@Override
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);

		// set starting points of textview
		updatePointsTextView(0);

		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		mGameMode = true;

		Button solution = (Button) mRootView.findViewById(R.id.button2);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);
	}

	private void updateToTrainMode() {
		mGameMode = false;

		Button solution = (Button) mRootView.findViewById(R.id.button2);
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
					this.mPoints, R.string.binary, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
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
