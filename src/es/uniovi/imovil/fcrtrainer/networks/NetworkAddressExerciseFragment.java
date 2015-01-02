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

package es.uniovi.imovil.fcrtrainer.networks;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkAddressExerciseFragment extends BaseNetworkMaskExerciseFragment
		implements View.OnClickListener {

	private static final int POINTS_FOR_QUESTION = 10; // 10 puntos por pregunta
	private static final int MAX_QUESTIONS = 5; // El juego consta de 5
												// preguntas
	private static final long GAME_DURATION_MS = 2 * 1000 * 60; // 1 minuto de
																// juego
	private boolean mGameMode = false;

	private boolean mWon = false;

	private int mPoints;
	private int currentQuestionCounter = 1;

	View mRootView;
	int mIp;
	TextView mTextViewIp;
	TextView mTextViewMask;
	EditText mSolutionEditText;
	ImageView mImageviewsolution;
	Button mButtonanswer;
	Button mButtonSolution;
	Handler mHandler;

	public static NetworkAddressExerciseFragment newInstance() {
		NetworkAddressExerciseFragment fragment = new NetworkAddressExerciseFragment();
		return fragment;
	}

	public NetworkAddressExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_network_address,
				container, false);

		mTextViewIp = (TextView) mRootView.findViewById(R.id.tv_ip);
		mTextViewMask = (TextView) mRootView.findViewById(R.id.tv_mask);
		mRandom = new Random();

		mSolutionEditText = (EditText) mRootView.findViewById(R.id.et_netw);

		generarPregunta();

		mButtonanswer = (Button) mRootView.findViewById(R.id.but_ans);
		mButtonanswer.setOnClickListener(this);

		mButtonSolution = (Button) mRootView.findViewById(R.id.but_solution);
		mButtonSolution.setOnClickListener(this);

		mHandler = new Handler();

		return mRootView;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.but_ans) {
			checkAnswer();
		} else if (v.getId() == R.id.but_solution) {
			solutionNetworkAddress();
		}

	}

	public void checkAnswer() {
		String net = intToIpString(mIp & mMask);
		if (net.equals(mSolutionEditText.getText().toString())) {
			showAnimationAnswer(true);
			if (this.mGameMode) {
				gameModeControl();
			}
			mSolutionEditText.setText("");
			generarPregunta();
		} else {
			showAnimationAnswer(false);
		}
	}

	public void solutionNetworkAddress() {
		mSolutionEditText.setTextColor(Color.BLACK);
		String net = intToIpString(mIp & mMask);
		mSolutionEditText.setText(net);
		mSolutionEditText.setSelection(mSolutionEditText.getText().length());
	}

	public void generarPregunta() {
		mIp = generateRandomIP();
		mMask = generateRandomMask();

		// The solution cannot be all zeros or the IP itself
		while ((mIp & mMask) == 0
				|| (mIp & mMask) == mIp) {
			mIp = generateRandomIP();
		}
		
		mTextViewIp.setText(intToIpString(mIp));
		mTextViewMask.setText(intToIpString(mMask));
		
		// Show the IP as starting point for the network mask, because they
		// probably have many numbers in common
		mSolutionEditText.setText(intToIpString(mIp));
		
		// Set the focus on the edit text and show the keyboard
		if(mSolutionEditText.requestFocus()) {
		    getActivity().getWindow().setSoftInputMode(
		    		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
		
		// Set the cursor at the end so that the user can begin deleting the
		// numbers that are not part of the network address
		mSolutionEditText.setSelection(mSolutionEditText.getText().length());
	}

	private int generateRandomIP() {
		int value = 0;
		while (value == 0) {
			value = mRandom.nextInt(0x7ffffffe);
		}
		return value;
	}

	

	// /--------------------- Modo Jugar -----------------------

	private void gameModeControl() {
		increasePoints(POINTS_FOR_QUESTION);

		if (currentQuestionCounter >= MAX_QUESTIONS) {
			// won
			mSolutionEditText.setText("");
			this.mWon = true;
			this.endGame();

		}

		if (currentQuestionCounter < MAX_QUESTIONS && getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			mSolutionEditText.setText("");
			this.mWon = false;
			this.endGame();
		}
		currentQuestionCounter++;
	}

	private void increasePoints(int val) {
		this.mPoints = this.mPoints + val;
		updatePointsTextView(this.mPoints);
	}

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.puntos_NA);
		tvPoints.setText(getResources().getString(R.string.points) + " "
				+ String.valueOf(p));
	}

	@Override
	public void startGame() {
		super.startGame();
		super.setGameDuration(GAME_DURATION_MS);

		// set starting points of textview
		updatePointsTextView(0);
		updateToGameMode();
	}

	private void updateToGameMode() {
		mSolutionEditText.setText("");
		mGameMode = true;

		generarPregunta();

		Button solution = (Button) mRootView.findViewById(R.id.but_solution);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.puntos_NA);
		points.setVisibility(View.VISIBLE);
	}

	private void updateToTrainMode() {
		mGameMode = false;
		mSolutionEditText.setText("");

		Button solution = (Button) mRootView.findViewById(R.id.but_solution);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.puntos_NA);
		points.setVisibility(View.INVISIBLE);
	}

	@Override
	public void cancelGame() {
		mSolutionEditText.setText("");
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	protected void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;
		// every remaining second gives one extra point.
		this.mPoints = (int) (this.mPoints + remainingTimeInSeconds);

		if (this.mWon) {
			savePoints();
		}

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();
	}

	private void reset() {
		this.mPoints = 0;
		this.currentQuestionCounter = 0;
		this.mWon = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPoints, R.string.network_address, new Date(),
					username, level());

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
