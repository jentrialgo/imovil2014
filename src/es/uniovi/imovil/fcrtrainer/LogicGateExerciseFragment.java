package es.uniovi.imovil.fcrtrainer;


import java.util.Random;

import android.content.res.TypedArray;

import android.os.Bundle;
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

public class LogicGateExerciseFragment extends BaseExerciseFragment  implements OnClickListener, OnItemSelectedListener{
	private Button buttoncheck;
	private String [] logicstring;
	private View rootView;
	private int contador;
	private TypedArray arrayimage;
	private ImageView imageview;
	private Button buttonsolution;
	private Spinner spinner; 
	private int n;
	public static final int RANDOM = 6;

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
		contador=RANDOM();

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

		spinner = (Spinner) rootView.findViewById(R.id.spinner_logic_gate);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
				R.array.logic_gates, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		imageview.setImageResource(arrayimage.getResourceId(contador, 0));



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
			//Mostramos la solución
			solutionLogicGate();
			break;
		}

	}


	public void CompruebaRespuesta(){
		String textosUpper = spinner.getSelectedItem().toString();
		if(logicstring[contador].equals(textosUpper)){

			showAnimationAnswer(true);
			//Ponemos el texto en verde y ponemos la imagen de un tic verde.
			contador=RANDOM();
			imageview.setImageResource(arrayimage.getResourceId(contador, 0));
		}


		//Si no es igual es texto del string con el del editText

		else {	

			showAnimationAnswer(false);
		}	
	}

	public void solutionLogicGate(){
		spinner.setSelection(contador);
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
	//Metodo para generar un número aleatorio
	public int RANDOM(){
		Random ran = new Random();
		n = ran.nextInt(RANDOM);

		return n;
	}
}
