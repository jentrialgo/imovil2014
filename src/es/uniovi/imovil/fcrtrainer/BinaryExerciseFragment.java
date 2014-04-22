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
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

	private enum MODE_GAME {
		BINARY_TO_DECIMAL, DECIMAL_TO_BINARY
	}

	private MODE_GAME modeGame = MODE_GAME.DECIMAL_TO_BINARY;


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

		/**
		 * Set Listener
		 * **/
		Button check = (Button) rootView.findViewById(R.id.checkbutton);
		check.setOnClickListener(this);

		Button solution = (Button) rootView.findViewById(R.id.seesolution);
		solution.setOnClickListener(this);

		Button change = (Button) rootView.findViewById(R.id.change);
		change.setOnClickListener(this);

		newQuestion();

		return rootView;
	}

	private void clearUserInput() {
		EditText userInput = (EditText) rootView.findViewById(R.id.answer);
		userInput.setText("");
	}




	private void newQuestion() {
		int questionNumber = binaryConverter.createRandomNumber();
		TextView question = (TextView) this.rootView
				.findViewById(R.id.numbertoconvert);

		if (modeGame == MODE_GAME.BINARY_TO_DECIMAL)
			currentQuestion = binaryConverter
					.convertDecimalToBinary(questionNumber); // returns binary as string
		else
			this.currentQuestion = "" + questionNumber; // convert decimal to string

		question.setText(this.currentQuestion + " = ");
		
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.checkbutton) {
			checkAnswer();
		}
		if (view.getId() == R.id.seesolution) {
			showSolution();
		}
		
		if (view.getId() == R.id.change) {
			clearUserInput();
			changeMode();
			changeViews();
			newQuestion();
		}
		
	}
	

	private void changeMode() {
		if (modeGame == MODE_GAME.BINARY_TO_DECIMAL) {
			modeGame = MODE_GAME.DECIMAL_TO_BINARY;
		} else {
			modeGame = MODE_GAME.BINARY_TO_DECIMAL;
		}
	}
	
	private void changeViews() {
		EditText answer = (EditText) rootView.findViewById(R.id.answer);
		TextView title = (TextView) rootView.findViewById(R.id.exercisetitle);

		if (modeGame == MODE_GAME.BINARY_TO_DECIMAL) {
			
			title.setText(getResources().getString(R.string.convert_to_dec));
			
			// User input: 0 to 9
			answer.setKeyListener(DigitsKeyListener.getInstance(getResources().getString(R.string.digits_available_decimal_binary)));
			answer.setHint(getResources().getString(R.string.hint_2_binary));
			
		} else {
			
			title.setText(getResources().getString(R.string.convert_to_bin));
			
			// DECIMAL TO BINARY: User input: only 0 or 1
			answer.setKeyListener(DigitsKeyListener.getInstance(getResources().getString(R.string.digits_available_01_binary)));
			answer.setHint(getResources().getString(R.string.hint_binary));
		}
		
		
		
	}
	
	
	private void showSolution() {
		EditText solution = (EditText) rootView.findViewById(R.id.answer);
		
		if(modeGame == MODE_GAME.DECIMAL_TO_BINARY){
			solution.setText(convertToBinary(this.currentQuestion));
		}else{
			solution.setText(convertToDecimal(this.currentQuestion));
		}
	}



	private String convertToDecimal(String textToDecimal){
		return this.binaryConverter.convertBinaryToDecimal(textToDecimal);
	}
	
	private String convertToBinary(String textToBinary) {
		return this.binaryConverter
				.convertDecimalToBinary(textToBinary);
	}


	private void checkAnswer() {
		String answer = answerFromUserInput();
		
		//respond on empty inputs with error.
		if(answer.isEmpty()){
			super.showAnimationAnswer(false);
			return;
		}
		
		boolean response = false;
		if (modeGame == MODE_GAME.DECIMAL_TO_BINARY) {
			response = checkBinaryAnswer(answer);
		}else{
			response = checkDecimalAnswer(answer);
		}
		
		super.showAnimationAnswer(response);
		
		//only create new Question if user has right input
		if(response){
			newQuestion();
			clearUserInput();
		}
	}

	private String answerFromUserInput(){
		EditText answerEditText = (EditText) rootView.findViewById(R.id.answer);
		String answer = answerEditText.getText().toString();
		
		System.out.println("answer: " + answer);
		return answer;
	}
	

	private boolean checkDecimalAnswer(String answer){
		String answerConverted = convertToBinary(answer);
		if(answerConverted.equals(currentQuestion)){
			return true;
		}
		return false;
	}
	
	private boolean checkBinaryAnswer(String answer) {
		// get answer without zeros in front to compare it easy.
		answer = binaryConverter.deleteStartingZeroesFromBinaryInput(answer);

		// Convert Question to binary
		String questionConverted = convertToBinary(this.currentQuestion);
		if (answer.equals(questionConverted)) {
			return true;
		}
		return false;
	}
}
