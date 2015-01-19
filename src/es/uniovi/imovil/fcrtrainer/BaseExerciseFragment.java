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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
 * está un botón en la barra de tareas para empezar y parar de jugar.
 * 
 * Para que esto funcione, el fragmento debe tener definido un panel para la
 * información del juego llamado game_info_panel, que debe ser incluido en el
 * layout utilizando esta orden:
 *  <include
        android:id="@+id/game_info_panel"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        layout="@layout/game_info_panel" />
 * 
 * En este se mostrará la información del juego (tiempo restante, puntuación y
 * nivel de dificultad) mientras se esté jugando. Cuando se pulse el botón de
 * jugar, se mostrará panel y empezará la cuenta atrás. Cuando se llegue a
 * cero, se acabará el juego.
 * 
 * Un fragmento con un ejercicio puede hacer estas cosas:
 * 
 * - Redefinir el método playGame() para empezar una partida. Aquí, típicamente,
 * se cambiará el layout para el modo juego y se generará la primera pregunta.
 * 
 * - Redefinir el método stopGame() para hacer las acciones que se deseen cuando
 * se acabe la partida, como por ejemplo, mostrar un mensaje con la puntuación y
 * llamar a HighScoreManager.addScore().
 * 
 * - Redefinir el método cancelGame(), que se llama cuando el usuario cancela el
 * juego, para hacer las acciones que se deseen.
 * 
 * En estros tres métodos se debe llamar siempre al método padre en
 * BaseExercise, ya que son los que controlan el comportamiento del juego.
 * 
 * Se debe llamar al método updateScore() cada vez que cambie la puntuación
 * 
 * También se puede llamar en el constructor del fragmento al método
 * setGameDuration() si se desea una duración distinta de los dos minutos que se
 * tienen por defecto.
 */
public abstract class BaseExerciseFragment extends Fragment {

	private final static int CLOCK_UPDATE_PERIOD_MS = 1000; // 1 s
	private final static int DEFAULT_GAME_DURATION_MS = 120 * 1000; // 2 min
	protected boolean mIsPlaying = false;

	private Handler mTimerHandler = new Handler();
	private Runnable mUpdateTimeTask = new TimeUpdater();
	private TextView mClock;
	private TextView mScoreTextView;
	private TextView mLevelTextView;
	private View mGameInfoPanel;
	private long mDurationMs = DEFAULT_GAME_DURATION_MS;
	private long mStartMs;

	private AlphaAnimation mAnimation;
	private AnticipateOvershootInterpolator mAntovershoot;

	private View mResult;
	private ImageView mResultImage;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mResult = (View) view.findViewById(R.id.result);
		mResultImage = (ImageView) view.findViewById(R.id.resultimage);
		mClock = (TextView) getView().findViewById(R.id.text_view_clock);
		mScoreTextView = (TextView) view.findViewById(R.id.text_view_score);
		mLevelTextView = (TextView) view.findViewById(R.id.text_view_level);

		mGameInfoPanel = view.findViewById(R.id.game_info_panel);
		if (mGameInfoPanel != null) {
			mGameInfoPanel.setVisibility(View.GONE);
		}

		super.onViewCreated(view, savedInstanceState);
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

			long nowMs = System.currentTimeMillis();
			long remainingTimeMs = mDurationMs - (nowMs - mStartMs);
			int remainingTimeSec = (int) (remainingTimeMs / 1000);
			int remainingTimeMin = remainingTimeSec / 60;
			remainingTimeSec = remainingTimeSec % 60;

			final long tenSecondsInMs = 10000;
			if (remainingTimeMs < tenSecondsInMs) {
				mClock.setTextColor(Color.RED);
			} else {
				mClock.setTextColor(getActivity().getResources().getColor(
						R.color.score_info));
			}

			mClock.setText(String.format("%d:%02d", remainingTimeMin,
					remainingTimeSec));

