package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HostCountExerciseFragment extends BaseExerciseFragment{

	private View mRootView;

	// Constructor
	public HostCountExerciseFragment() 
	{
	}
	
	public static HostCountExerciseFragment newInstance() 
	{
		HostCountExerciseFragment fragment = new HostCountExerciseFragment();
		return fragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView=inflater.inflate(R.layout.fragment_host_count, container, false);
		return mRootView;
	}
}
