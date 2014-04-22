package es.uniovi.imovil.fcrtrainer;

import java.util.Locale;

import java.util.Random;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CidrExerciseFragment extends BaseExerciseFragment{
	
	private Button bCheck;
	private Button bSol;
	private String [] mascaras;
	private String [] respuestas;
	private TextView mascara;
	int n;
	EditText answer;
	public int mask;
	public static final int RANDOM_MASK = 0;
	

	public static CidrExerciseFragment newInstance() {
		
		CidrExerciseFragment fragment = new CidrExerciseFragment();
		return fragment;
	}
	
	public CidrExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView;
		rootView = inflater.inflate(R.layout.fragment_cidr, container, false);
		
		//Aray mascaras y se inicializa la pregunta "mascara"
		
		mascaras = getResources().getStringArray(R.array.mascaras);
		bCheck=(Button) rootView.findViewById(R.id.cButton);
		mascara.setText(mascaras[RANDOM_MASK]);
		answer = (EditText) rootView.findViewById(R.id.respuesta);
		respuestas = getResources().getStringArray(R.array.cidr);
		
		//Usamos listeners para los botones "Comprobar" y "Solucion"
		
		bCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			checkAnswer(answer.getEditableText().toString());
			}});
		
		bSol.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSolution();
			}
		});
		
		
		return rootView;
	}
	
	//Metodo para comprobar la respuesta
	public void checkAnswer (String answer) {
				
		if (answer.equals(respuestas[n])){
			showAnimationAnswer(true);
			RANDOM();
			} else
				showAnimationAnswer(false);		
	}
	
	//Metodo para mostrar la solucion
	public void showSolution() {
		
		answer.setText(respuestas[n]);
		
	}
	
	//Metodo para generar una nueva mascara para responder
	public void RANDOM(){

		Random ran = new Random();
		mask = ran.nextInt(RANDOM_MASK);
		
		mascara.setText(mascaras[mask]);
		
	}

}