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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NetworkLayerExerciseFragment extends BaseExerciseFragment {
	private View rootView;		
	private TextView pregunta;
	private TextView layerPoints;
	private RadioGroup opciones;

	private RadioButton rb_layer;
	private RadioButton rb_network;
	private RadioButton rb_transport;
	private RadioButton rb_application;

	private Button comprobar;
	private Button solucion;
	private String[] preguntas;
	private String[] respuestas;
	private String rb_pressed="";
	private int indice =0;

	private final int POINTS_FOR_QUESTION = 10;
	private final int MAX_QUESTIONS = 5;
	private final long GAME_DURATION_MS = 1 * 1000 * 60; // 1min
	private int currentQuestionCounter = 0;
	private boolean won = false;

	private int points;

	//constructores
	public NetworkLayerExerciseFragment() 
	{

	}

	//metodos
	public static NetworkLayerExerciseFragment newInstance() 
	{
		NetworkLayerExerciseFragment fragment = new NetworkLayerExerciseFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		rootView =inflater.inflate(R.layout.fragment_layer, container, false);
		//TextView para mostrar la pregunta
		pregunta = (TextView) rootView.findViewById(R.id.textlayer);
		//TextView para los puntos en el modo jugar
		layerPoints = (TextView) rootView.findViewById(R.id.points_layer);

		//Radiogrup
		opciones = (RadioGroup) rootView.findViewById(R.id.layer_group);
		rb_layer = (RadioButton) rootView.findViewById(R.id.link_layer);
		rb_network = (RadioButton) rootView.findViewById(R.id.internet_layer);
		rb_transport = (RadioButton) rootView.findViewById(R.id.transport_layer);
		rb_application = (RadioButton) rootView.findViewById(R.id.application_layer);


		opciones.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId)
			{
				// TODO Auto-generated method stub
				switch(checkedId){
				case R.id.link_layer:
					rb_pressed = getResources().getString(R.string.link_layer);
					break;
				case R.id.internet_layer:
					rb_pressed = getResources().getString(R.string.internet_layer);
					break;
				case R.id.transport_layer:
					rb_pressed = getResources().getString(R.string.transport_layer);
					break;
				case R.id.application_layer:
					rb_pressed = getResources().getString(R.string.application_layer);
					break;
				}
			}
		});

		//Buttons
		comprobar = (Button) rootView.findViewById(R.id.button_layer);
		solucion = (Button) rootView.findViewById(R.id.button_solutionlayer);

		comprobar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_layer){
					CompruebaRespuesta();
				}
			}
		});

		solucion.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_solutionlayer){
					Solucion();
				}
			}
		});

		//Arrays
		preguntas = getResources().getStringArray(R.array.layer_exercise_questions);
		respuestas = getResources().getStringArray(R.array.layer_exercise_answers);
		RANDOM();
		pregunta.setText(preguntas[indice]);	

		return rootView;
	}

	protected void Solucion() {
		if (respuestas[indice].equals("Capa de enlace")){
			rb_layer.setChecked(true);
		}
		else if (respuestas[indice].equals("Capa de internet")){
			rb_network.setChecked(true);
		}
		else if (respuestas[indice].equals("Capa de transporte")){
			rb_transport.setChecked(true);
		}
		else {
			rb_application.setChecked(true);
		}
	}

	private void CompruebaRespuesta() {
		if (rb_pressed.equals(respuestas[indice])){
			showAnimationAnswer(true);
			if (mIsPlaying){
				gameModeControl();
			}
			RANDOM();			
			pregunta.setText(preguntas[indice]);
		}
		else showAnimationAnswer(false);		
	}

	//Metodo para generar un número aleatorio
	public int RANDOM(){
		Random ran = new Random();
		indice = ran.nextInt(11);
		return indice;
	}

	private void increasePoints(int val) {
		this.points = this.points + val;
		updatePointsTextView(this.points);
	}

	private void updatePointsTextView(int p) {		
		layerPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
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

	@Override
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);
		// set starting points of textview
		updatePointsTextView(0); 
		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		solucion.setVisibility(View.INVISIBLE);
		layerPoints.setVisibility(View.VISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	private void updateToTrainMode() {
		solucion.setVisibility(View.VISIBLE);
		layerPoints.setVisibility(View.GONE);
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

	@Override
	void endGame() {
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
		this.points = (int) (this.points + remainingTimeInSeconds);

		if (this.won){
			String username = getResources().getString(R.string.default_user_name);
			try {
				HighscoreManager.addScore(getActivity().getApplicationContext(),
						this.points, R.string.network_layer, new Date(), username);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		dialogGameOver();
		super.endGame();
		updateToTrainMode();
		this.points = 0;
		this.currentQuestionCounter = 0;
		this.won = false;
	}

}





