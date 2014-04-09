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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
 * Para que esto funcione, el fragmento debe tener definido un TextView llamado
 * text_view_clock, que debe ser inicialmente invisible. Este TextView servirá
 * para mostrar el reloj mientras se esté jugando. Cuando se pulse el botón de
 * jugar, se mostrará el reloj y empezará la cuenta atrás. Cuando se llegue a
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
 * También se puede llamar en el constructor del fragmento al método
 * setGameDuration() si se desea una duración distinta de los dos minutos que se
 * tienen por defecto.
 */
public abstract class BaseExerciseFragment extends Fragment {

	private final static int CLOCK_UPDATE_PERIOD_MS = 1000; // 1 s
	private final static int DEFAULT_GAME_DURATION_MS = 120 * 1000; // 2 min

	boolean mIsPlaying = false;

	private Handler mTimerHandler = new Handler();
	private Runnable mUpdateTimeTask = new TimeUpdater();
	private TextView mClock;
	private long mDurationMs = DEFAULT_GAME_DURATION_MS;
	private long mStartMs;
	
	private AlphaAnimation animation;
	private AnticipateOvershootInterpolator antovershoot;
	
	private View result;
	private ImageView resultImage;

	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		result = (View) view.findViewById(R.id.result);
		resultImage = (ImageView) view.findViewById(R.id.resultimage);
		super.onViewCreated(view, savedInstanceState);
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
				mClock.setTextColor(Color.BLACK);
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
		if (mIsPlaying) {
			item.setIcon(R.drawable.ic_action_stop);
		} else {
			item.setIcon(R.drawable.ic_action_play);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mIsPlaying) {
			cancelGame();
		}
	}

	private void setClockVisibility(int visibility) {
		if (mClock != null) {
			mClock.setVisibility(visibility);
		}
	}

	void setGameDuration(long durationMs) {
		mDurationMs = durationMs;
	}

	/**
	 * Esta función comienza el juego. En BaseExercise simplemente se hace
	 * visible el reloj y se lanza la tarea que lo actualiza cada segundo. Si se
	 * quiere cambiar la duración del juego, se debe llamar antes a
	 * setGameDuration(). Las clases derivadas deben redifinirla, llamando al
	 * padre, para ñadir lo necesario a cada juego particular
	 */
	void startGame() {
		mClock = (TextView) getView().findViewById(R.id.text_view_clock);
		if (mClock == null) {
			Toast.makeText(getActivity(), getString(R.string.error_no_clock),
					Toast.LENGTH_LONG).show();
			return;
		}

		mIsPlaying = true;
		mStartMs = System.currentTimeMillis();
		setClockVisibility(View.VISIBLE);
		getActivity().supportInvalidateOptionsMenu();

		final long updateTime = 0; // Hacer la primera actualización
									// inmediatamente
		mTimerHandler.postDelayed(mUpdateTimeTask, updateTime);
	}

	/**
	 * Esta función cancela el juego, parando y ocultando el reloj. Las clases
	 * derivadas deben redifinirla, llamando al padre, para añadir lo necesario
	 * a cada juego particular
	 */
	void cancelGame() {
		stopPlaying();
	}

	/**
	 * Esta función se llama al finalizar el juego, parando y ocultando el
	 * reloj. Las clases derivadas deben redifinirla, llamando al padre, para
	 * añadir lo necesario a cada juego particular
	 */
	void endGame() {
		stopPlaying();
	}

	private void stopPlaying() {
		if (mIsPlaying) {
			mIsPlaying = false;
			mTimerHandler.removeCallbacks(mUpdateTimeTask);
			setClockVisibility(View.GONE);
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
		result.setVisibility(View.VISIBLE);
		animation = new AlphaAnimation(0,1);
		animation.setDuration(600);
		animation.setFillBefore(true);
		animation.setFillAfter(true);
		animation.setRepeatCount(Animation.RESTART);
		animation.setRepeatMode(Animation.REVERSE);
		result.startAnimation(animation);
		
		if(correct) resultImage.setImageDrawable(getResources().getDrawable(R.drawable.correct));
		else resultImage.setImageDrawable(getResources().getDrawable(R.drawable.incorrect));

		// This only works in API 12+, so we skip this animation on old devices
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			resultImage.animate().setDuration(700).setInterpolator(antovershoot).scaleX(1.5f).scaleY(1.5f).withEndAction(new Runnable(){
				@Override
				public void run() {
					// Back to its original size after the animation's end
					resultImage.animate().scaleX(1f).scaleY(1f);
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
}