			if (remainingTimeMin > 0 || remainingTimeSec > 0) {
				mTimerHandler.postDelayed(mUpdateTimeTask,
						CLOCK_UPDATE_PERIOD_MS);
			} else {
				endGame();
			}
		}

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
			item.setVisible(!activity.isDrawerOpen());
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
			cancelGame();
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
	 * Esta función comienza el juego. En BaseExercise simplemente se hace
	 * visible el reloj y se lanza la tarea que lo actualiza cada segundo. Si se
	 * quiere cambiar la duración del juego, se debe llamar antes a
	 * setGameDuration(). Las clases derivadas deben redifinirla, llamando al
	 * padre, para ñadir lo necesario a cada juego particular
	 */
	protected void startGame() {
		if (mClock == null) {
			Toast.makeText(getActivity(), getString(R.string.error_no_clock),
					Toast.LENGTH_LONG).show();
			return;
		}

		updateScore(0);
		showLevel();

		getActivity().supportInvalidateOptionsMenu(); // to hide the settings action

		mIsPlaying = true;
		setGameInfoPanelVisibility(View.VISIBLE);
		getActivity().supportInvalidateOptionsMenu();
		showAnimationGameStart(true);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mStartMs = System.currentTimeMillis();
				final long updateTime = 0; // Hacer la primera actualización
				// inmediatamente
				mTimerHandler.postDelayed(mUpdateTimeTask, updateTime);
			}
		}, 1500);


	}

	private void showLevel() {
		if (mLevelTextView == null) {
			return;
		}
		
		mLevelTextView.setText(level().toString(getActivity()));
	}

	/***
	 * Esta función muestra el valor que se le pase en el panel de puntuación
	 */
	protected void updateScore(int newScore) {
		mScoreTextView.setText(getString(R.string.score) + " "
				+ String.valueOf(newScore));
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
	 * reloj. Las clases derivadas deben redifinirla, llamando al padre, para
	 * añadir lo necesario a cada juego particular
	 */
	protected void endGame() {
		stopPlaying();
	}

	private void stopPlaying() {
		if (mIsPlaying) {
			mIsPlaying = false;
			mTimerHandler.removeCallbacks(mUpdateTimeTask);
			setGameInfoPanelVisibility(View.GONE);
			getActivity().supportInvalidateOptionsMenu();
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
	@SuppressLint("NewApi") protected void showAnimationAnswer(boolean correct){ 
		// Fade in - fade out
		mResult.setVisibility(View.VISIBLE);
		mAnimation = new AlphaAnimation(0,1);
		mAnimation.setDuration(600);
		mAnimation.setFillBefore(true);
		mAnimation.setFillAfter(true);
		mAnimation.setRepeatCount(Animation.RESTART);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mResult.startAnimation(mAnimation);
		if(correct)
			mResultImage.setImageDrawable(getResources().getDrawable(R.drawable.correct));
		else mResultImage.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));

		// This only works in API 12+, so we skip this animation on old devices
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
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
	
	@SuppressLint("NewApi") protected void showAnimationGameStart(boolean correct){ 
		// Fade in - fade out
		mResult.setVisibility(View.VISIBLE);
		mAnimation = new AlphaAnimation(0,1);
		mAnimation.setDuration(1000);
		mAnimation.setFillBefore(true);
		mAnimation.setFillAfter(true);
		mAnimation.setRepeatCount(Animation.RESTART);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mResult.startAnimation(mAnimation);
		if(correct)
			mResultImage.setImageDrawable(getResources().getDrawable(R.drawable.game_start));


		// This only works in API 12+, so we skip this animation on old devices
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			mResultImage.animate().setDuration(1100).setInterpolator(mAntovershoot).scaleX(1.5f).scaleY(1.5f).withEndAction(new Runnable(){
				@Override
				public void run() {
					// Back to its original size after the animation's end
					mResultImage.animate().scaleX(1f).scaleY(1f);
				}
			});
		}
	}
	
	/**
	 * @return nivel en el que se está
	 */
	protected Level level() {
		return PreferenceUtils.getLevel(getActivity());
	}
	
}
