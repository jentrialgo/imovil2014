package es.uniovi.imovil.fcrtrainer;



import java.util.ArrayList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;


public class LogicOperationExerciseFragment extends BaseExerciseFragment implements OnItemSelectedListener{

	private View mRootView;
	private Spinner mLogicOperationSpinner;
	ArrayList<Option> mOptions;
	private RelativeLayout mOptionFragment;
	static Boolean mBool;
	
	
	public static LogicOperationExerciseFragment newInstance() {
		
		LogicOperationExerciseFragment fragment = new LogicOperationExerciseFragment();
		return fragment;
	}
	
	public LogicOperationExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.fragmen_logic_operation, container, false);
		initializeLogicOperationSpinner();
		return mRootView;
	}

	public void initializeLogicOperationSpinner(){
		mOptions = new ArrayList<Option>();
		addOptions(mOptions, R.array.options);

		ArrayAdapter<Option> adapter = new ArrayAdapter<Option>(
				getActivity(), android.R.layout.simple_list_item_1, mOptions);

		mLogicOperationSpinner = (Spinner) mRootView
				.findViewById(R.id.spinner_logic_operation_options);
		mLogicOperationSpinner.setAdapter(adapter);

		mLogicOperationSpinner.setOnItemSelectedListener(this);
	}

	
	private void addOptions(ArrayList<Option> options, int arrayResourceId) {
		TypedArray array = getResources().obtainTypedArray(arrayResourceId);

		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			int resourceId = array.getResourceId(i, defaultId);

			Option option = new Option(getResources().getString(resourceId), resourceId);
			options.add(option);
		}

		array.recycle();
		
	}
	
	private void initializeFragmentView(int selectedExerciseId) {
		mOptionFragment = (RelativeLayout) mRootView.findViewById(R.id.fragment_LOcontainer);
		
		//Llamar al fragment adecuado (entrenamiento o juego)
		((MainActivity)getActivity()).replaceLOFragment(mBool);
	
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
		Option option = (Option) parent.getItemAtPosition(pos);
		//comprobar la opcion entrenamiento o juego para cargar los fragments
		if(option.getOption().equals("Entrenamiento"))
			mBool=true;
		if(option.getOption().equals("Juego"))
			mBool=false;
		initializeFragmentView(option.getId());
		
	}

	
	@Override
	public void onNothingSelected(AdapterView<?> view) {
		// No hacer nada
		
	}
	
}
