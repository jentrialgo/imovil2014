package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class LogicOperationTrainFragment extends BaseExerciseFragment {

	private View mRootView;
	ArrayList<LOEntrada> mEntrada1;
	ArrayList<LOOperacion> mOperacion;
	ArrayList<LOEntrada> mEntrada2;
	private Spinner mLOEntrada1Spinner;
	private Spinner mLOOperacionSpinner;
	private Spinner mLOEntrada2Spinner;
	private TextView mSalida;
	
	public static LogicOperationTrainFragment newInstance() {
		
		LogicOperationTrainFragment fragment = new LogicOperationTrainFragment();
		return fragment;
	}
	
	public LogicOperationTrainFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.logic_operation_training_item, container, false);
		
		initializeLogicOperationEntrada1Spinner();
		initializeLogicOperationOperacionSpinner();
		initializeLogicOperationEntrada2Spinner();
		
		mSalida=(TextView) mRootView.findViewById(R.id.LOresult);
		
		((Button) mRootView.findViewById(R.id.LOsalida)).setOnClickListener(new OnClickListener() {
					@Override
					//Calcular el resultado de la operacion al pulsar el botón
					public void onClick(View v) {
						String e1;
						String op;
						String e2;
						
						e1 = mLOEntrada1Spinner.getSelectedItem().toString();
						op = mLOOperacionSpinner.getSelectedItem().toString();
						e2 = mLOEntrada2Spinner.getSelectedItem().toString();
						
						//Operacion AND
						if(op.equals("AND")){
							//0 and 0 = 0
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("0");
							//0 and 1 = 0
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("0");
							//1 and 0 = 0
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("0");
							//1 and 1 = 1
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("1");
						}
						//Operacion OR
						else if(op.equals("OR")){
							//0 or 0 = 0
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("0");
							//0 or 1 = 1
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("1");
							//1 or 0 = 1
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("1");
							//1 or 1 = 1
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("1");
						}
						//Operacion XOR
						else if(op.equals("XOR")){
							//0 xor 0 = 0
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("0");
							//0 xor 1 = 1
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("1");
							//1 xor 0 = 1
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("1");
							//1 xor 1 = 0
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("0");
						}
						//Operacion NAND
						else if(op.equals("NAND")){
							//0 nand 0 = 1
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("1");
							//0 nand 1 = 1
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("1");
							//1 nand 0 = 1
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("1");
							//1 nand 1 = 0
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("0");
						}
						//Operacion XNOR
						else if(op.equals("XNOR")){
							//0 xnor 0 = 1
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("1");
							//0 xnor 1 = 0
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("0");
							//1 xnor 0 = 0
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("0");
							//1 xnor 1 = 1
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("1");
						}
						//Operacion NOR
						else if(op.equals("NOR")){
							//0 nor 0 = 1
							if((e1.equals("0")) && (e2.equals("0")))
								mSalida.setText("1");
							//0 nor 1 = 0
							else if ((e1.equals("0")) && (e2.equals("1")))
								mSalida.setText("0");
							//1 nor 0 = 0
							else if ((e1.equals("1")) && (e2.equals("0")))
								mSalida.setText("0");
							//1 nor 1 = 0
							else if ((e1.equals("1")) && (e2.equals("1")))
								mSalida.setText("0");
						}
					}
        });
		
		return mRootView;
	}

	//Spinner entrada 1
	private void initializeLogicOperationEntrada1Spinner() {
		mEntrada1 = new ArrayList<LOEntrada>();
		addOptionsEntrada(mEntrada1, R.array.LOentrances);

		ArrayAdapter<LOEntrada> adapter = new ArrayAdapter<LOEntrada>(
				getActivity(), android.R.layout.simple_list_item_1, mEntrada1);

		mLOEntrada1Spinner = (Spinner) mRootView
				.findViewById(R.id.spinner_LOentrada1);
		mLOEntrada1Spinner.setAdapter(adapter);
	}
	
	//Spinner entrada 2
	private void initializeLogicOperationEntrada2Spinner() {
		mEntrada2 = new ArrayList<LOEntrada>();
		addOptionsEntrada(mEntrada2, R.array.LOentrances);

		ArrayAdapter<LOEntrada> adapter = new ArrayAdapter<LOEntrada>(
				getActivity(), android.R.layout.simple_list_item_1, mEntrada2);

		mLOEntrada2Spinner = (Spinner) mRootView
				.findViewById(R.id.spinner_LOentrada2);
		mLOEntrada2Spinner.setAdapter(adapter);
	}

	//Spinner operaciones logicas
	private void initializeLogicOperationOperacionSpinner() {
		mOperacion = new ArrayList<LOOperacion>();
		addOptionsOperacion(mOperacion, R.array.LOoperation);

		ArrayAdapter<LOOperacion> adapter = new ArrayAdapter<LOOperacion>(
				getActivity(), android.R.layout.simple_list_item_1, mOperacion);

		mLOOperacionSpinner = (Spinner) mRootView
				.findViewById(R.id.spinner_LOoperacion);
		mLOOperacionSpinner.setAdapter(adapter);
	}

	//Opciones operaciones logicas
	private void addOptionsOperacion(ArrayList<LOOperacion> operaciones, int arrayResourceId) {
		TypedArray array = getResources().obtainTypedArray(arrayResourceId);

		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			int resourceId = array.getResourceId(i, defaultId);

			LOOperacion operacion = new LOOperacion(getResources().getString(resourceId), resourceId);
			operaciones.add(operacion);
		}

		array.recycle();
	}

	//Opciones entradas
	private void addOptionsEntrada(ArrayList<LOEntrada> entradas, int arrayResourceId) {
		TypedArray array = getResources().obtainTypedArray(arrayResourceId);

		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			int resourceId = array.getResourceId(i, defaultId);

			LOEntrada entrada = new LOEntrada(getResources().getString(resourceId), resourceId);
			entradas.add(entrada);
		}

		array.recycle();
	}
}
