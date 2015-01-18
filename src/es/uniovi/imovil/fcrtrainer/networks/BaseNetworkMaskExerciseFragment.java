package es.uniovi.imovil.fcrtrainer.networks;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
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

public abstract class BaseNetworkMaskExerciseFragment
		extends BaseExerciseFragment implements OnClickListener {

	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 1 * 1000 * 60;

	protected Random mRandom = new Random();
	
	protected int mMask;

	protected boolean mWon = false;
	protected boolean mGameMode = false;
	protected int mCurrentQuestionCounter = 1;
	protected int mPoints;

	protected View mRootView;
	protected TextView mExerciseTitle;
	protected TextView mQuestion;
	protected EditText mAnswer;
	protected Button mCheckAnswer;
	protected Button mShowSolution;

	protected int generateRandomMask() {
		Level level = PreferenceUtils.getLevel(getActivity());
	
		int maxOffset = 8;
		switch (level) {
		case BEGINNER:
			maxOffset = 4;
			break;
		case INTERMEDIATE:
			maxOffset = 16;
			break;
		case PROFICIENCY:
			maxOffset = 26;
			break;
		}
		
		// Add 1 because 0 is not a valid mask
		int offset = mRandom.nextInt(maxOffset) + 1;
	
		return 0xffffffff << offset;
	}

	protected String intToIpString(int ipAddress) {
		int[] bytes = new int[] {
				(ipAddress >> 24 & 0xff),
				(ipAddress >> 16 & 0xff),
				(ipAddress >> 8 & 0xff),
				(ipAddress &0xff)
		};
		return Integer.toString(bytes[0]) 
				+ "." + Integer.toString(bytes[1])
				+ "." + Integer.toString(bytes[2])
				+ "." + Integer.toString(bytes[3]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_network_exercise,
				container, false);
		
		mExerciseTitle = (TextView) mRootView.findViewById(
				R.id.text_view_exercise_title);
		mQuestion = (TextView) mRootView.findViewById(R.id.text_view_question);
		mAnswer = (EditText) mRootView.findViewById(R.id.text_view_answer);

		mExerciseTitle.setText(titleString());
		
		((Button) mRootView.findViewById(R.id.button_check_answer))
			.setOnClickListener(this);
		((Button) mRootView.findViewById(R.id.button_show_solution))
			.setOnClickListener(this);
		
		newQuestion();

		return mRootView;
	}

	public void startGame() {
		setGameDuration(GAME_DURATION_MS);
	
		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		mGameMode = true;
	
		newQuestion();
	
		Button solution = (Button) mRootView.findViewById(
				R.id.button_show_solution);
		solution.setVisibility(View.GONE);
	}

	private void updateToTrainMode() {
		mGameMode = false;
	
		Button solution = (Button) mRootView.findViewById(
				R.id.button_show_solution);
		solution.setVisibility(View.VISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	protected void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;
		// every remaining second gives one extra point.
		mPoints = (int) (mPoints + remainingTimeInSeconds);
	
		if (this.mWon) {
			savePoints();
		}
	
		dialogGameOver();
		super.endGame();
		updateToTrainMode();
		reset();
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPoints, exerciseId(), new Date(), username,
					level());
	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void gameModeControl() {
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
		mPoints = mPoints + val;
		updateScore(mPoints);
	}

	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);
	
		if (this.mWon) {
			message = getResources().getString(R.string.won) + " "
					+ getResources().getString(R.string.points_final) + " "
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

	private void reset() {
		mPoints = 0;
		mCurrentQuestionCounter = 0;
		mWon = false;
	
		updateScore(mPoints);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_check_answer:
			checkAnswer();
			break;
		case R.id.button_show_solution:
			showSolution();
			break;
		}
	}

	/**
	 * Debe retornar una cadena con el título del ejercicio actual
	 */
	protected abstract String titleString();

	/**
	 * Generates a new question and updates the user interface
	 */
	protected abstract void newQuestion();

	/**
	 * Listener for the check answer button
	 */
	protected abstract void checkAnswer();

	/**
	 * Listener for the show solution button
	 */
	protected abstract void showSolution();

	/**
	 * @return The id of the exercise for saving the highscore
	 */
	protected abstract int exerciseId();

}
