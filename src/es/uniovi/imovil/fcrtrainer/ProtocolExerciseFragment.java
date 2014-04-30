package es.uniovi.imovil.fcrtrainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProtocolExerciseFragment extends BaseExerciseFragment {

	//Problemas al cargar de la BD las preguntas.
	//Esqueleto m’nimo, pero sin probar la funcionalidad por no cargarse bien la BD.
	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final int DB_VERSION = 1;
	private ArrayList<Test> testList=null;
	private View mRootView;
	private TextView question;
	private int i=0;
	Test test;
	RadioGroup rg;
	RadioButton rb1;
	RadioButton rb2;
	RadioButton rb3;
	Button bCheck;
	Button bSolution;
	int rbSelected;
	private boolean changeColor=false;

	public static ProtocolExerciseFragment newInstance() 
	{
		ProtocolExerciseFragment fragment = new ProtocolExerciseFragment();
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		mRootView=inflater.inflate(R.layout.fragment_protocol, container, false);
        DataBaseHelper db = new DataBaseHelper(this.getActivity(), DB_NAME, null, DB_VERSION);
        rb1 = (RadioButton) mRootView.findViewById(R.id.rb1);
        rb2 = (RadioButton) mRootView.findViewById(R.id.rb2);
        rb3 = (RadioButton) mRootView.findViewById(R.id.rb3);
        rg = (RadioGroup) mRootView.findViewById(R.id.rg1);
        question = (TextView) mRootView.findViewById(R.id.exerciseQuestion); 
        bCheck = (Button) mRootView.findViewById(R.id.checkbutton);
		bSolution = (Button) mRootView.findViewById(R.id.seesolution);
        try 
        {
            db.createDataBase();
            db.openDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Cargar la bd con las preguntas y respuestas en un array-list.
        testList=db.loadData();
        //Lanzar el entrenamiento.
		//seeDB();
        training(); 
	    //Raccionar a eventos del RadioGroup
	    rb1.setOnClickListener(new RadioButton.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            rbSelected=1;
	        }
	    });
	    
	    rb2.setOnClickListener(new RadioButton.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            rbSelected=2;
	        }
	    });
	    
	    rb3.setOnClickListener(new RadioButton.OnClickListener(){
	        @Override
	        public void onClick(View v) {
	            rbSelected=3;
	        }
	    });
	    
	    //Eventos del botón de comprobar la solución.
		bCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeColor=false;
				switch(rbSelected)
				{
				case 1:
					if (rb1.getText().equals(test.getResponse()))
						showAnimationAnswer(true);
					else 
						showAnimationAnswer(false);
					rb1.setChecked(false);
					reset();
					training();
					return;
				case 2:
					if (rb2.getText().equals(test.getResponse()))
						showAnimationAnswer(true);
					else
						showAnimationAnswer(false);
					if (!changeColor)
						rb2.setTextColor(0xff000000);
					rb2.setChecked(false);
					reset();
					training();
					return;
				case 3:
					if (rb3.getText().equals(test.getResponse()))
						showAnimationAnswer(true);
					else 
						showAnimationAnswer(false);
					rb3.setChecked(false);
					reset();
					training();	
					return;
				default:
					reset();
					training();
					return;
				}				
			}

			private void reset() {
				// TODO Auto-generated method stub
				rbSelected=0;
				i++;
				if (!changeColor)
				{
					rb1.setTextColor(0xff000000);
					rb2.setTextColor(0xff000000);
					rb3.setTextColor(0xff000000);
				}			
			}
		});
		
		//Botón de mostrar solución.
		bSolution.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					showSolution();
			}
		});
		return mRootView;		
	}


	private void training()
	{
		if (i<testList.size()) //Completar todas las preguntas de la Base de Datos.
		{
			test = testList.get(i);				
			//Mostrar pregunta y opciones.
			question.setText(test.getQuestion());
			rb1.setText(test.getOption1());
			rb2.setText(test.getOption2());
			rb3.setText(test.getOption3());					
			//Final de entrenamiento.
		}
		else //Reiniciar
		{
			i=0;
			training();
		}
	}
	
	public void showSolution() {
		// Mostrar la solución buena en rojo.
		changeColor=true;
		if (rb1.getText().equals(test.getResponse()))
		{
				rb1.setTextColor(0xff00ff00);
		}
		if (rb2.getText().equals(test.getResponse()))
		{
				rb2.setTextColor(0xff00ff00);
		}
		if (rb3.getText().equals(test.getResponse()))
		{
				rb3.setTextColor(0xff00ff00);
		}
	}
}
