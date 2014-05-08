package es.uniovi.imovil.fcrtrainer;


import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.TextView;

public class LogicGateExerciseFragment extends BaseExerciseFragment  implements OnClickListener, OnItemSelectedListener{
	private Button buttoncheck;
	private String [] logicstring;
	private View rootView;
	private int pregunta_actual;
	private TypedArray arrayimage;
	private ImageView imageview;
	private Button buttonsolution;
	private Spinner spinner; 
	private int n;
	private TextView mClock;
	private int fin_juego=0;
	public static final int RANDOM = 6;
	private int valorInicial=0;
	private int valorFinal=5;
	private ArrayList<Integer> listaNumero = new ArrayList<Integer>();
	private static final int POINTS_FOR_QUESTION = 10;
	private int puntos;
	private TextView puntuacion;
	private TextView title_puntuacion;
	private static final long GAME_DURATION_MS = 1 * 1000 * 60;//1 min
	public static LogicGateExerciseFragment newInstance() {

		LogicGateExerciseFragment fragment = new LogicGateExerciseFragment();
		return fragment;
	}

	public LogicGateExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inicializamos la variable contador con el fin de recorrer el array con las diferentes puertas 
		//logicas
		pregunta_actual=RANDOM();

		//Inflamos el Layout
		rootView = inflater.inflate(R.layout.fragment_logic_gate, container, false);


		//Cargamos el array con las puertas logicas
		logicstring= getResources().getStringArray(R.array.logic_gates);

		//Inicializamos las vistas de los botones y sus respectivos Listener
		buttoncheck=(Button) rootView.findViewById(R.id.cButton);
		buttonsolution=(Button)rootView.findViewById(R.id.sButton);
		buttoncheck.setOnClickListener(this);
		buttonsolution.setOnClickListener(this);

		//Cargamos un array con las imagenes de las puertas logicas
		arrayimage = getResources().obtainTypedArray(R.array.logic_gates_images);
		//Inicializamos las vistas de las imagenes
		imageview=(ImageView) rootView.findViewById(R.id.imagelogicgate);
		imageview.setImageResource(arrayimage.getResourceId(pregunta_actual, 0));
		//Inicializamos el spinner y le cargamos los  elementos
		spinner = (Spinner) rootView.findViewById(R.id.spinner_logic_gate);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.logic_gates, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		//Inicializamos views para el modo juego
		mClock=(TextView)rootView.findViewById(R.id.text_view_clock);
		puntuacion=(TextView)rootView.findViewById(R.id.puntuacion);
		title_puntuacion=(TextView)rootView.findViewById(R.id.title_puntuacion);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.cButton:
			//Metodo que comprueba la respuesta
			CompruebaRespuesta();	
			break;

		case R.id.sButton:
			//Mostramos la soluci—n
			solutionLogicGate();
			break;
		}

	}


	public void CompruebaRespuesta(){
		if(mIsPlaying){
			if(fin_juego<logicstring.length-1){
				compruebaModo(pregunta_actual);
			}
			else {

				endGame();
				dialogGameOver();
			}
		}

		else {
			compruebaModo(pregunta_actual);
		}
	}

	//Metodo para seleccionar en el spinner la respuesta.
	public void solutionLogicGate(){
		spinner.setSelection(pregunta_actual);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
	//Metodo para generar un nœmero aleatorio
	public int RANDOM(){
		Random ran = new Random();
		n = ran.nextInt(RANDOM);

		return n;
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
	void startGame() {
		// TODO Auto-generated method stub
		super.startGame();
		//Fijamos el contador en 1 minuto
		setGameDuration(GAME_DURATION_MS);
		//Cambiamos el layout y se adapta al modo juego
		buttonsolution.setVisibility(View.GONE);
		buttoncheck.setText("Ok");
		puntos=0;
		pregunta_actual=generar();
		imageview.setImageResource(arrayimage.getResourceId(pregunta_actual, 0));
		puntuacion.setVisibility(View.VISIBLE);
		title_puntuacion.setVisibility(View.VISIBLE);
		title_puntuacion.setText(R.string.title_puntuacion);
		puntuacion.setText("0");
	}
	@Override
	void cancelGame() {
		super.cancelGame();
		//Cambiamos el layout  y lo dejamos otra vez como el modo ejercicio
		puntuacion.setVisibility(View.GONE);
		title_puntuacion.setVisibility(View.GONE);
		buttonsolution.setVisibility(View.VISIBLE);
		buttoncheck.setText("Comprobar");
	}

	/**
	 * Esta funcion se llama al finalizar el juego, parando y ocultando el
	 * reloj. Las clases derivadas deben redifinirla, llamando al padre, para
	 * a–adir lo necesario a cada juego particular
	 */
	void endGame() {
		super.endGame();
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
		puntos = (int) (puntos + remainingTimeInSeconds);
		//Guardamos los puntos
		savePoints();
		//Vaciamos la lista que contiene todos los resultados posibles del metodo generar()
		listaNumero.clear();
		//Cambiamos el layout para dejarlo en modo ejercicio
		title_puntuacion.setVisibility(View.GONE);
		puntuacion.setVisibility(View.GONE);
		mClock.setVisibility(View.GONE);
		buttonsolution.setVisibility(View.VISIBLE);
		buttoncheck.setText("Comprobar");
		fin_juego=0;
		
	}

	//Metodo para a–adir los puntos a la tabla de highscore
	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {

			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.puntos, R.string.logic_gate, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	//Metodo para generar numeros aleatorios sin repetir numeros.
	public int generar(){
		if(listaNumero.size() < (valorFinal - valorInicial) +1){
			//Aun no se han generado todos los numeros
			int numero = numeroAleatorio();//genero un numero
			if(listaNumero.isEmpty()){//si la lista esta vacia
				listaNumero.add(numero);
				return numero;
			}else{//si no esta vacia
				if(listaNumero.contains(numero)){//Si el numero que generŽ esta contenido en la lista
					return generar();//recursivamente lo mando a generar otra vez
				}else{//Si no esta contenido en la lista
					listaNumero.add(numero);
					return numero;
				}
			}
		}else{// ya se generaron todos los numeros
			return -1;
		}
	}

	//Genera un numero aleatorio
	private int numeroAleatorio(){

		return (int)(Math.random()*(valorFinal-valorInicial+1)+valorInicial);

	}

	//Metodo para comprobar el modo en el que se ecuentra (juego o ejercicio) y segun el modo
	//Si estas en modo juego genera solo 6 numeros aleatorios sin repetir y si no, genera numeros
	//aleatorios infinitos
	public void compruebaModo(int pregunta){
		String textosUpper = spinner.getSelectedItem().toString();
		if(logicstring[pregunta].equals(textosUpper)){
			fin_juego++;
			showAnimationAnswer(true);
			//Ponemos el texto en verde y ponemos la imagen de un tic verde.
			if(mIsPlaying){
				pregunta=generar();
				pregunta_actual=pregunta;
				puntos+=POINTS_FOR_QUESTION;
				puntuacion.setText(Integer.toString(puntos));
			}
			else {
				pregunta=RANDOM();
				pregunta_actual = pregunta;
			}
			imageview.setImageResource(arrayimage.getResourceId(pregunta, 0));
		}


		//Si no es igual es texto del string con el del editText

		else {	

			showAnimationAnswer(false);
		}	
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);
		message = getResources().getString(R.string.points_final) + " "
				+ puntos +" "+"puntos";

		Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.end_game));
		alert.setMessage(message);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alert.show();

	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mIsPlaying) {
			cancelGame();
		}
	}

}
