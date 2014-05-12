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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CidrExerciseFragment extends BaseExerciseFragment implements OnClickListener{
	
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 1 * 1000 * 60; // 1min
	private boolean gameMode = false;

	
	private boolean won = false;
	
	private int puntos;
	private int currentQuestionCounter = 1;
	
	private Button bCheck;
	private Button bSol;
	private String [] mascaras;
	private String [] respuestas;
	private TextView mascara;
	int n;
	EditText answer;
	public int mask;
	public static final int RANDOM_MASK = 5;
	public View rootView;
	

	public static CidrExerciseFragment newInstance() {
		
		CidrExerciseFragment fragment = new CidrExerciseFragment();
		return fragment;
	}
	
	public CidrExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		rootView = inflater.inflate(R.layout.fragment_cidr, container, false);
		
		//Cargar los views
		
		CargaViews();
		
		bCheck=(Button) rootView.findViewById(R.id.cButton);
		bCheck.setOnClickListener(this);

		bSol=(Button) rootView.findViewById(R.id.sButton);
		bSol.setOnClickListener(this);


		GenerarPregunta();
	
		
		
		//Usamos listeners para los botones "Comprobar" y "Solucion"
		
		bCheck.setOnClickListener((OnClickListener) this);
		
		bSol.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSolution(mask);
			}
		});
		
		
		return rootView;
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.cButton) {
			checkAnswer((answer.getEditableText().toString()));
			clearUserInput();

		}
		if (view.getId() == R.id.sButton) {
			showSolution(n);
		}

	}
	
	private void clearUserInput() {
		EditText userInput = (EditText) rootView.findViewById(R.id.respuesta);
		userInput.setText("");
	}
	
	public void CargaViews(){
		
		mascaras = getResources().getStringArray(R.array.mascaras);
		mascara = (TextView) rootView.findViewById(R.id.mascara);
		answer = (EditText) rootView.findViewById(R.id.respuesta);
		respuestas = getResources().getStringArray(R.array.cidr);
		
	}
	
	//Metodo para comprobar la respuesta
	
	
	public void checkAnswer (String ans) {
		
		if (ans.isEmpty()) {
			super.showAnimationAnswer(false);
			return;
		}
		//Si es correcta, cambia la m�scara por una nueva y pone el "EditText" en blanco
		if ((ans.toString().equals(respuestas[mask].toString()))){
			showAnimationAnswer(true);
			if (this.gameMode) {
				gameModeControl();
			}

			GenerarPregunta();
					
			} else
				showAnimationAnswer(false);		
	}
	
	//Metodo para mostrar la solucion
	public void showSolution(int n) {
		
		answer.setText(respuestas[n]);
		
	}
	
	//Metodo para generar un n�mero aleatorio
	public int RANDOM(){

		Random ran = new Random();
		n = ran.nextInt(RANDOM_MASK);

		return n;
	}
	
	public void GenerarPregunta(){
		
		mask = RANDOM();
		mascara.setText(mascaras[mask]);
		
	}
	
	///--------------------- Modo Jugar -----------------------
	
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
		TextView tvPoints = (TextView) rootView.findViewById(R.id.puntos);
		tvPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
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

		Button solution = (Button) rootView.findViewById(R.id.sButton);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.puntos);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		gameMode = false;

		Button solution = (Button) rootView.findViewById(R.id.sButton);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.puntos);
		points.setVisibility(View.INVISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	void endGame() {
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
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
					this.puntos, 0, new Date(), username);

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

