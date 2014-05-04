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

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TwosComplementExerciseFragment  extends BaseExerciseFragment {
	private static final int NBIT = 7;
	private static final int MAX_NUMBER_TO_CONVERT=(int) (Math.pow(2,NBIT-1)-1);
	private static final int MIN_NUMBER_TO_CONVERT=(int) -(Math.pow(2,NBIT-1));
	private static final int GENERATE_DECIMAL=0;
	private static final int GENERATE_BINARY=1;
	private static final int MAX_QUESTS_GAME=5;
	private static final int POINTS_CORRECT_ANSWER=10;
	
	private RelativeLayout rel_cards;
	private Button but_check;
	private Button but_solution;
	private Button but_setMode;
	private boolean GameMode = false;
	private int quests_shown;
	private int points = 0;
	private EditText edi_answer;
	private TextView tex_tittle;
	private TextView tex_numberToConvert;
	private String numbToConvert;
	private boolean modeToDecimal=false;
	
public static TwosComplementExerciseFragment newInstance() {
		
		TwosComplementExerciseFragment fragment = new TwosComplementExerciseFragment();
		return fragment;
	}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	
	View rootView;
	rootView = inflater.inflate(R.layout.fragment_twos_complement, container, false);
	
	rel_cards = (RelativeLayout) rootView.findViewById(R.id.rel_cards);
	but_check = (Button) rootView.findViewById(R.id.bCheck);
	but_solution = (Button) rootView.findViewById(R.id.bSeesolution);	
	but_setMode = (Button) rootView.findViewById(R.id.bSetconversion);
	
	edi_answer = (EditText) rootView.findViewById(R.id.etAnswer);
	tex_tittle = (TextView) rootView.findViewById(R.id.txTittle);
	tex_numberToConvert = (TextView) rootView.findViewById(R.id.txNumbertoconvert);
	
	but_check.setOnClickListener(new OnClickListener() 
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if(GameMode==false)
			{
				if(but_check.getText().equals(getResources().getString((R.string.next_binary))))
				{
					edi_answer.setText("");
					but_check.setText(getResources().getString(R.string.check));
					if(modeToDecimal)
						generateRand(GENERATE_BINARY);
					else
						generateRand(GENERATE_DECIMAL);		
				}
						
				else
				{
					checkAnswer();
				}
			}
			else
			{
				checkAnswer();
				
			}
		}
	});
	
	but_solution.setOnClickListener(new OnClickListener() 
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			seeSolution();
		}
	});
	
	but_setMode.setOnClickListener(new OnClickListener() 
	{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			modeToDecimal = !modeToDecimal;
			if(modeToDecimal)
			{
				tex_tittle.setText(getResources().getString(R.string.convert_to_decimal));
				generateRand(GENERATE_BINARY);
			}
			else
			{
				tex_tittle.setText(getResources().getString(R.string.convert_to_twoscomplement));
				generateRand(GENERATE_DECIMAL);
			}
			edi_answer.setText("");
		}
	});
		
	generateRand(GENERATE_DECIMAL);
	
	return rootView;
}

private void generateRand(int mode) {
	// TODO Auto-generated method stub
	Random rand = new Random(); //To generate positive or negative numbers, in this case [-64,63]
	int num_rand= rand.nextInt(MAX_NUMBER_TO_CONVERT-MIN_NUMBER_TO_CONVERT+1)+MIN_NUMBER_TO_CONVERT;

	if (mode==GENERATE_BINARY)
	{
		numbToConvert= toTwosComplement(""+num_rand);
		tex_numberToConvert.setText(numbToConvert);
	}
	else if(mode==GENERATE_DECIMAL)
	{
		numbToConvert=""+num_rand;
		tex_numberToConvert.setText(numbToConvert);
	}
}

private String toTwosComplement(String numb) {

	int num = Integer.parseInt(numb);
	char aux;
	int tam;
	int res;

	String res_negativo="";

	String auxi= Integer.toBinaryString(Math.abs(num));
	tam=auxi.length();
	if(tam!=NBIT)
		for(int i = 0; i<NBIT-tam; i++)
				auxi="0"+auxi;

	if(num<0)
	{
		for(int i = 0; i<NBIT; i++)
		{
			aux=(char) auxi.charAt(i);
			if(aux=='0')
				res_negativo=res_negativo+"1";
			if(aux=='1')
				res_negativo=res_negativo+"0";
		}
	
		res=(Integer.parseInt(res_negativo, 2))+(Integer.parseInt("1", 2));
		auxi=""+Integer.toString(res, 2);
	}
	return auxi;
		
}

