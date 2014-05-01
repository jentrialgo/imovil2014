package es.uniovi.imovil.fcrtrainer;

import java.util.Random;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CidrExerciseFragment extends BaseExerciseFragment{
	
	private Button bCheck;
	private Button bSol;
	private String [] mascaras;
	private String [] respuestas;
	private TextView mascara;
	int n;
	EditText answer;
	public int mask;
	public static final int RANDOM_MASK = 5;
	

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
		
		//Cogemos todos los views
		
		mascaras = getResources().getStringArray(R.array.mascaras);
		bCheck=(Button) rootView.findViewById(R.id.cButton);
		bSol=(Button) rootView.findViewById(R.id.sButton);
		mascara = (TextView) rootView.findViewById(R.id.mascara);
		answer = (EditText) rootView.findViewById(R.id.respuesta);
		respuestas = getResources().getStringArray(R.array.cidr);
		mask = RANDOM();
		mascara.setText(mascaras[mask]);
		
		
		//Usamos listeners para los botones "Comprobar" y "Solucion"
		
		bCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			checkAnswer((answer.getEditableText().toString()));
			}});
		
		bSol.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSolution(mask);
			}
		});
		
		
		return rootView;
	}
	
	//Metodo para comprobar la respuesta
	
	public void checkAnswer (String ans) {
		
		//Si es correcta, cambia la máscara por una nueva y pone el "EditText" en blanco
		if ((ans.toString().equals(respuestas[mask].toString()))){
			showAnimationAnswer(true);
			mask = RANDOM();
			mascara.setText(mascaras[mask]);
			answer.setText("");
			
			} else
				showAnimationAnswer(false);		
	}
	
	//Metodo para mostrar la solucion
	public void showSolution(int n) {
		
		answer.setText(respuestas[n]);
		
	}
	
	//Metodo para generar un número aleatorio
	public int RANDOM(){

		Random ran = new Random();
		n = ran.nextInt(RANDOM_MASK);

		return n;
	}

}