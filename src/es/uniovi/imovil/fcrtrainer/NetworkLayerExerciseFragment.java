package es.uniovi.imovil.fcrtrainer;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class NetworkLayerExerciseFragment extends BaseExerciseFragment {
	Button comprobar;
	Spinner respuesta;
	String[] preguntas;
	String[] respuestas;


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
	
		View rootView;		
		rootView =inflater.inflate(R.layout.fragment_layer, container, false);		
		comprobar = (Button) rootView.findViewById(R.id.button_layer);
		setSpinnerContent( rootView );
		preguntas = getResources().getStringArray(R.array.layer_exercise_questions);
		respuestas = getResources().getStringArray(R.array.layer_exercise_answers);
		HashMap<String, String> layerMap = new HashMap<String, String>();
		layerMap.put(preguntas[0], "Capa1");
		layerMap.put(preguntas[1], "Capa4");
		layerMap.put(preguntas[2], "Capa3");
		layerMap.put(preguntas[3], "Capa1");
		layerMap.put(preguntas[4], "Capa3");
		layerMap.put(preguntas[5], "Capa4");
		layerMap.put(preguntas[6], "Capa1");
		 
		return rootView;
	}
	
	public void onClick(View v) {
		if(v.getId() == R.id.button_layer){
			//CompruebaRespuesta();

		}

	}
	
	private void setSpinnerContent( View view ){
		  respuesta = (Spinner) view.findViewById( R.id.spinner );
		// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
               R.array.layer_exercise_answers, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        respuesta.setAdapter(adapter);
	}



}



