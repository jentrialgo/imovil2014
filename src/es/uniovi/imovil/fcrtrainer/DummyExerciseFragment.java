package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar
 * una etiqueta.
 *
 */
public class DummyExerciseFragment extends BaseExerciseFragment {
	
	public static DummyExerciseFragment newInstance() {
		
		DummyExerciseFragment fragment = new DummyExerciseFragment();
		return fragment;
	}
	
	public DummyExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView;
		rootView = inflater.inflate(R.layout.fragment_dummy, container, false);
		
		return rootView;
	}
}
