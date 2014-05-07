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

public class HostCountExerciseFragment extends BaseExerciseFragment{

	private static final int RANDOM_NUMBER_LIMIT = 30;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 10 * 1000 * 60; // 10min
	
	private boolean won = false;
	private boolean gameMode = false;
	private int points;
	private int currentQuestionCounter = 1;
	
	private View mRootView;
	private Button btnCheck;
	private Button btnSolution;
	private String [] hostCountQuestions;
	private String [] hostCountAnswers;
	private TextView question;	
	EditText answer;
	int randomNumberQuestion;

	// Constructor
	public HostCountExerciseFragment() 
	{
	}
	
	public static HostCountExerciseFragment newInstance() 
	{
		HostCountExerciseFragment fragment = new HostCountExerciseFragment();
		return fragment;
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		mRootView=inflater.inflate(R.layout.fragment_host_count, container, false);
		
        btnCheck =  (Button) mRootView.findViewById(R.id.btnCheckAnswer);
        btnSolution =  (Button) mRootView.findViewById(R.id.btnSolution);
    	question = (TextView) mRootView.findViewById(R.id.question);
    	answer = (EditText) mRootView.findViewById(R.id.answer);
    	hostCountQuestions = getResources().getStringArray(R.array.host_count_questions);
    	hostCountAnswers = getResources().getStringArray(R.array.host_count_answers);
    	randomNumberQuestion = generateRandomNumber();
    	//Carga una de las preguntas del array 
    	newQuestion();
    	
    	
        btnCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	checkAnswer((answer.getText().toString()));
            }
        });
        
        btnSolution.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSolution(randomNumberQuestion);
			}
		});
        
		return mRootView;
	}
	
	public void newQuestion(){
		question.setText(hostCountQuestions[randomNumberQuestion]);
	}
	
	private int generateRandomNumber() {
		Random rn = new Random();
		
		// Funcion nextInt devuelve un numero aleatorio entre [0, limite)
		int x = rn.nextInt(RANDOM_NUMBER_LIMIT);
		return x;

	}

	public void checkAnswer (String a) {
		//Si la respuesta es correcta, genera otra nueva pregunta y borra respuesta
		if ((a.toString().equals(hostCountAnswers[randomNumberQuestion].toString()))){
			showAnimationAnswer(true);
			randomNumberQuestion = generateRandomNumber();
			question.setText(hostCountQuestions[randomNumberQuestion]);
			if (this.gameMode) {
				gameModeControl();
			}
			answer.setText("");
			
			} else
				showAnimationAnswer(false);		

	}
	
	//Metodo para mostrar la solucion
	public void showSolution(int numberOfQuestion) {
		answer.setText(hostCountAnswers[numberOfQuestion]);		
	}
	
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);
		
		// set starting points of textview
		updatePointsTextView(0); 

		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		gameMode = true;

		newQuestion();

		Button solution = (Button) mRootView.findViewById(R.id.btnSolution);
		solution.setVisibility(View.INVISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		gameMode = false;

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
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
		this.points = (int) (this.points + remainingTimeInSeconds);

		if (this.won)
			savePoints();

		dialogGameOver();
		super.endGame();
		updateToTrainMode();
		reset();
	}

	private void reset() {
		this.points = 0;
		this.currentQuestionCounter = 0;
		this.won = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.points, R.string.host_count, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
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
		this.points = this.points + val;
		updatePointsTextView(this.points);
	}
	
	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.points);
		tvPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (this.won) {
			message = getResources().getString(R.string.won) + " "
					+ getResources().getString(R.string.points) + " "
					+ this.points;
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
