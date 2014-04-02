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
	private Button prueba;
	private TextView q;
	private int[] answers = new int[3];

	// Constructor
	public HostCountExerciseFragment() 
	{
	}
	
	public static HostCountExerciseFragment newInstance() 
	{
		HostCountExerciseFragment fragment = new HostCountExerciseFragment();
		return fragment;
	}
	
	/* linearlayout  contenedorRespuestas = getView().findViewById(R.id.contenedor);
	 * for(i=0;i<numPreguntas;i<0){
	 * TextView t = new TextView();
	 * t.setText(respueta[i]);
	 * contenedor.add(t);
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		mRootView=inflater.inflate(R.layout.fragment_host_count, container, false);
		
        prueba =  (Button) mRootView.findViewById(R.id.btnOk);
        
    	q = (TextView) mRootView.findViewById(R.id.question);
    	q.setText(generateQuestion());
    	
    	
        prueba.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
/*                    int bitsOne = generateRandom(1,31);
                    String textWording = "Prueba de enunciado ";
                	
                	Log.d("GENERATE WORDING", generateWording(textWording, bitsOne));*/

                }
            }
        );
        
		return mRootView;
	}

	
	public String generateQuestion(){
		// Genera la pregunta con una mascara de subred aleatoria y obtiene las 
		// 3 posibles respuestas generando un numero de orden al mostrar aleatorio
		
		String fullWording;
		String textWording ="Calcular el numero de hosts de la subred a partir de la mascara de red ";
		
		int bitsOne = generateRandom(1,31);

		// Generamos el enunciado completo con texto y mascara aleatoria
		fullWording = generateWording(textWording, bitsOne);
		
		// Generacion de respuestas y su orden (?)
		/*		for (int i=0; i < answers; i++){
			do{
				answer[i] = generateRandom(1,31);
			}while (answer[i] != bitsOne);
		}*/
		return fullWording;
	}
	
	private int generateRandom(int min, int max) {
		Random rn = new Random();
		// Limite ha de ser 31, pero ponemos 30 para que la funcion nextInt
		// obtenga aleatorios entre 1 y 31 (al sumar 1 al resultado obtenido)
		int limit = 30;
		
		// Funcion nextInt devuelve un numero aleatorio entre [0, limite)
		int x = rn.nextInt(limit)+1;
		return x;

	}

	public String generateWording(String textWording, int bitsOne){
		// Enunciado completo compuesto por la cadena de texto (constante) y los bits 
		// que forman la mascara de subred
		String fullWording = textWording + bitsToMask(bitsOne);
		return fullWording;
		
	}
	
	public String bitsToMask(int bitsOne){
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
	
	public int calculateHosts(String mask, int bitsOne){
		// Numero de bits a 0
		int n = 32 - bitsOne;
		
		// Numero de hosts = 2^n -2
		int hosts = 2^n -2;
		
		return hosts;
	}
	
	
}
