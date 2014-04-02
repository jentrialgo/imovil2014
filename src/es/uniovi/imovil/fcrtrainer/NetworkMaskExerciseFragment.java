
package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NetworkMaskExerciseFragment extends BaseExerciseFragment{

	public static NetworkMaskExerciseFragment newInstance() {
		
		NetworkMaskExerciseFragment fragment = new NetworkMaskExerciseFragment();
		return fragment;
	}
	
	public NetworkMaskExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView;
		rootView = inflater.inflate(R.layout.fragment_logic_gate, container, false);
		
		return rootView;
	}

}
