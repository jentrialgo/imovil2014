package es.uniovi.imovil.fcrtrainer;


import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class LogicOperationExerciseFragment extends BaseExerciseFragment implements OnClickListener{

	private View mRootView;
	private TextView tvEntrada1;
	private TextView tvEntrada2;
	private TextView tvOperacion;
	private EditText etRespuesta;
	private Button bCheck;
	private Button bSolucion;
	private static final int MAX_NUMBER_OF_BITS = 5;
	private static final int BASE_BINARIA = 2;
	private static final int MAX_INT_NUMBER_TO_BINARY = 32;
	private static final int MAX_NUMBER_OF_OPERATIONS = 3;
	
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
		
		inicializarTexViews(mRootView);
		inicializarEditText(mRootView);
		inicializarButtons(mRootView);

		return mRootView;
	}

	
	private void inicializarTexViews(View mRootView2) {
		String binario;
		
		tvEntrada1 = (TextView) mRootView.findViewById(R.id.LOentrada1);
		binario = BinarioAleatorio();
		tvEntrada1.setText(binario);
		
		tvEntrada2 = (TextView) mRootView.findViewById(R.id.LOentrada2);
		binario = BinarioAleatorio();
		tvEntrada2.setText(binario);
		
		tvOperacion = (TextView) mRootView.findViewById(R.id.LOoperacion);
		String operacion = OperacionAleatoria();
		tvOperacion.setText(operacion);
	}
	
	
	private void inicializarEditText(View mRootView2) {
		etRespuesta = (EditText) mRootView.findViewById(R.id.LOrespuesta);
		
		etRespuesta.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (EditorInfo.IME_ACTION_DONE == actionId) {
						isCorrect(etRespuesta.getEditableText().toString().trim().toLowerCase(Locale.US));
					
				}
				return false;
			}
		});
	}
	
	
	private void inicializarButtons(View mRootView2) {
		bCheck = (Button)mRootView.findViewById(R.id.LObCalcular);
		bCheck.setOnClickListener(this);
		
		bSolucion = (Button) mRootView.findViewById(R.id.LObSolucion);
		bSolucion.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		if(v.getId()== bCheck.getId()){
			isCorrect(etRespuesta.getEditableText().toString().trim());
		}
		
		if(v.getId() == bSolucion.getId()){
			showSolution();
		}	
	}

	
	public void isCorrect(String answer) {
		String binario;
		String op;
		
		String solucion = LOCalcularResultado(tvEntrada1.getText().toString(),tvOperacion.getText().toString(), tvEntrada2.getText().toString());
		
		if(answer.equals(solucion)){
			showAnimationAnswer(true);
			//si se acertó la respuesta, crear otra pregunta
			binario = BinarioAleatorio();
			tvEntrada1.setText(binario);
				
			binario = BinarioAleatorio();
			tvEntrada2.setText(binario);
				
			op = OperacionAleatoria();
			tvOperacion.setText(op);
			
			etRespuesta.setText("");
		}
		else{
			showAnimationAnswer(false);
		}
	}
	
	public String LOCalcularResultado (String e1, String operacion, String e2){
		int entrada1 = Integer.parseInt(e1,BASE_BINARIA);
		int entrada2 = Integer.parseInt(e2,BASE_BINARIA);
		int result = 0;
		String solucion;
		
		if (operacion.equals("AND"))
				result = entrada1 & entrada2;
		else if (operacion.equals("OR"))
				result = entrada1 | entrada2;
		else if (operacion.equals("XOR"))
				result = entrada1 ^ entrada2;
		
		solucion = Integer.toBinaryString(result);
		
		//Rellenar con los 0 que falten por delante
		int i=solucion.length();
		while (i < MAX_NUMBER_OF_BITS){
			solucion = "0" + solucion;
			i=solucion.length();
		}
		
		return solucion;
	}
	
	
	private String BinarioAleatorio(){
		Random rnd = new Random();
		int entero = rnd.nextInt(MAX_INT_NUMBER_TO_BINARY);
		String binario = Integer.toBinaryString(entero);
		
		//LLenamos la cadena de 0 hasta tener 5 bits
		int i=binario.length();
		while (i < MAX_NUMBER_OF_BITS){
		  binario = "0" + binario;
		  i=binario.length();
		}
		return binario;
	}
	

	private String OperacionAleatoria(){
		Random rnd = new Random();
		int entero = rnd.nextInt(MAX_NUMBER_OF_OPERATIONS);
		String operacion;
		
		switch(entero){
		case 0:
			operacion = "AND";
			return operacion;
		case 1:
			operacion = "OR";
			return operacion;
		default:
			operacion = "XOR";
			return operacion;
		}
	}


	private void showSolution() {
		String solucion = LOCalcularResultado(tvEntrada1.getText().toString(),tvOperacion.getText().toString(), tvEntrada2.getText().toString());
		etRespuesta.setText(solucion);	
	}
}
