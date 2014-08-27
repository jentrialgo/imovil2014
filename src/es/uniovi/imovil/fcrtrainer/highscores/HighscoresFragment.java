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

package es.uniovi.imovil.fcrtrainer.highscores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.Exercise;
import es.uniovi.imovil.fcrtrainer.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class HighscoresFragment extends Fragment implements
		OnItemSelectedListener {

	private static final String TAG = "HighscoresFragment";

	/**
	 * Nombre del fichero de preferencias.
	 */
	private static final String PREFERENCES = "preferences";

	/**
	 * Preferencia que indica si es la primera ver que se abren las puntuaciones
	 */
	private static final String FIRST_TIME_HIGHSCORES = "first_time_highscores";

	private View mRootView;
	private Spinner mExerciseSpinner;
	private ListView mHighscoreListView;
	ArrayList<Exercise> mExercises;

	public static HighscoresFragment newInstance() {
		HighscoresFragment fragment = new HighscoresFragment();
		return fragment;
	}

	public HighscoresFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_highscores, container,
				false);

		initializeExerciseSpinner();

		// El ListView se inicializa porque cuando se carga el spinner, se
		// genera un evento onItemSelected del spinner

		return mRootView;
	}

	private void initializeExerciseSpinner() {
		// La idea de esta función es crear los elementos del spinner utilizando
		// los arrays definidos en los recursos

		mExercises = new ArrayList<Exercise>();
		addExerciseModule(mExercises, R.array.codes);
		addExerciseModule(mExercises, R.array.digital_systems);
		addExerciseModule(mExercises, R.array.networks);

		ArrayAdapter<Exercise> adapter = new ArrayAdapter<Exercise>(
				getActivity(), android.R.layout.simple_list_item_1, mExercises);

		mExerciseSpinner = (Spinner) mRootView
				.findViewById(R.id.spinner_exercise);
		mExerciseSpinner.setAdapter(adapter);

		mExerciseSpinner.setOnItemSelectedListener(this);
	}

	private void addExerciseModule(ArrayList<Exercise> exercises,
			int arrayResourceId) {
		TypedArray array = getResources().obtainTypedArray(arrayResourceId);

		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			int resourceId = array.getResourceId(i, defaultId);

			Exercise exercise = new Exercise(getResources().getString(
					resourceId), resourceId);
			exercises.add(exercise);
		}

		array.recycle();
	}

	private void initializeListView(int selectedExerciseId) {
		mHighscoreListView = (ListView) mRootView
				.findViewById(R.id.list_view_highscores);

		if (firstTime()) {
			addBasicHighscores();
		}

		ArrayList<Highscore> highscores = loadHighscores();

		highscores = selectHighscores(highscores, selectedExerciseId);

		Collections.sort(highscores);
		Collections.reverse(highscores);

		HighscoreAdapter adapter = new HighscoreAdapter(getActivity(),
				highscores);
		mHighscoreListView.setAdapter(adapter);
	}

	private boolean firstTime() {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		boolean defaultValue = true;
		boolean firstTime = sharedPreferences.getBoolean(FIRST_TIME_HIGHSCORES,
				defaultValue);
		if (firstTime) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(FIRST_TIME_HIGHSCORES, false);
			editor.commit();
			return true;
		}
		return false;
	}

	private ArrayList<Highscore> loadHighscores() {
		ArrayList<Highscore> highscores = new ArrayList<Highscore>();

		try {
			highscores = HighscoreManager.loadHighscores(getActivity());
		} catch (JSONException e) {
			Log.d(TAG, "Error al analizar el JSON: " + e.getMessage());
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.error_parsing_highscores),
					Toast.LENGTH_LONG).show();

			// Las puntuaciones estarán vacías
		}
		return highscores;
	}

	private ArrayList<Highscore> selectHighscores(
			ArrayList<Highscore> highscores, int selectedExerciseId) {
		ArrayList<Highscore> selectedHighscores = new ArrayList<Highscore>();

		for (Highscore highscore : highscores) {
			if (selectedExerciseId == highscore.getExercise()) {
				selectedHighscores.add(highscore);
			}
		}
		return selectedHighscores;
	}

	private void addBasicHighscores() {
		String[] names = getResources().getStringArray(R.array.student_names);
		ArrayList<Highscore> highscores = new ArrayList<Highscore>();

		for (Exercise exercise : mExercises) {
			addBasicScoresForExercise(highscores, names, exercise.getId());
		}

		try {
			HighscoreManager.addAllHighscores(getActivity(), highscores);
		} catch (JSONException e) {
			Log.d(TAG, "Error al analizar el JSON: " + e.getMessage());
			Toast.makeText(
					getActivity(),
					getActivity().getString(
							R.string.error_adding_initial_highscores),
					Toast.LENGTH_LONG).show();
		}
	}

	private void addBasicScoresForExercise(ArrayList<Highscore> highscores,
			String[] names, int exerciseId) {
		for (int i = 0; i < names.length; i++) {
			int minScore = 10;
			int maxScore = 100;
			int score = minScore
					+ (int) (Math.random() * ((maxScore - minScore) + 1));
			Highscore highscore = new Highscore(score, exerciseId, new Date(),
					names[i]);
			highscores.add(highscore);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		Exercise exercise = (Exercise) parent.getItemAtPosition(pos);
		initializeListView(exercise.getId());
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		// No hay que hacer nada
	}

}
