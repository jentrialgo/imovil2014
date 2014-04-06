package es.uniovi.imovil.fcrtrainer;


import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class LogicOperationExerciseFragment extends BaseExerciseFragment implements OnClickListener, AnimationListener{

	private View mRootView;
	private TextView mTvEntrada1;
	private TextView mTvEntrada2;
	private TextView mTvOperacion;
	private TextView mTvSalida;
	private EditText mEtRespuesta;
	private Button mBOk;
	private Button mBSolucion;
	private Animation animation;
	private ImageView mAnimacion;
	private static String entrada1persistencia = "ENTRADA1";
	private static String entrada2persistencia = "ENTRADA2";
	private static String operacionpersistencia = "OPERACION";
	
	public static LogicOperationExerciseFragment newInstance() {
		
		LogicOperationExerciseFragment fragment = new LogicOperationExerciseFragment();
		return fragment;
	}
	
	public LogicOperationExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String binario;
		
		mRootView = inflater.inflate(R.layout.fragmen_logic_operation, container, false);
		
		mTvEntrada1 = (TextView) mRootView.findViewById(R.id.LOentrada1);
		binario = BinarioAleatorio();
		mTvEntrada1.setText(binario);
		
		mTvEntrada2 = (TextView) mRootView.findViewById(R.id.LOentrada2);
		binario = BinarioAleatorio();
		mTvEntrada2.setText(binario);
		
		mTvOperacion = (TextView) mRootView.findViewById(R.id.LOoperacion);
		String operacion = OperacionAleatoria();
		mTvOperacion.setText(operacion);
		
		mTvSalida = (TextView) mRootView.findViewById(R.id.LOsolucion);
		mTvSalida.setTextColor(Color.RED);
		
		mEtRespuesta = (EditText) mRootView.findViewById(R.id.LOrespuesta);
		mEtRespuesta.requestFocus();
		
		mBOk = (Button)mRootView.findViewById(R.id.LObCalcular);
		mBOk.setOnClickListener(this);
		
		mBSolucion = (Button) mRootView.findViewById(R.id.LObSolucion);
		mBSolucion.setOnClickListener(this);
		
		mAnimacion= (ImageView) mRootView.findViewById(R.id.imageViewAnimacion);
		mAnimacion.setVisibility(View.INVISIBLE);
	
		return mRootView;
	}

	
	//Funciones para el modo entrenamiento
	public String LOCalcularResultado (int entrada1, String operacion, int entrada2){
		int result=0;
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
		while (i < 5){
			solucion = "0" + solucion;
			i=solucion.length();
		}
		
		return solucion;
	}
	
	//Funcion que otorga numeros binarios aleatorios de 5 bits
	private String BinarioAleatorio(){
		//Generacion de un entero aleatorio entre 0 y 31
		Random rnd = new Random();
		int entero = rnd.nextInt(32);
		//Pasamos el entero a binario
		String binario = Integer.toBinaryString(entero);
		//LLenamos la cadena de 0 hasta tener 5 bits
		int i=binario.length();
		while (i < 5){
		  binario = "0" + binario;
		  i=binario.length();
		}
		return binario;
	}
	
	//Funcion que genera una operacion aleatoria (AND,OR...)
	private String OperacionAleatoria(){
		Random rnd = new Random();
		int entero = rnd.nextInt(3);
		
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
	//Fin de las funciones para el modo entrenamiento

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onClick(View v) {
		String binario;
		String op;
		
		String stringE1 = mTvEntrada1.getText().toString();
		int entrada1 = Integer.parseInt(stringE1,2);
		
		String stringE2 = mTvEntrada2.getText().toString();
		int entrada2 = Integer.parseInt(stringE2,2);
		
		String operacion = mTvOperacion.getText().toString();
		
		String solucion = LOCalcularResultado(entrada1,operacion, entrada2);
		
		String respuesta = mEtRespuesta.getText().toString();
		
		//si se ha pulsado el boton ok se comprueba el resultado del editText
		if(v.getId()== mBOk.getId()){
			if(respuesta.equals(solucion)){
				mTvSalida.setText("");
				
				//Crear animacion
				strartAnimacionEstrella();
				
				//si se acertó la respuesta, crear otra pregunta
				binario = BinarioAleatorio();
				mTvEntrada1.setText(binario);
				
				binario = BinarioAleatorio();
				mTvEntrada2.setText(binario);
				
				op = OperacionAleatoria();
				mTvOperacion.setText(op);
				
				mEtRespuesta.setText("");
			}
			else{
				mTvSalida.setText("Error, inténtalo de nuevo");
			}
		}
		
		//Si se ha pulsado el boton solucion se muestra la respuesta en el edittext
		if(v.getId() == mBSolucion.getId()){
			//Para mostrar la solucion anter debe haber dado una respuesta
			if(respuesta.equals("")){
				mTvSalida.setText("Para pedir la solución debes responer algo");
			}
			else{
				mEtRespuesta.setText(solucion);
				mTvSalida.setText("Respuesta pedida");
			}
		}
			
	}

	private void strartAnimacionEstrella() {
		mAnimacion.setVisibility(View.VISIBLE);
		animation = AnimationUtils.loadAnimation(getActivity(), R.anim.appearance);
		mAnimacion.startAnimation(animation);
		mAnimacion.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// Nada que hacer aqui
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// Nada que hacer aqui
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// Nada que hacer aqui
		
	}		
}
