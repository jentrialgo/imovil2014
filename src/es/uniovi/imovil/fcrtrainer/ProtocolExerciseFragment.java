package es.uniovi.imovil.fcrtrainer;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProtocolExerciseFragment extends BaseExerciseFragment implements OnClickListener {

	//Problemas al cargar de la BD las preguntas.
	//Esqueleto m’nimo, pero sin probar la funcionalidad por no cargarse bien la BD.
	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final int DB_VERSION = 1;
	private static final String TAG = null;
	private ArrayList<Test> testList=null;
	private View mRootView;
	private Button respButton1;
	private Button respButton2;
	private Button respButton3;
	private Button respButton4; 
	private Button retry;
	private TextView question;
	private int i=0;


	int trainingAcerts;
	Test test;

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
        DataBaseHelper db = new DataBaseHelper(this.getActivity(), DB_NAME, null, DB_VERSION);
        respButton1 = (Button) mRootView.findViewById(R.id.button1);
        respButton1.setOnClickListener(this);
        respButton2 = (Button) mRootView.findViewById(R.id.button2);
        respButton2.setOnClickListener(this);
        respButton3 = (Button) mRootView.findViewById(R.id.button3);
        respButton3.setOnClickListener(this);
        respButton4 = (Button) mRootView.findViewById(R.id.button4);
        respButton4.setOnClickListener(this);
        question = (TextView) mRootView.findViewById(R.id.exerciseQuestion); 
        retry = (Button) mRootView.findViewById(R.id.retry);
        retry.setOnClickListener(this);
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
		return mRootView;
	}

	private void retry()
	{
		i=0;
		respButton1.setVisibility(View.VISIBLE);
		respButton2.setVisibility(View.VISIBLE);
		respButton3.setVisibility(View.VISIBLE);
		respButton4.setVisibility(View.VISIBLE);
		retry.setVisibility(View.INVISIBLE);
		training();
	}


	//Controlar las respuestas del usuario.
	@Override
	public void onClick(View v)
	{
		int idSelected = v.getId();
		if (idSelected == retry.getId())
			retry();
		else if (idSelected==respButton1.getId()) //Pulsado bot—n 1.
			if (respButton1.getText().equals(test.getResponse()))
			{
				Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();	
			}
			else 
			{
				Toast.makeText(getActivity(),  " Fallo. La respuesta es "+ test.getResponse(), Toast.LENGTH_SHORT).show();
			}
		else if (idSelected==respButton2.getId()) //Pulsado bot—n 1.
			if (respButton2.getText().equals(test.getResponse()))
			{
				Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();	
			}
			else 
			{
				Toast.makeText(getActivity(), " Fallo. La respuesta es "+ test.getResponse(), Toast.LENGTH_SHORT).show();
			}
		else if (idSelected==respButton3.getId()) //Pulsado bot—n 1.
			if (respButton3.getText().equals(test.getResponse()))
			{
				Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();	
			}
			else 
			{
				Toast.makeText(getActivity(), " Fallo. La respuesta es "+ test.getResponse(), Toast.LENGTH_SHORT).show();
			}
		else
			if (respButton4.getText().equals(test.getResponse()))
			{
				//Acierto. Hacer algo. ÀIncrementar contador?. ÀMostrar mensaje?.
				Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();		    
			}
			else 
			{
				Toast.makeText(getActivity(), " Fallo. La respuesta es "+ test.getResponse(), Toast.LENGTH_SHORT).show();
			}
		i++; //Incrementar la variable global que marca la pregunta.
		training(); //Se regresa a training. Se pasar‡ a la siguiente pregunta.
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	private void training()
	{
		if (i<testList.size()) //Completar todas las preguntas de la Base de Datos.
		{
			test = testList.get(i);				
			//Mostrar pregunta y opciones.
			question.setText(test.getQuestion());
			respButton1.setText(test.getOption1());
			respButton2.setText(test.getOption2());
			respButton3.setText(test.getOption3());
			respButton4.setText(test.getOption4()); 		
			//Final de entrenamiento.
		}
		else
		{
			resetTrivial();
		}
	}

	private void resetTrivial()
	{
		question.setText(R.string.finish);
		respButton1.setVisibility(View.INVISIBLE);
		respButton2.setVisibility(View.INVISIBLE);
		respButton3.setVisibility(View.INVISIBLE);
		respButton4.setVisibility(View.INVISIBLE);
		retry.setVisibility(View.VISIBLE);
	}

	/*
	private void seeDB()
	{
		Iterator it = testList.iterator();
		Log.v(TAG,"SEEEEE");
		Log.v(TAG,"Elementos: "+testList.size());
		Test test;
		while (it.hasNext())
		{
			test = (Test) it.next();
			Log.v(TAG,test.getOption1()+" "+test.getOption2()+" "+test.getOption3()+" "+test.getOption4());
		}
	}
	*/

}