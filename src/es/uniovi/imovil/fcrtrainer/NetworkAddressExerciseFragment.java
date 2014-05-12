package es.uniovi.imovil.fcrtrainer;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkAddressExerciseFragment extends BaseExerciseFragment
		implements View.OnClickListener {

	private static final int POINTS_FOR_QUESTION = 10; // 10 puntos por pregunta
	private static final int MAX_QUESTIONS = 5; // El juego consta de 5
												// preguntas
	private static final long GAME_DURATION_MS = 2 * 1000 * 60; // 1 minuto de
																// juego
	private boolean gameMode = false;

	private boolean won = false;

	private int puntos;
	private int currentQuestionCounter = 1;

	View rootView;
	int questionIndex; // Índice en los arrays de recursos
	String[] ip;
	String[] mask;
	String[] net;
	TextView textViewIp;
	TextView textViewMask;
	TextView tv1;
	EditText solutionEditText;
	ImageView imageviewsolution;
	Button banswer;
	Button bsolution;
	Handler handler;
	Random random;

	public static NetworkAddressExerciseFragment newInstance() {

		NetworkAddressExerciseFragment fragment = new NetworkAddressExerciseFragment();
		return fragment;
	}

	public NetworkAddressExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_network_address,
				container, false);

		ip = getResources().getStringArray(R.array.ips);
		mask = getResources().getStringArray(R.array.masks);
		net = getResources().getStringArray(R.array.nets);
		textViewIp = (TextView) rootView.findViewById(R.id.tv_ip);
		textViewMask = (TextView) rootView.findViewById(R.id.tv_mask);
		tv1 = (TextView) rootView.findViewById(R.id.exercisetitleNA);
		random = new Random();

		solutionEditText = (EditText) rootView.findViewById(R.id.et_netw);

		GenerarPregunta();

		banswer = (Button) rootView.findViewById(R.id.but_ans);
		banswer.setOnClickListener(this);

		bsolution = (Button) rootView.findViewById(R.id.but_solution);
		bsolution.setOnClickListener(this);

		handler = new Handler();

		return rootView;

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
		if (net[questionIndex].equals(solutionEditText.getText().toString())) {
			showAnimationAnswer(true);
			if (this.gameMode) {
				gameModeControl();
				solutionEditText.setText("");
				GenerarPregunta();
			} else {
				if (currentQuestionCounter < MAX_QUESTIONS) {
					currentQuestionCounter++;
					solutionEditText.setText("");
					GenerarPregunta();
				} else {
					currentQuestionCounter = 1;
					handler.postDelayed(new Runnable() {
						public void run() {

							banswer.setVisibility(Button.GONE);
							bsolution.setVisibility(Button.GONE);
							solutionEditText.setVisibility(EditText.GONE);
							textViewIp.setVisibility(TextView.GONE);
							textViewMask.setVisibility(TextView.GONE);

							tv1.setText(R.string.end_train);
							tv1.setTextSize(47);

						}
					}, 1500);

				}

			}
		} else {
			showAnimationAnswer(false);

		}
	}

	public void solutionNetworkAddress() {
		solutionEditText.setTextColor(Color.BLACK);
		solutionEditText.setText(net[questionIndex]);
	}

	public void GenerarPregunta() {

		questionIndex = random.nextInt(ip.length);

		textViewIp.setText(ip[questionIndex]);
		textViewMask.setText(mask[questionIndex]);
	}

	// /--------------------- Modo Jugar -----------------------

	private void gameModeControl() {
		increasePoints(POINTS_FOR_QUESTION);

		if (currentQuestionCounter >= MAX_QUESTIONS) {
			// won
			this.won = true;
			this.endGame();

		}

		if (currentQuestionCounter < MAX_QUESTIONS && getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			this.won = false;
			this.endGame();
		}
		currentQuestionCounter++;
	}

	private void increasePoints(int val) {
		this.puntos = this.puntos + val;
		updatePointsTextView(this.puntos);
	}

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) rootView.findViewById(R.id.puntos_NA);
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
		gameMode = true;

		GenerarPregunta();

		Button solution = (Button) rootView.findViewById(R.id.but_solution);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.puntos_NA);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		gameMode = false;

		Button solution = (Button) rootView.findViewById(R.id.but_solution);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.puntos_NA);
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
		this.puntos = (int) (this.puntos + remainingTimeInSeconds);

		if (this.won)
			savePoints();

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();
	}

	private void reset() {
		this.puntos = 0;
		this.currentQuestionCounter = 0;
		this.won = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {

			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.puntos, R.string.network_address, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (this.won) {
			message = getResources().getString(R.string.won) + " "
					+ getResources().getString(R.string.points) + " "
					+ this.puntos;
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
