/*

Copyright 2014 Profesores y alumnos de la asignatura Inform?tica M?vil de la EPI de Gij?n

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

import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar
 * una etiqueta.
 *
 */
@SuppressLint("NewApi") public class HexadecimalExerciseFragment extends BaseExerciseFragment {
	private EditText etResponse;
	private Button bCheck;
	private Button bChange;
	private TextView tvNumberToConvert;
	private TextView tvTitle;
	private View result;
	private ImageView resultimage;
	private int numberToConvert;
	private boolean tohex = true;
	private static final int MAX_NUMBER_TO_CONVERT = 1000;
	private static final int GENERATE_BIN_TO_CONVERT = 0;
	private static final int GENERATE_HEX_TO_CONVERT = 1;
	private AlphaAnimation animation;
	private AnticipateOvershootInterpolator antovershoot;

	public static HexadecimalExerciseFragment newInstance() {

		HexadecimalExerciseFragment fragment = new HexadecimalExerciseFragment();
		return fragment;
	}

	public HexadecimalExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_hexadecimal, container, false);

		etResponse = (EditText) rootView.findViewById(R.id.response);
		bCheck = (Button) rootView.findViewById(R.id.checkbutton);
		tvNumberToConvert = (TextView) rootView.findViewById(R.id.numbertoconvert);
		bChange = (Button) rootView.findViewById(R.id.change);
		tvTitle = (TextView) rootView.findViewById(R.id.exercisetitle);
		result = (View) rootView.findViewById(R.id.result);
		resultimage = (ImageView) rootView.findViewById(R.id.resultimage);

		antovershoot = new AnticipateOvershootInterpolator(10f);

		etResponse.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(EditorInfo.IME_ACTION_DONE == actionId){
					if(tohex) isCorrect(etResponse.getEditableText().toString().trim().toLowerCase(Locale.US));
					else isCorrect(etResponse.getEditableText().toString().trim());
				}
				return false;
			}});

		bCheck.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(tohex) isCorrect(etResponse.getEditableText().toString().trim().toLowerCase(Locale.US));
				else isCorrect(etResponse.getEditableText().toString().trim());
			}});

		bChange.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				tohex ^= true;
				if(tohex){
					etResponse.setInputType(EditorInfo.TYPE_CLASS_TEXT);
					tvTitle.setText(getResources().getString(R.string.convert_to_hex));
					generateRandomNumber(GENERATE_BIN_TO_CONVERT);
				}else{
					etResponse.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
					tvTitle.setText(getResources().getString(R.string.convert_to_bin));
					generateRandomNumber(GENERATE_HEX_TO_CONVERT);
				}
			}
		});

		generateRandomNumber(GENERATE_BIN_TO_CONVERT);

		return rootView;
	}

	public void generateRandomNumber(int type){
		Random randomGenerator = new Random();
		numberToConvert = randomGenerator.nextInt(MAX_NUMBER_TO_CONVERT);
		if(type == GENERATE_BIN_TO_CONVERT) tvNumberToConvert.setText(Integer.toBinaryString(numberToConvert));
		else tvNumberToConvert.setText(Integer.toHexString(numberToConvert).toUpperCase(Locale.US));
		System.out.println(numberToConvert);
	}

	/**
	 * Checks if the response is correct
	 * @param response the user input
	 * @param tohex the conversion type. If true, conversion to hex. If false, conversion to binary.
	 */
	public void isCorrect(String response){
		if(tohex){
			if (response.equals(Integer.toHexString(numberToConvert)))
				showResult(true);
			else showResult(false);
		}else{
			if (response.equals(Integer.toBinaryString(numberToConvert)))
				showResult(true);
			else showResult(false);
		}
	}

	public void showResult(boolean correct){

		result.setVisibility(View.VISIBLE);
		animation = new AlphaAnimation(0,1);
		animation.setDuration(600);
		animation.setFillBefore(true);
		animation.setFillAfter(true);
		animation.setRepeatCount(Animation.RESTART);
		animation.setRepeatMode(Animation.REVERSE);
		result.startAnimation(animation);

		/*
		 * I'm using two images here, one for the correct answer and another one for the incorrect ones. We should create
		 * another fragment (About this app?) to credit the author of those images, which are under the Creative Commons
		 * Attribution - No Derivative Works 3.0 Unported. The source of the images is iconfinder, 
		 * https://www.iconfinder.com/icons/27837/accept_check_confirmed_go_green_ok_positive_yes_icon#size=128
		 * and the author is Visual Pharm, http://icons8.com/. 
		 */
		
		if(correct){
			resultimage.setImageDrawable(getResources().getDrawable(R.drawable.correct));
			etResponse.setText("");
			if(tohex) generateRandomNumber(GENERATE_BIN_TO_CONVERT);
			else generateRandomNumber(GENERATE_HEX_TO_CONVERT);
		} else {
			resultimage.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));
		}

		// This only works in API 12+. We should take a look for alternatives (NineOldAndroids library, maybe?)
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			resultimage.animate().setDuration(500);
			resultimage.animate().setInterpolator(antovershoot).scaleX(1.5f).scaleY(1.5f);
		}

	}

}

