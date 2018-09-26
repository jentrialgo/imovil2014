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
import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.R;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LogicOperationExerciseFragment extends BaseExerciseFragment
		implements OnClickListener {

	private static final String TAG_INPUT1 = "LOentrada1";
	private static final String TAG_INPUT2 = "LOentrada2";
	private static final String TAG_OPERATION = "LOoperacion";
	private static final String TAG_ANSWER = "LOrespuesta";
	private static final String TAG_SOLUTION = "Solucion";
	private static final String TAG_NUMERO_PREGUNTA = "mNumeroPregunta";

	private static final int BASE_BINARIA = 2;
	private static final int MAX_NUMBER_OF_OPERATIONS = 3;

	private View mRootView;
	private TextView mTvEntrada1;
	private TextView mTvEntrada2;
	private TextView mTvOperacion;
	private EditText mEtRespuesta;
	private Button mButtonCheck;
	private Button mButtonSolucion;

	private Random mRandom;
	private String mSolucion;

	// Juego
	int mNumeroPregunta = 0;

	public static LogicOperationExerciseFragment newInstance() {
        return new LogicOperationExerciseFragment();
	}

	public LogicOperationExerciseFragment() {
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_logic_operation,
				container, false);

		mTvEntrada1 = mRootView.findViewById(R.id.LOentrada1);
		mTvEntrada2 = mRootView.findViewById(R.id.LOentrada2);
		mTvOperacion = mRootView.findViewById(R.id.LOoperacion);

		mEtRespuesta = mRootView.findViewById(R.id.text_view_answer);

		inicializarButtons();

		mRandom = new Random();

		if (savedInstanceState != null) {
			cargaDatos(savedInstanceState);
		} else {
			crearPregunta();
			inicializarEditText();
		}

		return mRootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mIsPlaying) {
			mButtonSolucion.setVisibility(View.GONE);
		}
	}

	private void cargaDatos(Bundle savedInstanceState) {
		String entrada1 = savedInstanceState.getString(TAG_INPUT1);
		mTvEntrada1.setText(entrada1);

		String entrada2 = savedInstanceState.getString(TAG_INPUT2);
		mTvEntrada2.setText(entrada2);

		String operacion = savedInstanceState.getString(TAG_OPERATION);
		mTvOperacion.setText(operacion);

		String respuesta = savedInstanceState.getString(TAG_ANSWER);
		mEtRespuesta.setText(respuesta);

		mSolucion = savedInstanceState.getString(TAG_SOLUTION);
		
		mNumeroPregunta = savedInstanceState.getInt(TAG_NUMERO_PREGUNTA);
	}

	private void inicializarEditText() {
		mEtRespuesta.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					checkSolution();
				}
				return false;
			}
		});
	}

	private void inicializarButtons() {
		mButtonCheck = mRootView.findViewById(R.id.LObCalcular);
		mButtonCheck.setOnClickListener(this);

		mButtonSolucion = mRootView.findViewById(R.id.LObSolucion);
		mButtonSolucion.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == mButtonCheck.getId()) {
			checkSolution();
		}

		if (v.getId() == mButtonSolucion.getId()) {
			showSolution();
		}
	}

	private void checkSolution() {
		String respuesta = mEtRespuesta.getText().toString();
		if (mIsPlaying) {
			clickJuego(respuesta);
		} else {
			isCorrect(mEtRespuesta.getEditableText().toString().trim());
		}
	}

	// Determina si la respuesta es correcta
	public void isCorrect(String answer) {
		if (answer.equals(mSolucion)) {
			showAnimationAnswer(true);
			// si se acert� la respuesta, crear otra pregunta
			crearPregunta();
		} else {
			showAnimationAnswer(false);
		}
	}

	private void crearPregunta() {
		String e1 = binarioAleatorio();
		mTvEntrada1.setText(e1);

		String e2 = binarioAleatorio();
		mTvEntrada2.setText(e2);

		String op = operacionAleatoria();
		mTvOperacion.setText(op);

		int entrada1 = Integer.parseInt(e1, BASE_BINARIA);
		int entrada2 = Integer.parseInt(e2, BASE_BINARIA);
		int result = 0;
		if (op.equals("AND"))
			result = entrada1 & entrada2;
		else if (op.equals("OR"))
			result = entrada1 | entrada2;
		else if (op.equals("XOR"))
			result = entrada1 ^ entrada2;

		mSolucion = Integer.toBinaryString(result);
		mSolucion = completaNumeroBits(mSolucion);
		
		mEtRespuesta.setText("");
	}

	private String binarioAleatorio() {
		int maxNumber = (int) Math.pow(BASE_BINARIA, level().numberOfBits());
		int entero = mRandom.nextInt(maxNumber);
		String binario = Integer.toBinaryString(entero);

		binario = completaNumeroBits(binario);
		return binario;
	}

	private String completaNumeroBits(String binario) {
		int i = binario.length();
		while (i < level().numberOfBits()) {
			binario = "0" + binario;
			i = binario.length();
		}
		return binario;
	}

	private String operacionAleatoria() {
		int entero = mRandom.nextInt(MAX_NUMBER_OF_OPERATIONS);
		String operacion;

		switch (entero) {
		case 0:
			operacion = "AND";
			return operacion;
		case 1:
			operacion = "OR";
			return operacion;
		default:
			operacion = "XOR";
			return operacion;
		}
	}

	private void showSolution() {
		mEtRespuesta.setText(mSolucion);
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// guardamos los campos de los textViews
		String entrada1 = mTvEntrada1.getText().toString();
		String entrada2 = mTvEntrada2.getText().toString();
		String operacion = mTvOperacion.getText().toString();
		String respuesta = mEtRespuesta.getText().toString();

		// lo guardamos en el Bundle
		outState.putString(TAG_INPUT1, entrada1);
		outState.putString(TAG_INPUT2, entrada2);
		outState.putString(TAG_OPERATION, operacion);
		outState.putString(TAG_ANSWER, respuesta);
		outState.putString(TAG_SOLUTION, mSolucion);
		
		outState.putInt(TAG_NUMERO_PREGUNTA, mNumeroPregunta);
	}

	// Inicia el modo entrenamiento
	private void vistaModoEntrenamiento() {
		mButtonSolucion.setVisibility(View.VISIBLE);
		crearPregunta();
		mEtRespuesta.requestFocus();
		mIsPlaying = false;
	}

	// ************************ MODO JUEGO *******************************
	protected void startGame() {
		vistaInicioJuego();

		// Inicializar el numero de preguntas y de aciertos
		mNumeroPregunta = 0;

		mEtRespuesta.setText("");
		mEtRespuesta.requestFocus();

		super.startGame();
	}

	protected void endGame() {
		super.endGame();
		vistaModoEntrenamiento();
	}

	protected void cancelGame() {
		super.cancelGame();
		vistaModoEntrenamiento();
	}

	private void clickJuego(String answer) {
		if (answer.equals(mSolucion)) {
			showAnimationAnswer(true);
			mNumeroPregunta++;
			computeCorrectQuestionAndUpdateScore();
			
			crearPregunta();
		} else {
			showAnimationAnswer(false);
			computeIncorrectQuestionAndUpdateScore();
		}

	}

	private void vistaInicioJuego() {
		mButtonSolucion.setVisibility(View.GONE);
		crearPregunta();
		mIsPlaying = true;
	}

    @Override
    protected Screen associatedExercise() {
        return Screen.LOGIC_OPERATION;
    }

	@Override
	protected int pointsPerCorrectQuestion() {
		return level().scoreMultiplier() * super.pointsPerCorrectQuestion();
	}

	@Override
	protected int penalizationPerIncorrectQuestion() {
		return level().scoreMultiplier() * super.penalizationPerIncorrectQuestion();
	}

}
