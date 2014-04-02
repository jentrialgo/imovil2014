/*

Copyright 2014 Profesores y alumnos de la asignatura Informática Móvil de la EPI de Gijón

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */

package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar una
 * etiqueta.
 * 
 */
public class BinaryExerciseFragment extends BaseExerciseFragment implements OnClickListener {
	private BinaryConverter binaryConverter = new BinaryConverter();
	private View rootView;
	private String currentQuestion;
	
	public static BinaryExerciseFragment newInstance() {
		BinaryExerciseFragment fragment = new BinaryExerciseFragment();

		return fragment;
	}

	public BinaryExerciseFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_binary, container, false);

		Button check = (Button) rootView.findViewById(R.id.button_check);
		check.setOnClickListener(this);
		
		Button next = (Button) rootView.findViewById(R.id.button_next);
		next.setOnClickListener(this);
		
		newQuestion();
		
		return rootView;
	}
	
	private void clearResponseTextView(){
		TextView responseTextView = (TextView) rootView.findViewById(R.id.correct_or_wrong);
		responseTextView.setText("");
	}
	
	private void newQuestion(){
		int questionNumber = binaryConverter.createRandomNumber();
		TextView question = (TextView) this.rootView.findViewById(R.id.question);
		
		this.currentQuestion = "" + questionNumber;
		question.setText(this.currentQuestion); //convert to string ...if not application stops...
		
		
		clearResponseTextView();
		//System.out.println(questionNumber);	
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.button_check){
			checkAnswer();
		}
		
		if(view.getId() == R.id.button_next){
			newQuestion();
		}
	}
	

	private void checkAnswer(){
		EditText answerEditText	= (EditText) rootView.findViewById(R.id.answer);
		
		String answer = answerEditText.getText().toString();
		String questionConverted = this.binaryConverter.convertDecimalToBinary(this.currentQuestion);
		
		TextView responseTextView = (TextView) rootView.findViewById(R.id.correct_or_wrong);
		
		String response = "";
		if(answer.equals(questionConverted)){
			response = "CORRECT! Viva la vida!";
		}else{
			response = "NOOOOOO!";
		}
		responseTextView.setText(response);
	}




}
