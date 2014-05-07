/**

Copyright 2014 Profesores y alumnos de la asignatura Inform?tica M?vil de la EPI de Gij?n

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
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignedMagnitudeExerciseFragment extends BaseExerciseFragment implements OnClickListener {
	
	private TextView textViewQuestion;
	private Button checkAnswer;
	private Button giveAnswer;
	private Button bChange;
	private EditText editTextAnswer;
	private Editable text;
	private TextView eTitle;
	private String question;
	private String answer;
	private String respUsuario;
	private Handler imageHandler;
	private View rootView;
	private boolean toSM=true;
	private int numberToConvert;
	
	//Modo juego
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 5 * 1000 * 60; // 5min
	private boolean won = false;
	
	private boolean gameMode = false;
	private int points;
	private int currentQuestionCounter = 1;
	
	
	public static SignedMagnitudeExerciseFragment newInstance() {
		
		SignedMagnitudeExerciseFragment fragment = new SignedMagnitudeExerciseFragment();
		return fragment;
	}
	
	public SignedMagnitudeExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_signed_magnitude, container, false);
		
		eTitle = (TextView) rootView.findViewById(R.id.exercisetitle);
		textViewQuestion = (TextView)rootView.findViewById(R.id.textViewPregunta);
		
		//Relleno el TextView con la primera pregunta
		generateRandomNumber();
		
		checkAnswer = (Button)rootView.findViewById(R.id.check_answer);
		giveAnswer = (Button)rootView.findViewById(R.id.give_solution);
		bChange = (Button) rootView.findViewById(R.id.change);
		checkAnswer.setOnClickListener(this);
		giveAnswer.setOnClickListener(this);
		imageHandler = new Handler();
		
		editTextAnswer = (EditText)rootView.findViewById(R.id.editTextRespuesta);
		editTextAnswer.clearFocus();
		
		bChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(toSM){
					eTitle.setText(getResources().getString(
							R.string.convert_to_signed_magnitude));
					toSM=false;
					generateRandomNumber();
				}
				else{
					eTitle.setText(getResources().getString(
							R.string.convert_to_dec));
					toSM=true;
					generateRandomNumber();
				}
				
			}
		});
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.check_answer:
			if(toSM){
				text = editTextAnswer.getText();
				respUsuario = text.toString();
				//Si la respuesta es correcta...
				if(respUsuario.equals(answer)){
					if(gameMode){
						gameModeControl();
					}
					showAnimationAnswer(true);
					editTextAnswer.setTextColor(Color.GREEN);
					editTextAnswer.setText(respUsuario);
					/*  1,5 segundos después genero la siguiente
				 	*  pregunta, vacío el editText y volvemos a poner 
				 	*  su color en negro */
					imageHandler.postDelayed(new Runnable() {
						public void run() {
							generateRandomNumber();
							editTextAnswer.setText("");
							editTextAnswer.setTextColor(Color.BLACK);
						}
					}, 1500);
				}
				//Si la respuesta es incorrecta...
				else{
					showAnimationAnswer(false);
					editTextAnswer.setTextColor(Color.RED);
					editTextAnswer.setText(respUsuario);
				
					/*  1,5 segundos después vacío el editText
				 	*  y volvemos a poner su color en negro */
					imageHandler.postDelayed(new Runnable() {
						public void run() {
							editTextAnswer.setText("");
							editTextAnswer.setTextColor(Color.BLACK);
						}
					}, 1500);
				}
			}
			
			else{
				text = editTextAnswer.getText();
				respUsuario = text.toString();
				//Si la respuesta es correcta...
				if(respUsuario.equals(answer)){
					if(gameMode){
						gameModeControl();
					}
					showAnimationAnswer(true);
					editTextAnswer.setTextColor(Color.GREEN);
					editTextAnswer.setText(respUsuario);
					/* 1,5 segundos después hago que la imagen desaparezca, paso a la siguiente
				 	*  pregunta, vacío el editText y volvemos a poner su color en negro */
					imageHandler.postDelayed(new Runnable() {
						public void run() {
							generateRandomNumber();
							editTextAnswer.setText("");
							editTextAnswer.setTextColor(Color.BLACK);
						}
					}, 1500);
				}
				//Si la respuesta es incorrecta...
				else{
					showAnimationAnswer(false);
					editTextAnswer.setTextColor(Color.RED);
					editTextAnswer.setText(respUsuario);
				
					/*  1,5 segundos después vacío el editText
				 	*  y volvemos a poner su color en negro */
					imageHandler.postDelayed(new Runnable() {
						public void run() {
							editTextAnswer.setText("");
							editTextAnswer.setTextColor(Color.BLACK);
						}
					}, 1500);
				}
				
			}
			break;
			
		
		case R.id.give_solution:
			editTextAnswer.setTextColor(Color.RED);
				editTextAnswer.setText(answer);
				/*  2 segundos después genero la siguiente pregunta, 
				 * vacío el editText y volvemos a poner
			 	 *  su color en negro */
				imageHandler.postDelayed(new Runnable() {
				public void run() {
						generateRandomNumber();
						editTextAnswer.setText("");
						editTextAnswer.setTextColor(Color.BLACK);
					}
				}, 2000);
			break;
			
		}
		
		
		
	}
	
	//Modo juego
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
		TextView tvPoints = (TextView) rootView.findViewById(R.id.points);
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
	
	private void updateToGameMode() {
		gameMode = true;

		generateRandomNumber();

		Button solution = (Button) rootView.findViewById(R.id.give_solution);
		solution.setVisibility(View.INVISIBLE);
		Button change = (Button) rootView.findViewById(R.id.change);
		change.setVisibility(View.INVISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}
	
	private void updateToTrainMode() {
		gameMode = false;

		Button solution = (Button) rootView.findViewById(R.id.give_solution);
		solution.setVisibility(View.VISIBLE);
		Button change = (Button) rootView.findViewById(R.id.change);
		change.setVisibility(View.VISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.points);
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
					this.points, R.string.sign_and_magnitude, new Date(), username);

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
	
	public void generateRandomNumber() {
		Random randomGenerator = new Random();
		numberToConvert = randomGenerator.nextInt(2000);
		if (toSM)
			/* Si el número generado es por ejemplo 1500, lo tratamos
			* como el número positivo 500, en cambio si el número generado
			* es 500 lo tratamos como -500 para pasarlo a signo magnitud */
			if(numberToConvert>=1000){
				numberToConvert=numberToConvert-1000;
				answer = Integer.toString(numberToConvert);
				question = "0" + Integer.toBinaryString(numberToConvert);
				textViewQuestion.setText(question);
			}
			else{
				question = "1" + Integer.toBinaryString(numberToConvert);
				textViewQuestion.setText(question);
				numberToConvert=numberToConvert*(-1);
				answer = Integer.toString(numberToConvert);
			}
		else{
			if(numberToConvert>=1000){
				numberToConvert=numberToConvert-1000;
				question = Integer.toString(numberToConvert);
				answer = "0" + Integer.toBinaryString(numberToConvert);
				textViewQuestion.setText(question);
			}
			else{
				answer = "1" + Integer.toBinaryString(numberToConvert);
				numberToConvert=numberToConvert*(-1);
				question = Integer.toString(numberToConvert);
				textViewQuestion.setText(question);
			}
			
		}
			
	}

	
	
	
	
}