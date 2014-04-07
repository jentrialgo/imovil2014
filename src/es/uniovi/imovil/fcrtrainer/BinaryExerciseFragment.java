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
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar una
 * etiqueta.
 * 
 */
public class BinaryExerciseFragment extends BaseExerciseFragment implements
		OnClickListener {
	private BinaryConverter binaryConverter = new BinaryConverter();
	private View rootView;
	private String currentQuestion;

	private enum MODE_BUTTON{
		CHECK,
		NEXT
	}
	MODE_BUTTON modeButton = MODE_BUTTON.CHECK;
	
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

		Button solution = (Button) rootView.findViewById(R.id.button_solution);
		solution.setOnClickListener(this);

		newQuestion();
		clearViews();
		
		return rootView;
	}

	private void clearViews() {
		// Image
		ImageView response = (ImageView) rootView
				.findViewById(R.id.correct_or_wrong);
		response.setVisibility(View.INVISIBLE);

		// UserInput
		EditText userInput = (EditText) rootView.findViewById(R.id.answer);
		userInput.setText("");
		
		//Information
		clearInformationText();
		
		
		//CheckButton
		setButtonToCheck();
		
		//SolutionButton
		setSolutionButtonInvisible(false);
	}

	private void clearInformationText() {
		// InformationText
		TextView textView = (TextView) rootView.findViewById(R.id.information);
		textView.setText("");
	}

	private void newQuestion() {
		int questionNumber = binaryConverter.createRandomNumber();
		TextView question = (TextView) this.rootView
				.findViewById(R.id.question);

		this.currentQuestion = "" +  questionNumber; // convert to string ...
		question.setText(this.currentQuestion + " = "); 
		// System.out.println(questionNumber);
	}

	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.button_check) {
			clearInformationText();
			
			switch(modeButton){
				case CHECK: checkAnswer(); break;
				case NEXT: 
					newQuestion(); 
					clearViews();
					break;
			}
		}

		if (view.getId() == R.id.button_solution) {
			setSolutionButtonInvisible(true);
			showSolution();
			setButtonToNext();
		}
	}
	private void setButtonToCheck(){
		//ButtonText
		Button next = (Button) rootView.findViewById(R.id.button_check);
		next.setText(getResources().getString(R.string.check_binary));
		modeButton = MODE_BUTTON.CHECK;
	}
	
	private void setButtonToNext(){
		Button check = (Button) rootView.findViewById(R.id.button_check);
		check.setText(getResources().getString(R.string.next_binary));
		
		modeButton = MODE_BUTTON.NEXT;
	}
	
	private void setSolutionButtonInvisible(boolean invisible){
		Button solutionButton = (Button) rootView.findViewById(R.id.button_solution);
		if(invisible)
			solutionButton.setVisibility(View.INVISIBLE);
		else
			solutionButton.setVisibility(View.VISIBLE);
	}
	
	private void showSolution(){
		EditText solution = (EditText) rootView.findViewById(R.id.answer);
		solution.setText(convertQuestionToBinary());
	}

	private boolean isAnswerEmpty(String answer){
		if (answer.matches("") || !answer.contains("1")) {
			TextView information = (TextView) getView().findViewById(
					R.id.information);
			information.setText("insertar un valor > 0 por favor.");
			return true;
		}
		return false;
	}
	
	private String convertQuestionToBinary(){
		return this.binaryConverter.convertDecimalToBinary(this.currentQuestion);

	}
	private void checkAnswer() {
		EditText answerEditText = (EditText) rootView.findViewById(R.id.answer);

		String answer = answerEditText.getText().toString();
		System.out.println("answer: " + answer);
		
		if(isAnswerEmpty(answer))
			return;
		
		// check if other numbers then
		System.out.println("answer: " + answer);
		
		//get answer without zeros in front to compare it easy.
		answer = binaryConverter.deleteStartingZeroesFromBinaryInput(answer);
		
		//Convert Question to binary
		String questionConverted = convertQuestionToBinary();
		
		//The image informs the user about a correct/wrong input
		ImageView response = (ImageView) rootView
				.findViewById(R.id.correct_or_wrong);

		if (answer.equals(questionConverted)) {
			response.setImageDrawable(getResources().getDrawable(
					R.drawable.true_1_64));
			
			//show next button
			setSolutionButtonInvisible(true);
			setButtonToNext();
			
		} else {
			response.setImageDrawable(getResources().getDrawable(
					R.drawable.false_0_64));
		}
		response.setVisibility(View.VISIBLE); //show the image
	}



}
