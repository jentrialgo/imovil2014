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
import android.util.Log;
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


	private static final long GAME_DURATION_MS = 3 * 1000; //2 minutos de juego.
	private ArrayList<ProtocolTest> testList=null;
	private View mRootView;
	private View mCardView;
	private RadioButton[] respRadioButton;
	private Button seeSolution;
	private Button check;
	private TextView question;
	private ProtocolTest test;
	private RadioGroup rg;
	private int currentQuestionCounter = 1;
	private boolean won = false;
	private int points;
	private int partialPoints;
	private int totalFails=0;
	private boolean gameMode = false;
	private  boolean flag;

	public static ProtocolExerciseFragment newInstance() 
	{
		ProtocolExerciseFragment fragment = new ProtocolExerciseFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView=inflater.inflate(R.layout.fragment_protocol, container, false);
        ProtocolDataBaseHelper db = new ProtocolDataBaseHelper(this.getActivity(), DB_NAME, null, DB_VERSION);
        question = (TextView) mRootView.findViewById(R.id.exerciseQuestion);
        mCardView = (RelativeLayout) mRootView.findViewById(R.id.card);
        rg = new RadioGroup (getActivity());
        addAnswerRadioButtons();
        seeSolution = (Button) mRootView.findViewById(R.id.seesolution);
        check = (Button) mRootView.findViewById(R.id.checkbutton);
        //Manejador del evento de mostrar solución (solo en modo entrenamiento).
        seeSolution.setOnClickListener( new OnClickListener() 
        {
			private Object TAG;

			@Override
			public void onClick(View arg0) 
			{

				flag = false;
				int i = 0;
				while (i<NUMBER_OF_ANSWERS)
				{
					if ((respRadioButton[i].getText().equals(test.getResponse())) && (!flag))
					{
						respRadioButton[i].setChecked(true);						
						flag=true;
					}
					i++;
				}

    	     }
		 });
        //Manejador del botón de comprobación de respuesta.
        check.setOnClickListener( new OnClickListener() 
        {
			@Override
			public void onClick(View arg0) 
			{
				flag = false;
				int index = 0;
				while ((index<NUMBER_OF_ANSWERS) && (!flag))
				{
					if (respRadioButton[index].isChecked()) {
						checkIfButtonClickedIsCorrectAnswer(index);
						flag = true;
					}
					index++;
				}
				if (!gameMode)
					newQuestion();
    	     }
        });
        try 
        {
            db.createDataBase();
            db.openDataBase();
        } catch (IOException e) {
        	// TODO: extraer cadenas a recursos
        	Toast.makeText(this.getActivity(), "Error al abrir la base de datos",
        			Toast.LENGTH_LONG).show();
        }

        testList=db.loadData();
        newQuestion();               
		return mRootView;
	}
	

	private void addAnswerRadioButtons() {
		respRadioButton = new RadioButton[NUMBER_OF_ANSWERS];
		for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
			addAnswerRadioButton(i,rg); //Añadir respueta al array.
		}
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_LEFT);
		TextView question = (TextView) mRootView.findViewById(R.id.exerciseQuestion); //Obtener pregunta.
		params.addRule(RelativeLayout.BELOW, question.getId());	 //Debajo de la pregunta.
		((RelativeLayout) mCardView).addView(rg, params);
	}

	private void addAnswerRadioButton(int index, RadioGroup rg) {
		respRadioButton[index] = new RadioButton(this.getActivity());
		rg.addView(respRadioButton[index]);
	}
	

	private void checkIfButtonClickedIsCorrectAnswer(int index) {
		boolean response = false;
		if (respRadioButton[index].getText().equals(test.getResponse())) 
		{
			response = true;
		}
		else 
		{
			response = false;
		}
		super.showAnimationAnswer(response);
		// only create new Question if user has right input
		if (this.gameMode) {
			if (response) {
				gameModeControl();
				newQuestion();
			}
			else if (totalFails<3) {
			totalFails++;
			partialPoints = partialPoints - REST_FOR_FAIL;
			}
			else 
			{
				partialPoints = 0;
				gameModeControl();
				newQuestion();
			}
		}
	}

	private void newQuestion()	{
		rg.clearCheck();
		partialPoints = POINTS_FOR_QUESTION; //Puntos parciales (al fallar se resta 2 puntos).
		test = testList.get((int)(Math.random()*(14-0))+0); 
     	//Mostrar pregunta y opciones.
		question.setText(test.getQuestion());
		for (int i = 0; i < NUMBER_OF_ANSWERS; i++) {
			respRadioButton[i].setText(test.getOption(i));
		}
	}
	
	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) mRootView.findViewById(R.id.points);
		tvPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
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
		gameMode  = true;

		newQuestion();

		Button solution = (Button) mRootView.findViewById(R.id.seesolution);
		solution.setVisibility(View.GONE);
		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}
	
	private void updateToTrainMode() {
		gameMode = false;

		Button solution = (Button) mRootView.findViewById(R.id.seesolution);
		solution.setVisibility(View.VISIBLE);

		TextView points = (TextView) mRootView.findViewById(R.id.points);
		points.setVisibility(View.GONE);
	}
	
	private void gameModeControl() {
		increasePoints(partialPoints);
		
		if (currentQuestionCounter >= MAX_QUESTIONS) {
			this.won = true;
			this.endGame();
		}

		if (currentQuestionCounter < MAX_QUESTIONS && getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			this.won = false;
			this.endGame();
		}
		currentQuestionCounter++;
		totalFails=0;
	}
	
	@Override
	void endGame() {
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		this.points = (int) (this.points + remainingTimeInSeconds);

		if (this.won)
			savePoints();

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();
	
	}
	
	private void increasePoints(int val) {
		points = points + val;
		updatePointsTextView(points);
	}
	
	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (won) {
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
	
	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {

			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.points, R.string.protocol, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void reset() {
		points = 0;
		currentQuestionCounter = 0;
		won = false;
		updatePointsTextView(0);
	}

}
