package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NetworkLayerExerciseFragment extends BaseExerciseFragment {


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
		return rootView;
	}
}



