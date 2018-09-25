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

import org.json.JSONException;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Clase base de la que deben heredar todos los fragmentos de los ejercicios.
 * Contendrá toda la funcionalidad común a todos los ejercicios. Entre ellos
 * están
 * 
 * - Un botón en la barra de tareas para empezar y parar de jugar.
 * 
 * - Un panel donde se muestra la puntuación, el tiempo que queda y el nivel.
 * 
 * - La gestión de la puntuación.
 * 
 * Todos los juegos tienen la misma mecánica: hay una cuenta atrás durante la
 * cual se van haciendo preguntas. Cuando el usuario acierta una pregunta, se
 * debe llamar a computeCorrectQuestion() para que se actualice la puntuación;
 * cuando falle, se debe llamar a computeIncorrectQuestion() para que penalice
 * al jugador. La idea es evitar que probar valores al azar sea una estrategia
 * ventajosa en el juego.
 * 
 * Para que este fragmento funcione, el fragmento debe tener definido un panel
 * para la información del juego llamado game_info_panel, que debe ser incluido
 * en el layout utilizando esta orden:
 * 
 * <include android:id="@+id/game_info_panel"
 * android:layout_width="match_parent" android:layout_height="wrap_content"
 * layout="@layout/game_info_panel" />
 * 
 * En este panel se mostrará la información del juego (tiempo restante,
 * puntuación y nivel de dificultad) mientras se esté jugando. Cuando se pulse
 * el botón de jugar, se mostrará panel y empezará la cuenta atrás. Cuando se
 * llegue a cero, se acabará el juego.
 * 
 * Un fragmento con un ejercicio puede hacer estas cosas:
 * 
 * - Redefinir el método playGame() para empezar una partida. Aquí, típicamente,
 * se cambiará el layout para el modo juego y se generará la primera pregunta.
 * 
 * - Redefinir el método stopGame() para hacer las acciones que se deseen cuando
 * se acabe la partida.
 * 
 * - Redefinir el método cancelGame(), que se llama cuando el usuario cancela el
 * juego, para hacer las acciones que se deseen.
 * 
 * En estros tres métodos se debe llamar siempre al método padre en
 * BaseExerciseFragment, ya que son los que controlan el comportamiento del
 * juego.
 * 
 * También se puede llamar en el constructor del fragmento al método
 * setGameDuration() si se desea una duración distinta de los dos minutos que se
 * tienen por defecto.
 */
public abstract class BaseExerciseFragment extends Fragment {
	private static final String STATE_IS_PLAYING = "mIsPlaying";
	private static final String STATE_CONSUMED_TIME_MS = "consumedTimeMs";
	private static final String STATE_DURATION_TIME_MS = "mDurationTimeMs";
	private static final String STATE_SCORE = "mScore";

	private final static int CLOCK_UPDATE_PERIOD_MS = 1000; // 1 s
	private final static int DEFAULT_GAME_DURATION_MS = 60 * 1000; // 1 min

	private static final int POINTS_PER_CORRECT_QUESTION = 5;
	private static final int PENALIZATION_PER_INCORRECT_QUESTION = 2;
	private static final int INITIAL_SCORE = 0;

	protected boolean mIsPlaying = false;
	private int mScore = 0;

	private Handler mTimerHandler = new Handler();
	private Runnable mUpdateTimeTask = new TimeUpdater();
	private TextView mClock;
	private TextView mScoreTextView;
	private TextView mLevelTextView;
	private View mGameInfoPanel;
	private long mDurationMs = DEFAULT_GAME_DURATION_MS;
	private long mStartMs;

	private AnticipateOvershootInterpolator mAntovershoot;

	private View mResult;
	private ImageView mResultImage;

	interface ScoreListener {
		void onNewScore(int score);
	}

	private ScoreListener mScoreListener = null;

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		mResult = view.findViewById(R.id.result);
		mResultImage = view.findViewById(R.id.resultimage);
		mClock = view.findViewById(R.id.text_view_clock);
		mScoreTextView = view.findViewById(R.id.text_view_score);
		mLevelTextView = view.findViewById(R.id.text_view_level);

		mGameInfoPanel = view.findViewById(R.id.game_info_panel);
		if (mGameInfoPanel != null) {
			mGameInfoPanel.setVisibility(View.GONE);
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			return;
		}
		mIsPlaying = savedInstanceState.getBoolean(STATE_IS_PLAYING);

