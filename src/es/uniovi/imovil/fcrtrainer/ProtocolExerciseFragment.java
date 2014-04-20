package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProtocolExerciseFragment extends BaseExerciseFragment {

	private View mRootView;

	public ProtocolExerciseFragment() 
	{
		//Constructor.
	}
	
	public static ProtocolExerciseFragment newInstance() 
	{
		ProtocolExerciseFragment fragment = new ProtocolExerciseFragment();
		return fragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView=inflater.inflate(R.layout.fragment_protocol, container, false);
		return mRootView;
	}
}
