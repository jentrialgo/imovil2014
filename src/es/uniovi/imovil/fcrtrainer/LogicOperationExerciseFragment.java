package es.uniovi.imovil.fcrtrainer;

import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

	private View mRootView;
	private TextView tvEntrada1;
	private TextView tvEntrada2;
	private TextView tvOperacion;
	private EditText etRespuesta;
	private Button bCheck;
	private Button bSolucion;
	private static final int MAX_NUMBER_OF_BITS = 5;
	private static final int BASE_BINARIA = 2;
	private static final int MAX_INT_NUMBER_TO_BINARY = 32;
	private static final int MAX_NUMBER_OF_OPERATIONS = 3;

	// Juego
	private long mDurationMs = 60 * 1000; // 1 min
	private static final int MAX_NUMBER_LO_QUESTIONS = 5;
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

		tvEntrada1 = (TextView) mRootView.findViewById(R.id.LOentrada1);
		tvEntrada2 = (TextView) mRootView.findViewById(R.id.LOentrada2);
		tvOperacion = (TextView) mRootView.findViewById(R.id.LOoperacion);

		etRespuesta = (EditText) mRootView.findViewById(R.id.LOrespuesta);

		if (savedInstanceState != null) {
			cargaDatos(savedInstanceState);
		} else {
			inicializarTexViews();
			inicializarEditText();
			inicializarButtons();
		}

		return mRootView;
	}

	private void cargaDatos(Bundle savedInstanceState) {
		String entrada1 = savedInstanceState.getString("LOentrada1");
		tvEntrada1.setText(entrada1);

		String entrada2 = savedInstanceState.getString("LOentrada2");
		tvEntrada2.setText(entrada2);

		String operacion = savedInstanceState.getString("LOoperacion");
		tvOperacion.setText(operacion);

		String respuesta = savedInstanceState.getString("LOrespuesta");
		etRespuesta.setText(respuesta);
	}

	private void inicializarTexViews() {
		String binario;

		binario = BinarioAleatorio();
		tvEntrada1.setText(binario);

		binario = BinarioAleatorio();
		tvEntrada2.setText(binario);

		String operacion = OperacionAleatoria();
		tvOperacion.setText(operacion);
	}

	private void inicializarEditText() {
		etRespuesta.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					isCorrect(etRespuesta.getEditableText().toString().trim()
							.toLowerCase(Locale.US));
				}
				return false;
			}
		});
	}

	private void inicializarButtons() {
		bCheck = (Button) mRootView.findViewById(R.id.LObCalcular);
		bCheck.setOnClickListener(this);

		bSolucion = (Button) mRootView.findViewById(R.id.LObSolucion);
		bSolucion.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String respuesta = etRespuesta.getText().toString();
		String entrada1 = tvEntrada1.getText().toString();
		String entrada2 = tvEntrada2.getText().toString();
		String operacion = tvOperacion.getText().toString();

		if (v.getId() == bCheck.getId()) {
			isCorrect(etRespuesta.getEditableText().toString().trim());
			if (mModoJuego) {
				clickJuego(respuesta, entrada1, operacion, entrada2);
			}
		}

		if (v.getId() == bSolucion.getId()) {
			if (!mFinJuego) {
				showSolution();
			} else {
				clickFin();
			}
		}
	}

	// Calcula si la respuesta es correcta
	public void isCorrect(String answer) {
		String solucion = LOCalcularResultado(tvEntrada1.getText().toString(),
				tvOperacion.getText().toString(), tvEntrada2.getText()
						.toString());

		if (answer.equals(solucion)) {
			showAnimationAnswer(true);
			// si se acertó la respuesta, crear otra pregunta
			crearPregunta();
		} else {
			showAnimationAnswer(false);
		}
	}

	// Creación de preguntas aleatorias
	private void crearPregunta() {
		String binario;
		String op;

		binario = BinarioAleatorio();
		tvEntrada1.setText(binario);

		binario = BinarioAleatorio();
		tvEntrada2.setText(binario);

		op = OperacionAleatoria();
		tvOperacion.setText(op);

		etRespuesta.setText("");

	}

	// Calcula el resultado correcto de la pregunta formulada
	public String LOCalcularResultado(String e1, String operacion, String e2) {
		int entrada1 = Integer.parseInt(e1, BASE_BINARIA);
		int entrada2 = Integer.parseInt(e2, BASE_BINARIA);
		int result = 0;
		String solucion;

		if (operacion.equals("AND"))
			result = entrada1 & entrada2;
		else if (operacion.equals("OR"))
			result = entrada1 | entrada2;
		else if (operacion.equals("XOR"))
			result = entrada1 ^ entrada2;

		solucion = Integer.toBinaryString(result);

		// Rellenar con los 0 que falten por delante
		int i = solucion.length();
		while (i < MAX_NUMBER_OF_BITS) {
			solucion = "0" + solucion;
			i = solucion.length();
		}

		return solucion;
	}

	// Generación de un binario aleatorio
	private String BinarioAleatorio() {
		Random rnd = new Random();
		int entero = rnd.nextInt(MAX_INT_NUMBER_TO_BINARY);
		String binario = Integer.toBinaryString(entero);

		// LLenamos la cadena de 0 hasta tener 5 bits
		int i = binario.length();
		while (i < MAX_NUMBER_OF_BITS) {
			binario = "0" + binario;
			i = binario.length();
		}
		return binario;
	}

	// Generación de una operación aleatoria
	private String OperacionAleatoria() {
		Random rnd = new Random();
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
		String solucion = LOCalcularResultado(tvEntrada1.getText().toString(),
				tvOperacion.getText().toString(), tvEntrada2.getText()
						.toString());
		etRespuesta.setText(solucion);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// guardamos en la variable t el texto del campo EditText
		String entrada1 = tvEntrada1.getText().toString();
		String entrada2 = tvEntrada2.getText().toString();
		String operacion = tvOperacion.getText().toString();
		String respuesta = etRespuesta.getText().toString();
		// lo "guardamos" en el Bundle
		outState.putString("LOentrada1", entrada1);
		outState.putString("LOentrada2", entrada2);
		outState.putString("LOoperacion", operacion);
		outState.putString("LOrespuesta", respuesta);
	}

	// Inicia el modo entrenamiento
	private void modoEntrenamiento() {
		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.LOpregunta).toString());

		tvEntrada2.setVisibility(View.VISIBLE);
		tvOperacion.setVisibility(View.VISIBLE);
		etRespuesta.setVisibility(View.VISIBLE);
		bCheck.setVisibility(View.VISIBLE);
		bSolucion.setVisibility(View.VISIBLE);

		inicializarTexViews();

		bSolucion.setText(getResources().getText(R.string.solution));

		mModoJuego = false;
		mFinJuego = false;
	}

	// ************************ MODO JUEGO *******************************
	void startGame() {
		super.startGame();

		// Establecer duracion del juego
		super.setGameDuration(mDurationMs);

		// Establecer los textViews Visibles
		inicioJuego();

		// Inicializar el numero de preguntas y de aciertos
		mNumeroPregunta = 0;
		mNumeroAciertos = 0;
		etRespuesta.setText("");

		mModoJuego = true;
	}

	// Funcion que termina el modo juego al pulsar un boton
	private void clickFin() {
		modoEntrenamiento();
	}

	private void clickJuego(String answer, String entrada1, String operacion,
			String entrada2) {

		String solucion = LOCalcularResultado(entrada1, operacion, entrada2);
		if (answer.equals(solucion)) {
			showAnimationAnswer(true);
			crearPregunta();
			mNumeroPregunta++;
			mNumeroAciertos = mNumeroAciertos + 10;
		} else {
			showAnimationAnswer(false);
		}

		if (mNumeroPregunta == MAX_NUMBER_LO_QUESTIONS) {
			mModoJuego = false;
			bSolucion.setVisibility(View.VISIBLE);
			endGame();
		}
	}

	// Inicia los campos al inicio de un nuevo juego
	private void inicioJuego() {
		bSolucion.setVisibility(View.GONE);

		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.LOpregunta).toString());

		tvEntrada2.setVisibility(View.VISIBLE);
		tvOperacion.setVisibility(View.VISIBLE);
		etRespuesta.setVisibility(View.VISIBLE);
		bCheck.setVisibility(View.VISIBLE);

		inicializarTexViews();
	}

	void endGame() {
		super.endGame();

		int score = calculaPuntuacion();

		//Establecer las vistas de final del juego
		finJuego();

		// Envío de la puntuación
		HighscoreManager hM = new HighscoreManager();
		try {
			hM.addScore(getActivity(), score, R.string.logic_operation,
					new Date(), null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private int calculaPuntuacion() {
		int aciertos = mNumeroAciertos;
		int segundos = 1;
		int score = aciertos + segundos;
		return score;
	}

	void cancelGame() {
		super.cancelGame();
		modoEntrenamiento();
	}

	// Establece las vistas del fin del juego
	private void finJuego() {
		TextView pregunta = (TextView) mRootView.findViewById(R.id.LOpregunta);
		pregunta.setText(getResources().getText(R.string.finJuego).toString());
		
		int score = calculaPuntuacion();
		
		tvEntrada1.setText("Has obtenido " + score + " puntos.");
		tvEntrada2.setVisibility(View.GONE);
		tvOperacion.setVisibility(View.GONE);
		etRespuesta.setVisibility(View.GONE);
		bCheck.setVisibility(View.GONE);
		bSolucion.setVisibility(View.VISIBLE);
		bSolucion.setText(getResources().getText(R.string.modoEntrenamiento));

		mFinJuego = true;
	}

}
