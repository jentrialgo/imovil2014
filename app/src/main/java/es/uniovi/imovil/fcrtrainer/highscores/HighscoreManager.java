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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.uniovi.imovil.fcrtrainer.Level;
import es.uniovi.imovil.fcrtrainer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * Clase para leer y guardar las puntuaciones
 * 
 */
public class HighscoreManager {
	private static final String TAG = "HigscoreManager";
	private static final String PREFERENCES_FILENAME = "Highscores";
	private static final String HIGHSCORE_PREFERENCE = "Highscores";
	private static final String HIGHSCORE_SCORE_TAG = "Highscore";
	private static final String HIGHSCORE_EXERCISE_TAG = "Exercise";
	private static final String HIGHSCORE_DATE_TAG = "Date";
	private static final String HIGHSCORE_USERNAME_TAG = "Username";
	private static final int MAX_NUMBER_HIGHSCORES = 10;

	private static SharedPreferences mSharedPreferences;

	/**
	 * Lee la lista de mejores puntuaciones
	 * 
	 * @param context
	 *            Contexto para gestionar recursos
	 * @param level Nivel a cargar
	 * @return Un array con la lista de todas las mejores puntuaciones
	 *         almacenadas
	 * @throws JSONException
	 */
	public static ArrayList<Highscore> loadHighscores(Context context,
			Level level)
			throws JSONException {
		mSharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME,
				Context.MODE_PRIVATE);
		String highscoreJson = mSharedPreferences.getString(
				preferenceName(level), "");

		return jsonToHighscoreList(context, highscoreJson);
	}

	private static String preferenceName(Level level) {
		return HIGHSCORE_PREFERENCE + level.toString();
	}

	private static ArrayList<Highscore> jsonToHighscoreList(Context context,
			String highscoreJson) throws JSONException {
		ArrayList<Highscore> highscores = new ArrayList<>();

		JSONArray array;
		array = new JSONArray(highscoreJson);

		for (int i = 0; i < array.length(); i++) {
			JSONObject object;
			object = array.getJSONObject(i);
			int score = object.getInt(HIGHSCORE_SCORE_TAG);
			int exercise = object.getInt(HIGHSCORE_EXERCISE_TAG);
			String dateString = object.getString(HIGHSCORE_DATE_TAG);
			String userName = object.getString(HIGHSCORE_USERNAME_TAG);
			if (userName == null) {
				userName = context.getString(R.string.default_user_name);
			}

			highscores
					.add(new Highscore(score, exercise, dateString, userName));
		}

		return highscores;
	}

	/**
	 * A�ade una puntuaci�n a los datos de mejores puntuaciones
	 * 
	 * @param context
	 *            Contexto para gestionar recursos
	 * @param score
	 *            Puntos obtenidos
	 * @param exercise
	 *            Ejercicio donde se han obtenido. Debe ser una de las
	 *            constantes usadas en FragmentFactory para lanzar el fragmento,
	 *            por ejemplo, R.string.logic_gate
	 * @param date
	 *            Fecha en la que se obtuvo la puntuaci�n
	 * @param userName
	 *            Nombre del usuario que obtuvo la puntuaci�n. Si se pasa null,
	 *            ser� el nombre de usuario por defecto
	 * @param level
	 *            Nivel para el que se consigue la puntuaci�n
	 * @throws JSONException
	 *             Cuando no se puede pasar a JSON o de JSON una puntuaci�n
	 */
	public static void addScore(Context context, int score, int exercise,
			Date date, String userName, Level level) throws JSONException {
		if (userName == null) {
			userName = context.getString(R.string.default_user_name);
		}
		Highscore highscore = new Highscore(score, exercise, date, userName);

		ArrayList<Highscore> highscores = new ArrayList<>();
		try {
			highscores = loadHighscores(context, level);
		} catch (JSONException e) {
			Log.d(TAG, "Error al leer las puntuaciones para a�adir una nueva: "
					+ e.getMessage());
		}
		highscores.add(highscore);
		highscores = trim(highscores, MAX_NUMBER_HIGHSCORES);
		saveHighscores(context, highscores, level);
	}

	private static ArrayList<Highscore> trim(ArrayList<Highscore> highscores,
			int maxNumberHighscores) {
		ArrayList<Highscore> trimmedHighscores = new ArrayList<>();
		SparseIntArray highscoresPerExercise = new SparseIntArray();

		Collections.sort(highscores);
		Collections.reverse(highscores);

		for (int i = 0; i < highscores.size(); i++) {
			int exercise = highscores.get(i).getExercise();
			if (highscoresPerExercise.get(exercise) != 0) {
				int numHighscores = highscoresPerExercise.get(exercise);
				if (numHighscores < maxNumberHighscores) {
					highscoresPerExercise.put(exercise, numHighscores + 1);
					trimmedHighscores.add(highscores.get(i));
				}
			} else {
				highscoresPerExercise.put(exercise, 1);
				trimmedHighscores.add(highscores.get(i));
			}
		}

		return trimmedHighscores;
	}

	/**
	 * A�ade un array de puntuaciones. Est� pensado para ser utilizado
	 * inicialmente cuando se rellenan con las puntuaciones iniciales para ser
	 * m�s r�pido que a�adirlas una a una
	 * 
	 * @param context
	 *			Contexto para gestionar recursos
	 *@param newHighscores
	 * 			Array de puntuaciones a añadir
	 * @param level
	 * 			Nivel para el que son las puntuaciones
	 * @throws JSONException
	 */
	public static void addAllHighscores(Context context,
			ArrayList<Highscore> newHighscores, Level level)
					throws JSONException {
		ArrayList<Highscore> highscores = new ArrayList<>();
		try {
			highscores = loadHighscores(context, level);
		} catch (JSONException e) {
			Log.d(TAG, "Error al leer las puntuaciones para a�adir una nueva: "
					+ e.getMessage());
		}
		highscores.addAll(newHighscores);
		highscores = trim(highscores, MAX_NUMBER_HIGHSCORES);
		saveHighscores(context, highscores, level);
	}

	private static void saveHighscores(Context context,
			ArrayList<Highscore> highscores, Level level) throws JSONException {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		String json = highscoresToJsonString(highscores);
		editor.putString(preferenceName(level), json);
		editor.apply();
	}

	private static String highscoresToJsonString(ArrayList<Highscore> highscores)
			throws JSONException {
		JSONArray array = new JSONArray();
		for (Highscore highscore : highscores) {
			JSONObject jsonHighscore = new JSONObject();
			jsonHighscore.put(HIGHSCORE_SCORE_TAG, highscore.getScore());
			jsonHighscore.put(HIGHSCORE_EXERCISE_TAG, highscore.getExercise());
			jsonHighscore.put(HIGHSCORE_DATE_TAG, highscore.getDate());
			jsonHighscore.put(HIGHSCORE_USERNAME_TAG, highscore.getUserName());
			array.put(jsonHighscore);
		}
		return array.toString();
	}

}
