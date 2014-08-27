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

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
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
import android.widget.EditText;
import android.widget.TextView;

public class CidrExerciseFragment extends BaseExerciseFragment implements
		OnClickListener {

	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 1 * 1000 * 60; // 1min
	private static final int RANDOM_MASK = 5;

	private boolean mGameMode = false;

	private boolean mWon = false;

	private int mPuntos;
	private int mCurrentQuestionCounter = 1;

	private Button mButtonCheck;
	private Button mButtonSol;
	private String[] mMascaras;
	private String[] mRespuestas;
	private TextView mMascara;
	int mMaskIndex;
	EditText mAnswer;
	public int mMask;
	public View mRootView;

	public static CidrExerciseFragment newInstance() {
		CidrExerciseFragment fragment = new CidrExerciseFragment();
		return fragment;
	}

	public CidrExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_cidr, container, false);

		// Cargar los views
		CargaViews();

		mButtonCheck = (Button) mRootView.findViewById(R.id.cButton);
		mButtonCheck.setOnClickListener(this);

		mButtonSol = (Button) mRootView.findViewById(R.id.sButton);
		mButtonSol.setOnClickListener(this);

		GenerarPregunta();

		// Usamos listeners para los botones "Comprobar" y "Solucion"
		mButtonCheck.setOnClickListener((OnClickListener) this);

		mButtonSol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSolution(mMask);
			}
		});

		return mRootView;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.cButton) {
			checkAnswer((mAnswer.getEditableText().toString()));
			clearUserInput();

		}
		if (view.getId() == R.id.sButton) {
			showSolution(mMaskIndex);
		}

	}

	private void clearUserInput() {
		EditText userInput = (EditText) mRootView.findViewById(R.id.respuesta);
		userInput.setText("");
	}

	public void CargaViews() {
		mMascaras = getResources().getStringArray(R.array.mascaras);
		mMascara = (TextView) mRootView.findViewById(R.id.mascara);
		mAnswer = (EditText) mRootView.findViewById(R.id.respuesta);
		mRespuestas = getResources().getStringArray(R.array.cidr);
	}

	// Metodo para comprobar la respuesta
	public void checkAnswer(String ans) {
		if (ans.isEmpty()) {
			super.showAnimationAnswer(false);
			return;
		}
		// Si es correcta, cambia la máscara por una nueva y pone el "EditText"
		// en blanco
		if ((ans.toString().equals(mRespuestas[mMask].toString()))) {
			showAnimationAnswer(true);
			if (this.mGameMode) {
				gameModeControl();
			}

			GenerarPregunta();
		} else {
			showAnimationAnswer(false);
		}
	}

	// Metodo para mostrar la solucion
	public void showSolution(int n) {
		mAnswer.setText(mRespuestas[n]);
	}

	// Metodo para generar un número aleatorio
	public int random() {
		Random ran = new Random();
		mMaskIndex = ran.nextInt(RANDOM_MASK);

		return mMaskIndex;
	}

	public void GenerarPregunta() {
		mMask = random();
		mMascara.setText(mMascaras[mMask]);
	}

	// /--------------------- Modo Jugar -----------------------

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
		this.mPuntos = this.mPuntos + val;
		updatePointsTextView(this.mPuntos);
	}

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.puntos);
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
		mGameMode = true;

		GenerarPregunta();

		Button solution = (Button) mRootView.findViewById(R.id.sButton);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.puntos);
		points.setVisibility(View.VISIBLE);
	}

	private void updateToTrainMode() {
		mGameMode = false;

		Button solution = (Button) mRootView.findViewById(R.id.sButton);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.puntos);
		points.setVisibility(View.INVISIBLE);
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
		this.mPuntos = (int) (this.mPuntos + remainingTimeInSeconds);

		if (this.mWon) {
			savePoints();
		}

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();
	}

	private void reset() {
		this.mPuntos = 0;
		this.mCurrentQuestionCounter = 0;
		this.mWon = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {

			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPuntos, 0, new Date(), username);
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
					+ this.mPuntos;
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
