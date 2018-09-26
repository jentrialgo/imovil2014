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

import java.util.Random;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NetworkLayerExerciseFragment extends BaseExerciseFragment {
	private static final String STATE_CURRENT_QUESTION = "mCurrentQuestion";

	private static final long GAME_DURATION_MS = 30 * 1000; // half minute

	private View mRootView;		
	private TextView mQuestion;
	private RadioGroup mOptions;

	private RadioButton mRbLink;
	private RadioButton mRbNetwork;
	private RadioButton mRbTransport;
	private RadioButton mRbAplication;

	private Button mCheck;
	private Button mSolution;
	private String[] mQuestions;
	private String[] mAnswers;
	private String mRbPressed = "";
	private int mCurrentQuestion = 0;

	private Random mRandom = new Random();

	//constructores
	public NetworkLayerExerciseFragment() {
		setGameDuration(GAME_DURATION_MS);
	}

	//metodos
	public static NetworkLayerExerciseFragment newInstance() {
		return new NetworkLayerExerciseFragment();
	}

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mRootView =inflater.inflate(R.layout.fragment_layer, container, false);
		//TextView para mostrar la pregunta
		mQuestion = mRootView.findViewById(R.id.textlayer);

		//Radiogrup
		mOptions = mRootView.findViewById(R.id.layer_group);
		mRbLink = mRootView.findViewById(R.id.link_layer);
		mRbNetwork = mRootView.findViewById(R.id.internet_layer);
		mRbTransport = mRootView.findViewById(R.id.transport_layer);
		mRbAplication = mRootView.findViewById(R.id.application_layer);

		mOptions.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
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
		mCheck = mRootView.findViewById(R.id.button_layer);
		mSolution = mRootView.findViewById(R.id.button_solutionlayer);

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
					showSolution();
				}
			}
		});

		//Arrays
		mQuestions = getResources().getStringArray(R.array.layer_exercise_questions);
		mAnswers = getResources().getStringArray(R.array.layer_exercise_answers);
		
		if (savedInstanceState == null) {
			newRandomQuestion();
			updateQuestionText();
		}

		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			return;
		}

		if (mIsPlaying) {
			mSolution.setVisibility(View.GONE);
		}
		
		mCurrentQuestion = savedInstanceState.getInt(STATE_CURRENT_QUESTION);
		updateQuestionText();
	}

	private void updateQuestionText() {
		mQuestion.setText(getString(R.string.protocol) + " "
				+ mQuestions[mCurrentQuestion]);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_CURRENT_QUESTION, mCurrentQuestion);
	}

	protected void showSolution() {
		String answer = mAnswers[mCurrentQuestion];
		if (answer.equals(getString(R.string.link_layer))){
			mRbLink.setChecked(true);
		}
		else if (answer.equals(getString(R.string.internet_layer))){
			mRbNetwork.setChecked(true);
		}
		else if (answer.equals(getString(R.string.transport_layer))){
			mRbTransport.setChecked(true);
		}
		else {
			mRbAplication.setChecked(true);
		}
	}

	private void CompruebaRespuesta() {
		if (mRbPressed.equals(mAnswers[mCurrentQuestion])){
			showAnimationAnswer(true);
			if (mIsPlaying){
				computeCorrectQuestionAndUpdateScore();
			}
			newRandomQuestion();			
			updateQuestionText();
			mOptions.clearCheck();
		}
		else {
			showAnimationAnswer(false);
			computeIncorrectQuestionAndUpdateScore();
		}
	}

	//Metodo para generar un número aleatorio
	public void newRandomQuestion() {
		mCurrentQuestion = mRandom.nextInt(11);
	}

	@Override
	public void startGame() {
		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		mSolution.setVisibility(View.GONE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	private void updateToTrainMode() {
		mSolution.setVisibility(View.VISIBLE);
	}

	@Override
	protected void endGame() {
		super.endGame();
		updateToTrainMode();
	}

    @Override
    protected Screen associatedExercise() {
        return Screen.NETWORK_LAYER;
    }

}