private String toDecimal(String numb) {
	// TODO Auto-generated method stub
	char aux;
	int res;
	String res_negativo="";
	
	if(numb.charAt(0)=='1')
	{
		for(int i = 0; i<NBIT; i++)
		{
			aux=(char) numb.charAt(i);
			if(aux=='0')
				res_negativo=res_negativo+"1";
			if(aux=='1')
				res_negativo=res_negativo+"0";
		}
		res=(Integer.parseInt(res_negativo, 2))+(Integer.parseInt("1", 2));
		return "-"+res;
	}
	else
		return ""+Integer.parseInt(numb, 2);
}

private void checkAnswer() {
	// TODO Auto-generated method stub
	String correctAnswer;

	if(modeToDecimal)
	{
		correctAnswer = toDecimal(numbToConvert);
		if(correctAnswer.equals(edi_answer.getText().toString()))
		{
			showAnimationAnswer(true);
			generateRand(GENERATE_BINARY);
			if(GameMode)
			{
				quests_shown++;
				points=points+POINTS_CORRECT_ANSWER;
			}
		}
		else
				showAnimationAnswer(false);
	}
	else
	{
		correctAnswer = toTwosComplement(numbToConvert);
		if(correctAnswer.equals(edi_answer.getText().toString()))
		{
			showAnimationAnswer(true);
			generateRand(GENERATE_DECIMAL);
			if(GameMode)
			{
				quests_shown++;
				points=points+POINTS_CORRECT_ANSWER;
			}
		}
		
		else
		{
			showAnimationAnswer(false);
		}
	}	
	edi_answer.setText("");
	
	if(quests_shown==MAX_QUESTS_GAME)
	{
		endGame();
	}
}

private int calculatePoints() {
	//Funciones de tiempo
	int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
	//every remaining second gives one extra point.
	points = (int) (points + remainingTimeInSeconds);
	return points;
}

private void seeSolution() {
	// TODO Auto-generated method stub
	
	if(modeToDecimal)
		edi_answer.setText(toDecimal(numbToConvert));
	else
		edi_answer.setText(toTwosComplement(numbToConvert));
	
	but_check.setText(R.string.next_binary);	
}

void startGame() {

	points = 0;
	but_check.setVisibility(View.VISIBLE);
	rel_cards.setVisibility(View.VISIBLE);
	but_solution.setVisibility(View.GONE);
	but_setMode.setVisibility(View.GONE);
	
	super.startGame();
	GameMode=true;
	quests_shown=1;
	
	
	if(modeToDecimal)
	{
		tex_tittle.setText(getResources().getString(R.string.convert_to_decimal));
		generateRand(GENERATE_BINARY);
	}
	else
	{
		tex_tittle.setText(getResources().getString(R.string.convert_to_twoscomplement));
		generateRand(GENERATE_DECIMAL);
	}
	

}

void cancelGame() {
	super.cancelGame();
	but_check.setVisibility(View.VISIBLE);
	rel_cards.setVisibility(View.VISIBLE);
	but_solution.setVisibility(View.VISIBLE);
	but_setMode.setVisibility(View.VISIBLE);
	GameMode=false;
	if(modeToDecimal)
	{
		tex_tittle.setText(getResources().getString(R.string.convert_to_decimal));
		generateRand(GENERATE_BINARY);
	}
	else
	{
		tex_tittle.setText(getResources().getString(R.string.convert_to_twoscomplement));
		generateRand(GENERATE_DECIMAL);
	}
}

void endGame() {
	super.endGame();
	tex_tittle.setText(R.string.finJuego);
	tex_numberToConvert.setText(getResources().getString(R.string.points01) + calculatePoints() + getResources().getString(R.string.points02));
	but_check.setVisibility(View.GONE);
	rel_cards.setVisibility(View.GONE);

	//enviar puntuacion 
	try {

		HighscoreManager.addScore(getActivity(), points, R.string.twoscomplement, new Date(), null);
	} catch (NumberFormatException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

}
