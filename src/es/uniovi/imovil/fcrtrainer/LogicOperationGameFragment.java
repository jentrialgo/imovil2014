package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogicOperationGameFragment extends BaseExerciseFragment{
	
	public static LogicOperationGameFragment newInstance() {
		
		LogicOperationGameFragment fragment = new LogicOperationGameFragment();
		return fragment;
	}
	
	public LogicOperationGameFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView;
		rootView = inflater.inflate(R.layout.logic_operation_game_item, container, false);
		
		return rootView;
	}

}
