package es.uniovi.imovil.fcrtrainer.highscores;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.uniovi.imovil.fcrtrainer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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

	private static SharedPreferences mSharedPreferences;

	/**
	 * Lee la lista de mejores puntuaciones
	 * 
	 * @param context
	 *            Contexto para gestionar recursos
	 * @return Un array con la lista de todas las mejores puntuaciones
	 *         almacenadas
	 * @throws JSONException
	 */
	public static ArrayList<Highscore> loadHighscores(Context context)
			throws JSONException {
		mSharedPreferences = context.getSharedPreferences(PREFERENCES_FILENAME,
				Context.MODE_PRIVATE);
		String highscoreJson = mSharedPreferences.getString(
				HIGHSCORE_PREFERENCE, "");

		return jsonToHighscoreList(context, highscoreJson);
	}

	private static ArrayList<Highscore> jsonToHighscoreList(Context context,
			String highscoreJson) throws JSONException {
		ArrayList<Highscore> highscores = new ArrayList<Highscore>();

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

			highscores.add(new Highscore(score, exercise, dateString, userName));
		}

		return highscores;
	}

	/**
	 * Añade una puntuación a los datos de mejores puntuaciones
	 * 
	 * @param context
	 *            Contexto para gestionar recursos
	 * @param score
	 *            Puntos obtenidos
	 * @param exercise
	 *            Ejercicio donde se han obtenido. Debe ser una de las
	 *            constantes en ExerciseFragmentFactory
	 * @param date
	 *            Fecha en la que se obtuvo la puntuación
	 * @param userName
	 *            Nombre del usuario que obtuvo la puntuación. Si se pasa null,
	 *            será el nombre de usuario por defecto
	 * @throws JSONException
	 *             Cuando no se puede pasar a JSON o de JSON una puntuación
	 */
	public static void addScore(Context context, int score, int exercise,
			Date date, String userName) throws JSONException {
		if (userName == null) {
			userName = context.getString(R.string.default_user_name);
		}
		Highscore highscore = new Highscore(score, exercise, date, userName);

		ArrayList<Highscore> highscores = new ArrayList<Highscore>();
		try {
			highscores = loadHighscores(context);
		} catch (JSONException e) {
			Log.d(TAG, "Error al leer las puntuaciones para añadir una nueva: "
					+ e.getMessage());
		}
		highscores.add(highscore);
		saveHighscores(context, highscores);
	}

	private static void saveHighscores(Context context,
			ArrayList<Highscore> highscores) throws JSONException {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		String json = highscoresToJsonString(highscores);
		editor.putString(HIGHSCORE_PREFERENCE, json);
		editor.commit();
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
