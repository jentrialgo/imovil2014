
package es.uniovi.imovil.fcrtrainer;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NetworkMaskExerciseFragment extends BaseExerciseFragment implements OnClickListener{
	
	
	
	TextView enunciado;
	EditText solucion;
	Button botonR;
	Button botonC;
	String[]preguntas;
	String[]respuestas;
	ImageView imagen;
	int i;
	Random r;
	Handler handler;
	
	
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
		rootView = inflater.inflate(R.layout.fragment_networkmask, container, false);
		r=new Random();
		i=r.nextInt(5);
		preguntas=new String[5];
		respuestas=new String[5];
		handler=new Handler();
		imagen=(ImageView)rootView.findViewById(R.id.imagen);
		enunciado=(TextView)rootView.findViewById(R.id.TextEnun2);
		solucion=(EditText)rootView.findViewById(R.id.respuesta);
		botonR=(Button)rootView.findViewById(R.id.button1);
		botonC=(Button)rootView.findViewById(R.id.button2);
		preguntas=getResources().getStringArray(R.array.preguntas);
		
		respuestas=getResources().getStringArray(R.array.respuestas);
		
		
		enunciado.setText(preguntas[i]);
		botonR.setOnClickListener(this);
		botonC.setOnClickListener(this);
		
		
		
		return rootView;
	}
	
	
	public void onClick(View v){
		
		if(v.getId()==R.id.button1){
				Log.i("EOOOO", solucion.getText().toString());
				Log.i("EIIIII", respuestas[i]);
			if(solucion.getText().toString().equals(respuestas[i])){
				Toast.makeText(getActivity(), "correcto", 2000).show();
				imagen.setImageResource(R.drawable.correct);
				imagen.setVisibility(View.VISIBLE);
				handler.postDelayed(new Runnable(){
					public void run(){
						imagen.setImageResource(0);	
						i=r.nextInt(5);
						enunciado.setText(preguntas[i]);
						solucion.setText("");
					}
				}, 1500);
				
				
			}else{
				
				imagen.setImageResource(R.drawable.incorrect);
				imagen.setVisibility(View.VISIBLE);
				handler.postDelayed(new Runnable(){
					public void run(){
						imagen.setImageResource(0);	
						i=r.nextInt(5);
						enunciado.setText(preguntas[i]);
						solucion.setText("");
					}
				}, 1500);
				
				
				
				
			}
				
				
				
		}
		
		
		if(v.getId()==R.id.button2){
			
			solucion.setText(respuestas[i]);
		
			handler.postDelayed(new Runnable(){
				public void run(){
						
					i=r.nextInt(5);
					enunciado.setText(preguntas[i]);
					solucion.setText("");
				}
			}, 1500);	
			
			
		
	
		
		
	}
	
	
   }
	
}
