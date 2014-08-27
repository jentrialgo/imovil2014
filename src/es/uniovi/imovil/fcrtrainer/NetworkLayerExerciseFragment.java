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
	private final static int POINTS_FOR_QUESTION = 10;
	private final static int MAX_QUESTIONS = 5;
	private final static long GAME_DURATION_MS = 1 * 1000 * 60; // 1min

	private View mRootView;		
	private TextView mQuestion;
	private TextView mLayerPoints;
	private RadioGroup mOptions;

	private RadioButton mRblayer;
	private RadioButton mRbnetwork;
	private RadioButton mRbTransport;
	private RadioButton mRbAplication;

	private Button mCheck;
	private Button mSolution;
	private String[] mQuestions;
	private String[] mAnswers;
	private String mRbPressed="";
	private int mIndex =0;

	private int mPoints;

	private int mCurrentQuestionCounter = 0;
	private boolean mWon = false;

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

		mRootView =inflater.inflate(R.layout.fragment_layer, container, false);
		//TextView para mostrar la pregunta
		mQuestion = (TextView) mRootView.findViewById(R.id.textlayer);
		//TextView para los puntos en el modo jugar
		mLayerPoints = (TextView) mRootView.findViewById(R.id.points_layer);

		//Radiogrup
		mOptions = (RadioGroup) mRootView.findViewById(R.id.layer_group);
		mRblayer = (RadioButton) mRootView.findViewById(R.id.link_layer);
		mRbnetwork = (RadioButton) mRootView.findViewById(R.id.internet_layer);
		mRbTransport = (RadioButton) mRootView.findViewById(R.id.transport_layer);
		mRbAplication = (RadioButton) mRootView.findViewById(R.id.application_layer);

		mOptions.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId)
			{
				switch(checkedId){
				case R.id.link_layer:
					mRbPressed = getResources().getString(R.string.link_layer);
					break;
				case R.id.internet_layer:
					mRbPressed = getResources().getString(R.string.internet_layer);
					break;
				case R.id.transport_layer:
					mRbPressed = getResources().getString(R.string.transport_layer);
					break;
				case R.id.application_layer:
					mRbPressed = getResources().getString(R.string.application_layer);
					break;
				}
			}
		});

		//Buttons
		mCheck = (Button) mRootView.findViewById(R.id.button_layer);
		mSolution = (Button) mRootView.findViewById(R.id.button_solutionlayer);

		mCheck.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_layer){
					CompruebaRespuesta();
				}
			}
		});

		mSolution.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_solutionlayer){
					Solucion();
				}
			}
		});

		//Arrays
		mQuestions = getResources().getStringArray(R.array.layer_exercise_questions);
		mAnswers = getResources().getStringArray(R.array.layer_exercise_answers);
		random();
		mQuestion.setText(mQuestions[mIndex]);	

		return mRootView;
	}

	protected void Solucion() {
		if (mAnswers[mIndex].equals("Capa de enlace")){
			mRblayer.setChecked(true);
		}
		else if (mAnswers[mIndex].equals("Capa de internet")){
			mRbnetwork.setChecked(true);
		}
		else if (mAnswers[mIndex].equals("Capa de transporte")){
			mRbTransport.setChecked(true);
		}
		else {
			mRbAplication.setChecked(true);
		}
	}

	private void CompruebaRespuesta() {
		if (mRbPressed.equals(mAnswers[mIndex])){
			showAnimationAnswer(true);
			if (mIsPlaying){
				gameModeControl();
			}
			random();			
			mQuestion.setText(mQuestions[mIndex]);
		}
		else showAnimationAnswer(false);		
	}

	//Metodo para generar un número aleatorio
	public int random(){
		Random ran = new Random();
		mIndex = ran.nextInt(11);
		return mIndex;
	}

	private void increasePoints(int val) {
		this.mPoints = this.mPoints + val;
		updatePointsTextView(this.mPoints);
	}

	private void updatePointsTextView(int p) {		
		mLayerPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
	}

	private void gameModeControl() {
		increasePoints(POINTS_FOR_QUESTION);

		if (mCurrentQuestionCounter >= MAX_QUESTIONS) {
			// won
			this.mWon = true;
			this.endGame();

		}

		if (mCurrentQuestionCounter < MAX_QUESTIONS && getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			this.mWon = false;
			this.endGame();
		}
		mCurrentQuestionCounter++;
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
		mSolution.setVisibility(View.INVISIBLE);
		mLayerPoints.setVisibility(View.VISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	private void updateToTrainMode() {
		mSolution.setVisibility(View.VISIBLE);
		mLayerPoints.setVisibility(View.GONE);
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

	@Override
	void endGame() {
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
		this.mPoints = (int) (this.mPoints + remainingTimeInSeconds);

		if (this.mWon){
			String username = getResources().getString(R.string.default_user_name);
			try {
				HighscoreManager.addScore(getActivity().getApplicationContext(),
						this.mPoints, R.string.network_layer, new Date(), username);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		dialogGameOver();
		super.endGame();
		updateToTrainMode();
		this.mPoints = 0;
		this.mCurrentQuestionCounter = 0;
		this.mWon = false;
	}

}
