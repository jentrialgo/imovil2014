package es.uniovi.imovil.fcrtrainer;



import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LogicOperationExerciseFragment extends BaseExerciseFragment{

	
	public static LogicOperationExerciseFragment newInstance() {
		
		LogicOperationExerciseFragment fragment = new LogicOperationExerciseFragment();
		return fragment;
	}
	
	public LogicOperationExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView;
		rootView = inflater.inflate(R.layout.fragmen_logic_operation, container, false);
		
		return rootView;
	}
	
	
}