		if (mIsPlaying) {
			setGameInfoPanelVisibility(View.VISIBLE);
			mScore = savedInstanceState.getInt(STATE_SCORE);
			updateScore();
			showLevel();

			mDurationMs = savedInstanceState.getLong(STATE_DURATION_TIME_MS,
					DEFAULT_GAME_DURATION_MS);
			long consumedTime = savedInstanceState.getLong(
					STATE_CONSUMED_TIME_MS, mDurationMs);
			mStartMs = System.currentTimeMillis() - consumedTime;
			printRemainingTime();
			mTimerHandler.postDelayed(mUpdateTimeTask, CLOCK_UPDATE_PERIOD_MS);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_IS_PLAYING, mIsPlaying);
		outState.putLong(STATE_CONSUMED_TIME_MS,
				mDurationMs - getRemainingTimeMs());
		outState.putLong(STATE_DURATION_TIME_MS, mDurationMs);
		outState.putInt(STATE_SCORE, mScore);
	}

	@Override
	public void onStart() {
		super.onStart();
		analyticsTrack(associatedExercise().toString());
	}

	private void analyticsTrack(String screenName) {
		Tracker t = ((FcrTrainerApplication) getActivity().getApplication())
				.getTracker();

		t.setScreenName(screenName);

		// Send a screen view.
		t.send(new HitBuilders.ScreenViewBuilder().build());
	}

	/**
	 * Get remaining time in ms.
	 * 
	 * @return long
	 */
	protected long getRemainingTimeMs(){
		long nowMs = System.currentTimeMillis();
		return mDurationMs - (nowMs - mStartMs);
	}

	private final class TimeUpdater implements Runnable {
		public void run() {
			mTimerHandler.removeCallbacks(mUpdateTimeTask);

			printRemainingTime();

			if (getRemainingTimeMs() > 0) {
				mTimerHandler.postDelayed(mUpdateTimeTask,
						CLOCK_UPDATE_PERIOD_MS);
			} else {
				endGame();
			}
		}

	}

	private void printRemainingTime() {
		long remainingTimeMs = getRemainingTimeMs();
		int remainingTimeSec = (int) (remainingTimeMs / 1000);
		int remainingTimeMin = remainingTimeSec / 60;
		remainingTimeSec = remainingTimeSec % 60;

		mClock.setText(String.format("%d:%02d", remainingTimeMin,
				remainingTimeSec));
	}

	public BaseExerciseFragment() {
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.game_control, menu);
		MenuItem item = menu.findItem(R.id.action_change_game_mode);
		try {
			MainActivity activity = (MainActivity) getActivity();
			if (item != null && activity != null) {
				item.setVisible(!activity.isDrawerOpen());
			}
		} catch (ClassCastException ex) {
			// El fragmento está incrustado en una actividad distinta a
			// MainActivity. No se hace nada
		}

		MenuItem setup = menu.findItem(R.id.action_settings);
		if (mIsPlaying) {
			item.setIcon(R.drawable.ic_action_stop);
			setup.setVisible(false);
		} else {
			item.setIcon(R.drawable.ic_action_play);
			setup.setVisible(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mIsPlaying) {
			mTimerHandler.removeCallbacks(mUpdateTimeTask);
		}
	}

	private void setGameInfoPanelVisibility(int visibility) {
		if (mGameInfoPanel != null) {
			mGameInfoPanel.setVisibility(visibility);
		}
	}

	protected void setGameDuration(long durationMs) {
		mDurationMs = durationMs;
	}

	/**
	 * Esta función comienza el juego. En BaseExercise se hace visible el reloj,
	 * se lanza la tarea que lo actualiza cada segundo y se inicializa la
	 * puntuación. Si se quiere cambiar la duración del juego, se debe llamar
	 * antes a setGameDuration(). Las clases derivadas deben redifinirla,
	 * llamando al padre, para ñadir lo necesario a cada juego particular
	 */
	protected void startGame() {
		if (mClock == null) {
			Toast.makeText(getActivity(), getString(R.string.error_no_clock),
					Toast.LENGTH_LONG).show();
			return;
		}

		mScore = INITIAL_SCORE;
		updateScore();
		showLevel();

		getActivity().invalidateOptionsMenu(); // to hide the settings action

		mIsPlaying = true;
		mClock.setText(getString(R.string.game_on));
		setGameInfoPanelVisibility(View.VISIBLE);
		getActivity().invalidateOptionsMenu();
		mStartMs = System.currentTimeMillis();
		mTimerHandler.postDelayed(mUpdateTimeTask, CLOCK_UPDATE_PERIOD_MS);
	}

	private void showLevel() {
		if (mLevelTextView == null) {
			return;
		}
		
		mLevelTextView.setText(level().toString(getActivity()));
	}

	private void updateScore() {
		mScoreTextView.setText(getString(R.string.score) + " "
				+ String.valueOf(mScore));
	}

	protected void computeCorrectQuestion() {
		mScore += POINTS_PER_CORRECT_QUESTION;
		updateScore();
	}

	protected void computeIncorrectQuestion() {
		mScore -= PENALIZATION_PER_INCORRECT_QUESTION;
		updateScore();
	}

	/**
	 * Esta función cancela el juego, parando y ocultando el reloj. Las clases
	 * derivadas deben redifinirla, llamando al padre, para añadir lo necesario
	 * a cada juego particular
	 */
	protected void cancelGame() {
		stopPlaying();
	}

	/**
	 * Esta función se llama al finalizar el juego, parando y ocultando el
	 * reloj y mostrando un mensaje de fin de juego que, por defecto, muestra
	 * la puntuación. Las clases derivadas deben redifinirla, llamando al
	 * padre, para añadir lo necesario a cada juego particular
	 */
	protected void endGame() {
		stopPlaying();
		saveScore();
		showEndGameFragment();
	}

    private void showEndGameFragment() {
	    EndGameFragment endGameFragment = EndGameFragment.newInstance(mScore, associatedExercise());
	    getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, endGameFragment, "ENDGAME")
                .addToBackStack(null)
                .commit();
    }


	private void stopPlaying() {
		if (mIsPlaying) {
			mIsPlaying = false;
			mTimerHandler.removeCallbacks(mUpdateTimeTask);
			setGameInfoPanelVisibility(View.GONE);
			getActivity().invalidateOptionsMenu();
		}
	}

	/**
	 * Shows an animation when the user taps on the check button.
	 * Currently requires a layout with the id result and an imageview
	 * with the id resultimage. The implementation of this views can be
	 * seen in fragment_hexadecimal.xml
	 * 
	 * @param correct if the answer is correct
	 */
	protected void showAnimationAnswer(boolean correct){
		// Fade in - fade out
		mResult.setVisibility(View.VISIBLE);
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(600);
		animation.setFillBefore(true);
		animation.setFillAfter(true);
		animation.setRepeatCount(Animation.RESTART);
		animation.setRepeatMode(Animation.REVERSE);
		mResult.startAnimation(animation);
		if(correct)
			mResultImage.setImageDrawable(getResources().getDrawable(R.drawable.correct));
		else mResultImage.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mResultImage.animate().setDuration(700).setInterpolator(mAntovershoot).scaleX(1.5f).scaleY(1.5f).withEndAction(new Runnable(){
                @Override
                public void run() {
                    // Back to its original size after the animation's end
                    mResultImage.animate().scaleX(1f).scaleY(1f);
                }
            });
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_change_game_mode:
			if (mIsPlaying) {
				cancelGame();
			} else {
				startGame();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * @return nivel en el que se está
	 */
	protected Level level() {
		return PreferenceUtils.getLevel(getActivity());
	}

	/***
	 * Debe devolver el objeto Screen de tipo ejercicio asociado con el fragmento concreto
	 */
	protected abstract Screen associatedExercise();

	/**
	 * Saves the score using the Highscore Manager. Also sends it too Google Play Games
	 *
	 */
	protected void saveScore() {
		String user = getString(R.string.default_user_name);

		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					mScore, associatedExercise(), new Date(), user, level());
		} catch (JSONException e) {
			Log.v(getClass().getSimpleName(), "Error when saving score");
		}

		mScoreListener.onNewScore(mScore);
	}

	public void setScoreListener(ScoreListener scoreListener) {
		mScoreListener = scoreListener;
	}


}
