package es.uniovi.imovil.fcrtrainer.networks;

import java.util.Random;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

public abstract class BaseNetworkMaskExerciseFragment
		extends BaseExerciseFragment implements OnClickListener {
	private static final String STATE_MASK = "mMask";

	protected Random mRandom = new Random();
	
	protected int mMask;

	protected TextView mExerciseTitle;
	protected TextView mQuestion;
	protected EditText mAnswer;
	protected Button mButtonShowSolution;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_network_exercise,
				container, false);
		
		mExerciseTitle = (TextView) rootView.findViewById(
				R.id.text_view_exercise_title);
		mQuestion = (TextView) rootView.findViewById(R.id.text_view_question);
		mAnswer = (EditText) rootView.findViewById(R.id.text_view_answer);

		mExerciseTitle.setText(titleString());
		
		((Button) rootView.findViewById(R.id.button_check_answer))
			.setOnClickListener(this);
		mButtonShowSolution = ((Button) rootView.findViewById(
				R.id.button_show_solution));
		mButtonShowSolution.setOnClickListener(this);
		
		if (savedInstanceState == null) {
			newQuestion();
		}

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			return;
		}

		mMask = savedInstanceState.getInt(STATE_MASK, 0);
		if (mIsPlaying) {
			mButtonShowSolution.setVisibility(View.GONE);
		}
		printQuestion();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_MASK, mMask);
	}

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

	protected static String intToIpString(int ipAddress) {
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

	public void startGame() {
		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		newQuestion();
		mButtonShowSolution.setVisibility(View.GONE);
	}

	private void updateToTrainMode() {
		mButtonShowSolution.setVisibility(View.VISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	protected void endGame() {
		super.endGame();
		updateToTrainMode();
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

	/***
	 * Print the question in the screen
	 */
	protected abstract void printQuestion();

}
