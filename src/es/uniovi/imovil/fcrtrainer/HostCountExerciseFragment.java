package es.uniovi.imovil.fcrtrainer;

import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HostCountExerciseFragment extends BaseExerciseFragment{

	private static final int RANDOM_NUMBER_LIMIT = 32;
	
	private View mRootView;
	private Button btnCheck;
	private Button btnSolution;
	private String [] hostCountQuestions;
	private String [] hostCountAnswers;
	private TextView question;	
	EditText answer;
	int randomNumberQuestion;

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
    	answer = (EditText) mRootView.findViewById(R.id.answer);
    	hostCountQuestions = getResources().getStringArray(R.array.host_count_questions);
    	hostCountAnswers = getResources().getStringArray(R.array.host_count_answers);
    	randomNumberQuestion = generateRandomNumber();
    	//Carga una de las preguntas del array 
    	question.setText(hostCountQuestions[randomNumberQuestion]);
    	
    	
        btnCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	checkAnswer((answer.getText().toString()));
            }
        });
        
        btnSolution.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				showSolution(randomNumberQuestion);
			}
		});
        
		return mRootView;
	}
	
	private int generateRandomNumber() {
		Random rn = new Random();
		
		// Funcion nextInt devuelve un numero aleatorio entre [0, limite)
		int x = rn.nextInt(RANDOM_NUMBER_LIMIT);
		return x;

	}

	public void checkAnswer (String a) {
		//Si la respuesta es correcta, genera otra nueva pregunta y borra respuesta
		if ((a.toString().equals(hostCountAnswers[randomNumberQuestion].toString()))){
			showAnimationAnswer(true);
			randomNumberQuestion = generateRandomNumber();
			question.setText(hostCountQuestions[randomNumberQuestion]);
			answer.setText("");
			
			} else
				showAnimationAnswer(false);		
	}
	
	//Metodo para mostrar la solucion
	public void showSolution(int numberOfQuestion) {
		answer.setText(hostCountAnswers[numberOfQuestion]);		
	}
	
	
}
