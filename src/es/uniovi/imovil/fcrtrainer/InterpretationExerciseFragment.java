package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InterpretationExerciseFragment extends BaseExerciseFragment {

	
	public static InterpretationExerciseFragment newInstance() {
		
		InterpretationExerciseFragment fragment = new InterpretationExerciseFragment();
		return fragment;
	}
	
	public InterpretationExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView;
		rootView = inflater.inflate(R.layout.fragment_interpretation, container, false);
		
		return rootView;
	}
	
}
