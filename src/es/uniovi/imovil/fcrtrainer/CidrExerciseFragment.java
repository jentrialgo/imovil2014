package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CidrExerciseFragment extends BaseExerciseFragment{

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
		
		return rootView;
	}

}