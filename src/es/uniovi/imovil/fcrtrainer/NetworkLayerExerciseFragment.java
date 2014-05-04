package es.uniovi.imovil.fcrtrainer;

import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class NetworkLayerExerciseFragment extends BaseExerciseFragment {
	private TextView pregunta;
	private RadioGroup opciones;
	
	private RadioButton rb_layer;
	private RadioButton rb_network;
	private RadioButton rb_transport;
	private RadioButton rb_application;

	private Button comprobar;
	private Button solution;
	private String[] preguntas;
	private String[] respuestas;
	private String rb_pressed="";
	private int indice =0;

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
		//TextView para mostrar la pregunta
		pregunta = (TextView) rootView.findViewById(R.id.textlayer);
		
		//Radiogrup
		opciones = (RadioGroup) rootView.findViewById(R.id.layer_group);
		rb_layer = (RadioButton) rootView.findViewById(R.id.link_layer);
		rb_network = (RadioButton) rootView.findViewById(R.id.internet_layer);
		rb_transport = (RadioButton) rootView.findViewById(R.id.transport_layer);
		rb_application = (RadioButton) rootView.findViewById(R.id.application_layer);

		
		opciones.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup rGroup, int checkedId)
            {
            	 // TODO Auto-generated method stub
                switch(checkedId){
                case R.id.link_layer:
                	rb_pressed = "Capa de enlace";
                    break;
                case R.id.internet_layer:
                	rb_pressed = "Capa de internet";
                	break;
                case R.id.transport_layer:
                	rb_pressed = "Capa de transporte";
                	break;
                case R.id.application_layer:
                	rb_pressed = "Capa de aplicación";
                	break;
                }
            	
        		RadioButton checkedRadioButton = (RadioButton)opciones.findViewById(opciones.getCheckedRadioButtonId());
                boolean checked = checkedRadioButton.isChecked();
                Log.v("h","Checked:"+checked);
            }
        });
			
		//Buttons
		comprobar = (Button) rootView.findViewById(R.id.button_layer);
		solution = (Button) rootView.findViewById(R.id.button_solutionlayer);
		
		comprobar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_layer){
					CompruebaRespuesta();
				}
			}
		});
		
		solution.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(v.getId() == R.id.button_solutionlayer){
					Solucion();
				}
			}
		});

		//Arrays
		preguntas = getResources().getStringArray(R.array.layer_exercise_questions);
		respuestas = getResources().getStringArray(R.array.layer_exercise_answers);
		RANDOM();
		pregunta.setText(preguntas[indice]);	
		 
		return rootView;
	}
		
	protected void Solucion() {
		Log.v("g","entre en solución");
		if (respuestas[indice].equals("Capa de enlace")){
		rb_layer.setChecked(true);
		}
		else if (respuestas[indice].equals("Capa de internet")){
			rb_network.setChecked(true);
		}
		else if (respuestas[indice].equals("Capa de transporte")){
			rb_transport.setChecked(true);
		}
		else {
			rb_application.setChecked(true);
		}
	}

	private void CompruebaRespuesta() {
		if (rb_pressed.equals(respuestas[indice])){
			showAnimationAnswer(true);
			RANDOM();			
			pregunta.setText(preguntas[indice]);
		}
		else showAnimationAnswer(false);		
	}
	
	//Metodo para generar un número aleatorio
		public int RANDOM(){
			Random ran = new Random();
			indice = ran.nextInt(11);
			return indice;
		}
}





