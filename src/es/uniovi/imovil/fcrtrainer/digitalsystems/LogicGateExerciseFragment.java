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

package es.uniovi.imovil.fcrtrainer.digitalsystems;

import java.util.Random;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class LogicGateExerciseFragment extends BaseExerciseFragment implements
		OnClickListener, OnItemSelectedListener {
	private static final String STATE_CURRENT_QUESTION = "mCurrentQuestion";

	private int mCurrentQuestion;

	private Button mButtoncheck;
	private String[] mLogicstring;
	private TypedArray mImageArray;
	private ImageView mImageView;
	private Button mSolutionButton;
	private Spinner mSpinner;
	private Random mRandom = new Random();

	public static LogicGateExerciseFragment newInstance() {

		LogicGateExerciseFragment fragment = new LogicGateExerciseFragment();
		return fragment;
	}

	public LogicGateExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflamos el Layout
		View rootView = inflater.inflate(R.layout.fragment_logic_gate,
				container, false);

		// Cargamos el array con las puertas logicas
		mLogicstring = getResources().getStringArray(R.array.logic_gates);

		// Inicializamos las vistas de los botones y sus respectivos Listener
		mButtoncheck = (Button) rootView.findViewById(R.id.cButton);
		mSolutionButton = (Button) rootView.findViewById(R.id.sButton);
		mButtoncheck.setOnClickListener(this);
		mSolutionButton.setOnClickListener(this);

		// Cargamos un array con las imagenes de las puertas logicas
		mImageArray = getResources().obtainTypedArray(
				R.array.logic_gates_images);

		mImageView = (ImageView) rootView.findViewById(R.id.imagelogicgate);

		if (savedInstanceState == null) {
			// Inicializamos la variable contador con el fin de recorrer el array
			// con las diferentes puertas lógicas
			mCurrentQuestion = random();

			mImageView.setImageResource(mImageArray.getResourceId(
					mCurrentQuestion, 0));
		}

		// Inicializamos el spinner y le cargamos los elementos
		mSpinner = (Spinner) rootView.findViewById(R.id.spinner_logic_gate);

		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this.getActivity(), R.array.logic_gates,
				android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(this);

		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null) {
			return;
		}

		if (mIsPlaying) {
			mSolutionButton.setVisibility(View.GONE);
		}

		mCurrentQuestion = savedInstanceState.getInt(STATE_CURRENT_QUESTION, 0);
		
		mImageView.setImageResource(mImageArray.getResourceId(
				mCurrentQuestion, 0));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_CURRENT_QUESTION, mCurrentQuestion);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cButton:
			checkAnswer();
			break;

		case R.id.sButton:
			showSolution();
			break;
		}

	}

	public void checkAnswer() {
		String answer = mSpinner.getSelectedItem().toString();
		if (mLogicstring[mCurrentQuestion].equals(answer)) {
			showAnimationAnswer(true);
			if (mIsPlaying) {
				computeCorrectQuestion();
			}
			mCurrentQuestion = random();
			mImageView.setImageResource(mImageArray.getResourceId(
					mCurrentQuestion, 0));
		} else {
			showAnimationAnswer(false);
			if (mIsPlaying) {
				computeIncorrectQuestion();
			}
		}
	}

	// Metodo para seleccionar en el spinner la respuesta.
	public void showSolution() {
		mSpinner.setSelection(mCurrentQuestion);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// Nada que hacer aquí
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Nada que hacer aquí
	}

	// Metodo para generar un número aleatorio
	private int random() {
		return mRandom.nextInt(numberOfGates());
	}

	private int numberOfGates() {
		return mLogicstring.length;
	}
	
	@Override
	protected void startGame() {
		super.startGame();

		// Cambiamos el layout y se adapta al modo juego
		mSolutionButton.setVisibility(View.GONE);
		mCurrentQuestion = random();
		mImageView.setImageResource(mImageArray.getResourceId(mCurrentQuestion,
				0));
	}

	@Override
	protected void cancelGame() {
		super.cancelGame();

		// Cambiamos el layout y lo dejamos otra vez como el modo ejercicio
		mSolutionButton.setVisibility(View.VISIBLE);
	}

	protected void endGame() {
		super.endGame();

		// Cambiamos el layout para dejarlo en modo ejercicio
		mSolutionButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected int obtainExerciseId() {
		return R.string.logic_gate;
	}

}
