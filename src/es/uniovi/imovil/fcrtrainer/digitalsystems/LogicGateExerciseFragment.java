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

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
	private static final int RANDOM = 6;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final long GAME_DURATION_MS = 1 * 1000 * 60;// 1 min

	private Button mButtoncheck;
	private String[] mLogicstring;
	private View mRootView;
	private int mCurrentQuestion;
	private TypedArray mImageArray;
	private ImageView mImageView;
	private Button mSolutionButton;
	private Spinner mSpinner;
	private int mN;
	private int mGameEnd = 0;
	private int mInitialValue = 0;
	private int mFinalValue = 5;
	private ArrayList<Integer> mNumberList = new ArrayList<Integer>();
	private int mPoints;
	private Random mRandom;

	public static LogicGateExerciseFragment newInstance() {

		LogicGateExerciseFragment fragment = new LogicGateExerciseFragment();
		return fragment;
	}

	public LogicGateExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRandom = new Random();
		
		// Inicializamos la variable contador con el fin de recorrer el array
		// con las diferentes puertas lógicas
		mCurrentQuestion = random();

		// Inflamos el Layout
		mRootView = inflater.inflate(R.layout.fragment_logic_gate, container,
				false);

		// Cargamos el array con las puertas logicas
		mLogicstring = getResources().getStringArray(R.array.logic_gates);

		// Inicializamos las vistas de los botones y sus respectivos Listener
		mButtoncheck = (Button) mRootView.findViewById(R.id.cButton);
		mSolutionButton = (Button) mRootView.findViewById(R.id.sButton);
		mButtoncheck.setOnClickListener(this);
		mSolutionButton.setOnClickListener(this);

		// Cargamos un array con las imagenes de las puertas logicas
		mImageArray = getResources().obtainTypedArray(
				R.array.logic_gates_images);

		// Inicializamos las vistas de las imagenes
		mImageView = (ImageView) mRootView.findViewById(R.id.imagelogicgate);
		mImageView.setImageResource(mImageArray.getResourceId(mCurrentQuestion,
				0));

		// Inicializamos el spinner y le cargamos los elementos
		mSpinner = (Spinner) mRootView.findViewById(R.id.spinner_logic_gate);

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

		return mRootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cButton:
			checkAnswer();
			break;

		case R.id.sButton:
			// Mostramos la solución
			solutionLogicGate();
			break;
		}

	}

	public void checkAnswer() {
		if (mIsPlaying) {
			if (mGameEnd < mLogicstring.length - 1) {
				compruebaModo(mCurrentQuestion);
			} else {
				endGame();
			}
		} else {
			compruebaModo(mCurrentQuestion);
		}
	}

	// Metodo para seleccionar en el spinner la respuesta.
	public void solutionLogicGate() {
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
		mN = mRandom.nextInt(RANDOM);

		return mN;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_change_game_mode:
			if (mIsPlaying) {
				cancelGame();
			} else {
				startGame();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void startGame() {
		super.startGame();

		// Fijamos el contador en 1 minuto
		setGameDuration(GAME_DURATION_MS);

		// Cambiamos el layout y se adapta al modo juego
		mSolutionButton.setVisibility(View.GONE);
		mButtoncheck.setText("Ok");
		mPoints = 0;
		mCurrentQuestion = generar();
		mImageView.setImageResource(mImageArray.getResourceId(mCurrentQuestion,
				0));
	}

	@Override
	protected void cancelGame() {
		super.cancelGame();

		// Cambiamos el layout y lo dejamos otra vez como el modo ejercicio
		mSolutionButton.setVisibility(View.VISIBLE);
		mButtoncheck.setText("Comprobar");
	}

	protected void endGame() {
		// convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000;

		// every remaining second gives one extra point.
		mPoints = (int) (mPoints + remainingTimeInSeconds);

		// Guardamos los puntos
		savePoints();

		// Vaciamos la lista que contiene todos los resultados posibles del
		// metodo generar()
		mNumberList.clear();

		// Cambiamos el layout para dejarlo en modo ejercicio
		mSolutionButton.setVisibility(View.VISIBLE);
		mButtoncheck.setText("Comprobar");
		mGameEnd = 0;

		super.endGame();
	}

	@Override
	protected int finalScore() {
		return mPoints;
	}

	@Override
	protected String gameOverMessage() {
		int remainingTime = (int) getRemainingTimeMs() / 1000;
		if (remainingTime > 0) {
			return String.format(
					getString(R.string.gameisoverexp), remainingTime, mPoints);
		} else {
			return String.format(
					getString(R.string.lost_time_over), mPoints);
		}
	}

	// Método para a�adir los puntos a la tabla de highscore
	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.mPoints, R.string.logic_gate, new Date(), username,
					level());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Metodo para generar numeros aleatorios sin repetir numeros.
	public int generar() {
		if (mNumberList.size() < (mFinalValue - mInitialValue) + 1) {
			// Aun no se han generado todos los numeros
			int numero = numeroAleatorio();// genero un numero
			if (mNumberList.isEmpty()) {// si la lista esta vacia
				mNumberList.add(numero);
				return numero;
			} else {// si no esta vacia
				if (mNumberList.contains(numero)) {// Si el numero que gener�
													// esta contenido en la
													// lista
					return generar();// recursivamente lo mando a generar otra
										// vez
				} else {// Si no esta contenido en la lista
					mNumberList.add(numero);
					return numero;
				}
			}
		} else {// ya se generaron todos los numeros
			return -1;
		}
	}

	// Genera un numero aleatorio
	private int numeroAleatorio() {
		return (int) (Math.random() * (mFinalValue - mInitialValue + 1) + mInitialValue);
	}

	// Metodo para comprobar el modo en el que se ecuentra (juego o ejercicio) y
	// segun el modo
	// Si estas en modo juego genera solo 6 numeros aleatorios sin repetir y si
	// no, genera numeros aleatorios infinitos
	public void compruebaModo(int pregunta) {
		String textosUpper = mSpinner.getSelectedItem().toString();
		if (mLogicstring[pregunta].equals(textosUpper)) {
			mGameEnd++;
			showAnimationAnswer(true);
			// Ponemos el texto en verde y ponemos la imagen de un tic verde.
			if (mIsPlaying) {
				pregunta = generar();
				mCurrentQuestion = pregunta;
				mPoints += POINTS_FOR_QUESTION;
				updateScore(mPoints);
			} else {
				pregunta = random();
				mCurrentQuestion = pregunta;
			}
			mImageView.setImageResource(mImageArray.getResourceId(pregunta, 0));
		} else {
			// Si no es igual es texto del string con el del editText
			showAnimationAnswer(false);
		}
	}

}
