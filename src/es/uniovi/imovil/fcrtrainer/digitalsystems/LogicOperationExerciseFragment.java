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

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.os.Bundle;
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

	private static final int BASE_BINARIA = 2;
	private static final int MAX_NUMBER_OF_OPERATIONS = 3;

	private static final int MAX_NUMBER_LO_QUESTIONS = 5;

	private View mRootView;
	private TextView mTvEntrada1;
	private TextView mTvEntrada2;
	private TextView mTvOperacion;
	private EditText mEtRespuesta;
	private Button mButtonCheck;
	private Button mButtonSolucion;

	private Random rnd;
	private String mSolucion;
	
	// Juego
	private long mDurationMs = 60 * 1000; // 1 min
	private Boolean mModoJuego = false;
	int mNumeroAciertos = 0;
	int mNumeroPregunta = 0;
	private Boolean mFinJuego = false;

	public static LogicOperationExerciseFragment newInstance() {
		LogicOperationExerciseFragment fragment = new LogicOperationExerciseFragment();
		return fragment;
	}

	public LogicOperationExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragmen_logic_operation,
				container, false);

		mTvEntrada1 = (TextView) mRootView.findViewById(R.id.LOentrada1);
		mTvEntrada2 = (TextView) mRootView.findViewById(R.id.LOentrada2);
		mTvOperacion = (TextView) mRootView.findViewById(R.id.LOoperacion);

		mEtRespuesta = (EditText) mRootView.findViewById(R.id.LOrespuesta);

		inicializarButtons();

		rnd = new Random();

		if (savedInstanceState != null) {
			cargaDatos(savedInstanceState);
		} else {
			crearPregunta();
			inicializarEditText();
		}

		return mRootView;
	}

	private void cargaDatos(Bundle savedInstanceState) {
		String entrada1 = savedInstanceState.getString("LOentrada1");
		mTvEntrada1.setText(entrada1);

		String entrada2 = savedInstanceState.getString("LOentrada2");
		mTvEntrada2.setText(entrada2);

		String operacion = savedInstanceState.getString("LOoperacion");
		mTvOperacion.setText(operacion);

		String respuesta = savedInstanceState.getString("LOrespuesta");
		mEtRespuesta.setText(respuesta);

		mSolucion = savedInstanceState.getString("Solucion");
		
		mEtRespuesta.setInputType(EditorInfo.TYPE_CLASS_TEXT);
	}

	private void inicializarEditText() {
		mEtRespuesta.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					isCorrect(mEtRespuesta.getEditableText().toString().trim()
							.toLowerCase(Locale.US));
				}
				return false;
			}
		});
	}

	private void inicializarButtons() {
		mButtonCheck = (Button) mRootView.findViewById(R.id.LObCalcular);
		mButtonCheck.setOnClickListener(this);

		mButtonSolucion = (Button) mRootView.findViewById(R.id.LObSolucion);
		mButtonSolucion.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String respuesta = mEtRespuesta.getText().toString();

		if (v.getId() == mButtonCheck.getId()) {
			if (mModoJuego) {
				clickJuego(respuesta);
			} else {
				isCorrect(mEtRespuesta.getEditableText().toString().trim());
			}
		}

		if (v.getId() == mButtonSolucion.getId()) {
			if (!mFinJuego) {
				showSolution();
			} else {
				clickFin();
			}
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
		String e1 = BinarioAleatorio();
		mTvEntrada1.setText(e1);

		String e2 = BinarioAleatorio();
		mTvEntrada2.setText(e2);

		String op = OperacionAleatoria();
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

	private String BinarioAleatorio() {
		int maxNumber = (int) Math.pow(BASE_BINARIA, level().numberOfBits());
		int entero = rnd.nextInt(maxNumber);
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

	private String OperacionAleatoria() {
		int entero = rnd.nextInt(MAX_NUMBER_OF_OPERATIONS);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// guardamos los campos de los textViews
		String entrada1 = mTvEntrada1.getText().toString();
		String entrada2 = mTvEntrada2.getText().toString();
		String operacion = mTvOperacion.getText().toString();
		String respuesta = mEtRespuesta.getText().toString();

		// lo guardamos en el Bundle
		outState.putString("LOentrada1", entrada1);
		outState.putString("LOentrada2", entrada2);
		outState.putString("LOoperacion", operacion);
		outState.putString("LOrespuesta", respuesta);
		outState.putString("Solucion", mSolucion);
	}

	// Inicia el modo entrenamiento
	private void vistaModoEntrenamiento() {
		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.LOpregunta).toString());

		mTvEntrada2.setVisibility(View.VISIBLE);
		mTvOperacion.setVisibility(View.VISIBLE);
		mEtRespuesta.setVisibility(View.VISIBLE);
		mButtonCheck.setVisibility(View.VISIBLE);
		mButtonSolucion.setVisibility(View.VISIBLE);

		crearPregunta();

		mButtonSolucion.setText(getResources().getText(R.string.solution));
		mEtRespuesta.requestFocus();

		mModoJuego = false;
		mFinJuego = false;
	}

	// ************************ MODO JUEGO *******************************
	protected void startGame() {
		super.startGame();
		// Establecer duracion del juego
		super.setGameDuration(mDurationMs);

		vistaInicioJuego();

		// Inicializar el numero de preguntas y de aciertos
		mNumeroPregunta = 0;
		mNumeroAciertos = 0;

		mEtRespuesta.setText("");
		mEtRespuesta.requestFocus();
	}

	protected void endGame() {
		super.endGame();
		int score = calculaPuntuacion();
		vistaFinJuego();
		enviarPuntuacion(score);
		mModoJuego = false;
	}

	protected void cancelGame() {
		super.cancelGame();
		vistaModoEntrenamiento();
	}

	private int calculaPuntuacion() {
		int aciertos = mNumeroAciertos;
		long miliseg = getRemainingTimeMs();
		int segundos = (int) (miliseg / 1000);
		int score = aciertos + segundos;
		return score;
	}

	private void enviarPuntuacion(int score) {
		try {
			HighscoreManager.addScore(getActivity(), score,
					R.string.logic_operation, new Date(), null, level());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void clickJuego(String answer) {
		if (answer.equals(mSolucion)) {
			showAnimationAnswer(true);
			mNumeroPregunta++;
			mNumeroAciertos = mNumeroAciertos + 10;
			crearPregunta();
		} else {
			showAnimationAnswer(false);
		}

		if (mNumeroPregunta == MAX_NUMBER_LO_QUESTIONS) {
			mButtonSolucion.setVisibility(View.VISIBLE);
			endGame();
		}
	}

	// Vuelve al modo entrenamiento al finalizar el juego
	private void clickFin() {
		vistaModoEntrenamiento();
	}

	private void vistaInicioJuego() {
		mButtonSolucion.setVisibility(View.GONE);

		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.LOpregunta).toString());

		mTvEntrada2.setVisibility(View.VISIBLE);
		mTvOperacion.setVisibility(View.VISIBLE);
		mEtRespuesta.setVisibility(View.VISIBLE);
		mButtonCheck.setVisibility(View.VISIBLE);

		crearPregunta();

		mModoJuego = true;
	}

	private void vistaFinJuego() {
		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.finJuego).toString());

		int score = calculaPuntuacion();

		mTvEntrada1.setText(getResources().getText(R.string.puntuacion)
				.toString() + " " + score);
		mTvEntrada2.setVisibility(View.GONE);
		mTvOperacion.setVisibility(View.GONE);
		mEtRespuesta.setVisibility(View.GONE);
		mButtonCheck.setVisibility(View.GONE);
		mButtonSolucion.setVisibility(View.VISIBLE);
		mButtonSolucion.setText(getResources().getText(
				R.string.modoEntrenamiento));

		mFinJuego = true;
	}
}
