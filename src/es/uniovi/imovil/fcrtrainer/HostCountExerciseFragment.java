package es.uniovi.imovil.fcrtrainer;

import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HostCountExerciseFragment extends BaseExerciseFragment{

	private View mRootView;
	private Button btnCheck;
	private Button btnSolution;
	private TextView question;	
	private TextView answer;
	int bitsOne = generateRandomNumberOfBits();

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
		
        btnCheck =  (Button) mRootView.findViewById(R.id.btnCheckAnswer);
        btnSolution =  (Button) mRootView.findViewById(R.id.btnSolution);
    	question = (TextView) mRootView.findViewById(R.id.question);
    	answer = (TextView) mRootView.findViewById(R.id.answer);
    	question.setText(generateQuestion());
    	
    	
        btnCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	checkAnswer((answer.getEditableText().toString()));
            }
        });
        
        btnSolution.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSolution();
			}
		});
        
		return mRootView;
	}

	
	public String generateQuestion(){
		// Genera la pregunta con una mascara de subred aleatoria y obtiene las 
		// 3 posibles respuestas generando un numero de orden al mostrar aleatorio
		
		String wording;
		// Generamos el enunciado con la  mascara aleatoria
		return wording = bitsToMask();
		
	}
	
	private int generateRandomNumberOfBits() {
		Random rn = new Random();
		// Limite ha de ser 31, pero ponemos 30 para que la funcion nextInt
		// obtenga aleatorios entre 1 y 31 (al sumar 1 al resultado obtenido)
		int limit = 30;
		
		// Funcion nextInt devuelve un numero aleatorio entre [0, limite)
		int x = rn.nextInt(limit)+1;
		Log.v("X",x+"");
		return x;

	}
	
	public String bitsToMask(){
		// Convierte los bits generados a una mascara de subred válida
		
		// Mascara con el primer bit a 1 y el resto 0; 32 bits
		int mask = 0x80000000;
		// Mascara resultante despues de realizar operaciones
		int resultMask = 0;
		// String con la mascara que será devuelta por la función
		String fullMask = "0.0.0.0";
		
		for (int i=32; i > bitsOne; i--){
			// Operacion OR de la mascara y la mascara resultado
			resultMask = resultMask | mask;
			// Desplazamiento del bit a 1 de la mascara al anterior
			mask = mask >> 1;
		}
		
		// Convertir la cadena de bits en una mascara de subred		
		fullMask = String.format("%d.%d.%d.%d", 
				(resultMask & 0x0000000000ff000000L) >> 24, 
				(resultMask & 0x0000000000ff0000) >> 16, 
				(resultMask & 0x0000000000ff00) >> 8, 
				 resultMask & 0xff);
		
		// Devuelve una mascara de subred
		return fullMask;
	}
	
	//Metodo para comprobar la respuesta	
	public void checkAnswer (String answ) {		
		//Si es correcta, cambia la máscara por una nueva y pone el "EditText" en blanco
		//Log.v("CHECK_AnSWER_answ", answ);
		//Log.v("CHECK_AnSWER_HOSTS", calculateHosts(bitsOne));
		if ((answ == calculateHosts(bitsOne))){
			showAnimationAnswer(true);
			question.setText(generateQuestion());
			answer.setText("");
			
			} else
				showAnimationAnswer(false);		
	}
	
	//Metodo para mostrar la solucion
	public void showSolution() {
		answer.setText(calculateHosts(bitsOne));		
	}
	
	private String calculateHosts(int bitsOne){
		Log.v("BITSONE", bitsOne+"");
		// Numero de bits a 0
		int bitsZero = 32 - bitsOne;
		Log.v("BITSCERO", bitsZero+"");
		// Numero de hosts = 2^n -2
		long hosts = (long) (Math.pow(2,bitsZero)-2);
		Log.v("CALCULATEHOSTS", hosts+"");
		return hosts+"";
	}
	
	
}
