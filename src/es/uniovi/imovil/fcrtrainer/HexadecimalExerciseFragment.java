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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Ejercicio a modo de prueba que no hace nada particular, solo mostrar
 * una etiqueta.
 *
 */
public class HexadecimalExerciseFragment extends BaseExerciseFragment {
	
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
		TextView text = (TextView) rootView.findViewById(R.id.textview);
		text.setText(getClass().getCanonicalName());
		return rootView;
	}
}
