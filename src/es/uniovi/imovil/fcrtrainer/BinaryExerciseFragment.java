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
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
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

	private enum MODE_GAME {
		BINARY_TO_DECIMAL, DECIMAL_TO_BINARY
	}

	private MODE_GAME modeGame = MODE_GAME.DECIMAL_TO_BINARY;

	private enum MODE_BUTTON {
		CHECK, NEXT
	}

	private MODE_BUTTON modeButton = MODE_BUTTON.CHECK;

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

		Button change = (Button) rootView.findViewById(R.id.button_change);
		change.setOnClickListener(this);

		newQuestion();
		clearViews();

		return rootView;
	}

	private void clearViews() {
		// Image
		clearImage();

		// UserInput
		EditText userInput = (EditText) rootView.findViewById(R.id.answer);
		userInput.setText("");

		// Information
		clearInformationText();

		// CheckButton
		setButtonToCheck();

		// SolutionButton
		setButtonInvisible(R.id.button_solution,false);
		
		// ChangeButton
		setButtonInvisible(R.id.button_change, false);
	}

	private void clearInformationText() {
		// InformationText
		TextView textView = (TextView) rootView.findViewById(R.id.information);
		textView.setText("");
	}

	private void clearImage() {

		ImageView response = (ImageView) rootView
				.findViewById(R.id.correct_or_wrong);
		response.setVisibility(View.INVISIBLE);
	}

	private void newQuestion() {
		// Binary to decimal
		int questionNumber = binaryConverter.createRandomNumber();
		TextView question = (TextView) this.rootView
				.findViewById(R.id.question);

		if (modeGame == MODE_GAME.BINARY_TO_DECIMAL)
			currentQuestion = binaryConverter
					.convertDecimalToBinary(questionNumber); // returns binary
																// as string
		else
			this.currentQuestion = "" + questionNumber; // convert decimal to
														// string

		// Show question in textfield
		question.setText(this.currentQuestion + " = ");
		// System.out.println(questionNumber);
	}

	@Override
	public void onClick(View view) {
		//button_check
		if (view.getId() == R.id.button_check) {
			clearInformationText();

			switch (modeButton) {
			case CHECK:
				checkAnswer();
				break;
			case NEXT:
				newQuestion();
				clearViews();
				break;
			}
		}
		
		//button_solution
		if (view.getId() == R.id.button_solution) {
			clearImage();
			setButtonInvisible(R.id.button_solution, true);
			setButtonInvisible(R.id.button_change, true);
			showSolution();
			setButtonToNext();
		}
		
		//button_change
		if (view.getId() == R.id.button_change) {
			changeMode();

			// update view
			clearViews();
			newQuestion();
			changeViews();
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

		if (modeGame == MODE_GAME.BINARY_TO_DECIMAL) {
			// User input: 0 to 9
			answer.setKeyListener(DigitsKeyListener.getInstance(getResources().getString(R.string.digits_available_decimal_binary)));
			answer.setHint(getResources().getString(R.string.hint_2_binary));
			
			setMaxLength(answer, 100);
		} else {
			// DECIMAL TO BINARY: User input: only 0 or 1
			answer.setKeyListener(DigitsKeyListener.getInstance(getResources().getString(R.string.digits_available_01_binary)));
			answer.setHint(getResources().getString(R.string.hint_binary));
			
			setMaxLength(answer, 8);
		}
	}
	
	private void setMaxLength(EditText text, int max){
		InputFilter maxLengthFilter = new InputFilter.LengthFilter(max);
		text.setFilters(new InputFilter[]{ maxLengthFilter });
	}

	private void setButtonToCheck() {
		// ButtonText
		Button next = (Button) rootView.findViewById(R.id.button_check);
		next.setText(getResources().getString(R.string.check_binary));
		modeButton = MODE_BUTTON.CHECK;
	}

	private void setButtonToNext() {
		Button check = (Button) rootView.findViewById(R.id.button_check);
		check.setText(getResources().getString(R.string.next_binary));

		modeButton = MODE_BUTTON.NEXT;
	}

	private void setButtonInvisible(int id, boolean invisible) {
		Button solutionButton = (Button) rootView
				.findViewById(id);
		if (invisible)
			solutionButton.setVisibility(View.INVISIBLE);
		else
			solutionButton.setVisibility(View.VISIBLE);
	}

	private void showSolution() {
		EditText solution = (EditText) rootView.findViewById(R.id.answer);
		
		if(modeGame == MODE_GAME.DECIMAL_TO_BINARY){
			solution.setText(convertToBinary(this.currentQuestion));
		}else{
			solution.setText(convertToDecimal(this.currentQuestion));
		}
	}

	private boolean isAnswerEmpty(String answer) {
		if (answer.matches("")) {
			TextView information = (TextView) getView().findViewById(
					R.id.information);
			information.setText(getResources().getString(R.string.insert_value_binary));
			return true;
		}
		return false;
	}

	private String convertToDecimal(String textToDecimal){
		return this.binaryConverter.convertBinaryToDecimal(textToDecimal);
	}
	private String convertToBinary(String textToBinary) {
		return this.binaryConverter
				.convertDecimalToBinary(textToBinary);

	}


	private void checkAnswer() {
		String answer = answerFromInput();
		if (isAnswerEmpty(answer))
			return;
		
		boolean response = false;
		if (modeGame == MODE_GAME.DECIMAL_TO_BINARY) {
			response = checkBinaryAnswer(answer);
		}else{
			response = checkDecimalAnswer(answer);
		}
		
		updateResponseView(response);
	}

	private String answerFromInput(){
		EditText answerEditText = (EditText) rootView.findViewById(R.id.answer);
		String answer = answerEditText.getText().toString();
		
		System.out.println("answer: " + answer);
		return answer;
	}
	private void updateResponseView(boolean response){
		// The image informs the user about a correct/wrong input
		ImageView image = (ImageView) rootView.findViewById(R.id.correct_or_wrong);
		if(response){
			image.setImageDrawable(getResources().getDrawable(
					R.drawable.true_1_64));	
			
			// update Buttons
			setButtonInvisible(R.id.button_change, true);
			setButtonInvisible(R.id.button_solution,true);
			setButtonToNext();
		}else{
			image.setImageDrawable(getResources().getDrawable(
					R.drawable.false_0_64));
		}

		image.setVisibility(View.VISIBLE); // show the image		
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
