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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar
 * una etiqueta.
 *
 */
public class HexadecimalExerciseFragment extends BaseExerciseFragment {
	private EditText etResponse;
	private Button bCheck;
	private TextView tvNumberToConvert;
	private int numberToConvert;
	
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
	
		bCheck.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				isCorrect(etResponse.getEditableText().toString().trim().toLowerCase(Locale.US));
				
			}});
		
		generateRandomNumber();
		
		return rootView;
	}
	
	public void generateRandomNumber(){
		Random randomGenerator = new Random();
		numberToConvert = randomGenerator.nextInt(1000);
		tvNumberToConvert.setText(String.valueOf(numberToConvert));
	}
	
	public void isCorrect(String response){
		if (response.equals(Integer.toHexString(numberToConvert))){
			Toast.makeText(getActivity(), getResources().getString(R.string.correct), Toast.LENGTH_SHORT).show();
			generateRandomNumber();
			etResponse.setText("");
		}else{
			Toast.makeText(getActivity(), getResources().getString(R.string.not_correct), Toast.LENGTH_SHORT).show();
		}
	}
	
}
