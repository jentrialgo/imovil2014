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

import es.uniovi.imovil.fcrtrainer.Screen;
import es.uniovi.imovil.fcrtrainer.Level;
import es.uniovi.imovil.fcrtrainer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
	private Spinner mLevelSpinner;
	private Spinner mExerciseSpinner;
	private ListView mHighscoreListView;

	public static HighscoresFragment newInstance() {
		return new HighscoresFragment();
	}

	public HighscoresFragment() {
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		mRootView = inflater.inflate(R.layout.fragment_highscores, container,
				false);

		initializeLevelSpinner();
		initializeExerciseSpinner();

		// El ListView se inicializa porque cuando se carga el spinner, se
		// genera un evento onItemSelected del spinner

		return mRootView;
	}

	private void initializeLevelSpinner() {
		ArrayList<String> levelNames = new ArrayList<>();

		TypedArray array = getResources().obtainTypedArray(
				R.array.pref_level_values);

		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			int resourceId = array.getResourceId(i, defaultId);

			String name = getResources().getString(resourceId);
			levelNames.add(name);
		}

		array.recycle();

		ArrayAdapter<String> adapter = new ArrayAdapter<>(
				getActivity(), android.R.layout.simple_spinner_item,
				levelNames);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
 
		mLevelSpinner = mRootView.findViewById(R.id.spinner_level);
		mLevelSpinner.setAdapter(adapter);

		mLevelSpinner.setOnItemSelectedListener(this);
	}

	private void initializeExerciseSpinner() {
		// La idea de esta función es crear los elementos del spinner utilizando
		// los elementos de Screen que son ejercicios

		ArrayAdapter<Screen> adapter = new ArrayAdapter<>(
				getActivity(), android.R.layout.simple_spinner_item,
				Screen.exercises());
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
	 
		mExerciseSpinner = mRootView.findViewById(R.id.spinner_exercise);
		mExerciseSpinner.setAdapter(adapter);

		mExerciseSpinner.setOnItemSelectedListener(this);
	}

	private void initializeListView(int selectedExerciseId, Level level) {
		mHighscoreListView = mRootView.findViewById(R.id.list_view_highscores);

		if (firstTime()) {
			addBasicHighscores();
		}

		ArrayList<Highscore> highscores = loadHighscores(level);

		highscores = selectHighscores(highscores, selectedExerciseId);

		if (highscores.size() == 0) {
			// This can happen if the ids of the highscores change. Recreate the high scores
			Toast.makeText(getActivity(),
					getActivity().getString(R.string.error_highscores_not_found),
					Toast.LENGTH_LONG).show();

			addBasicHighscores();
		}

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
			editor.apply();
			return true;
		}
		return false;
	}

	private ArrayList<Highscore> loadHighscores(Level level) {
		ArrayList<Highscore> highscores = new ArrayList<>();

		try {
			highscores = HighscoreManager.loadHighscores(getActivity(), level);
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
		ArrayList<Highscore> selectedHighscores = new ArrayList<>();

		for (Highscore highscore : highscores) {
			if (selectedExerciseId == highscore.getExercise()) {
				selectedHighscores.add(highscore);
			}
		}
		return selectedHighscores;
	}

	private void addBasicHighscores() {
		for (Level level : Level.values()) {
			addBasicHighscores(level);
		}
	}

	private void addBasicHighscores(Level level) {
		String[] names = getResources().getStringArray(R.array.student_names);
		ArrayList<Highscore> highscores = new ArrayList<>();

		for (Screen exercise : Screen.exercises()) {
			addBasicScoresForExercise(highscores, names, exercise.ordinal());
		}

		try {
			HighscoreManager.addAllHighscores(getActivity(), highscores, level);
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
		initializeListView(readExerciseIdFromSpinner(), readLevelFromSpinner());
	}

	private int readExerciseIdFromSpinner() {
		Screen exercise = (Screen) mExerciseSpinner.getSelectedItem();
		return exercise.ordinal();
	}

	private Level readLevelFromSpinner() {
		String levelName = (String) mLevelSpinner.getSelectedItem();
		return Level.fromString(getActivity(), levelName);
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		// No hay que hacer nada
	}

}
