package es.uniovi.imovil.fcrtrainer;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CidrExerciseFragment extends BaseExerciseFragment{
	
	Button bCheck;
	String [] mascaras;
	String [] respuestas;
	View rootView;
	TextView mascara;
	int contador;
	EditText edit;
	

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
		bCheck.setOnClickListener((OnClickListener) this);
		mascara.setText(mascaras[0]);
		
		//Array de respuestas
		respuestas = getResources().getStringArray(R.array.cidr);
		
		return rootView;
	}

}