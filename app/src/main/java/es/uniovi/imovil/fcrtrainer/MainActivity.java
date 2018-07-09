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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
		NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnCompleteListener<Player>, BaseExerciseFragment.ScoreListener {
	private static final String TAG = "MainActivity";

	/**
	 * Nombre del fichero de preferencias.
	 */
	private static final String PREFERENCES = "preferences";
	/**
	 * Preferencia donde se almacena el último ejercicio accedido.
	 */
	private static final String LAST_EXERCISE = "last_exercise";
	/**
	 * Preferencia que indica que el usuario sabe manejar el drawer. La guía de
	 * Android recomienda mostrar el Drawer abierto hasta que el usuario lo haya
	 * desplegado al menos una vez.
	 */
	private static final String USER_LEARNED_DRAWER = "user_learned_drawer";

	// request codes we use when invoking an external activity
	private static final int RC_UNUSED = 5001;
	private static final int RC_SIGN_IN = 9001;

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private Screen mExercise;
	private boolean mUserLearnedDrawer;

	private GoogleSignInClient mGoogleSignInClient;
	private LeaderboardsClient mLeaderboardsClient;
	private PlayersClient mPlayersClient;
    private Button mSignInButton;
	private TextView mUserName;

    private class AccomplishmentsOutbox {
        int mScore = -1;

        boolean isEmpty() {
            return mScore < 0;
        }
    }

    // Achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    private final AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mTitle = getTitle();
		mDrawerTitle = mTitle;
		boolean fromSavedInstanceState = false;

		int exerciseOrdinal;
		if (savedInstanceState != null) {
			// Recuperar el estado tras una interrupción
			exerciseOrdinal= savedInstanceState.getInt(LAST_EXERCISE);
			mUserLearnedDrawer = savedInstanceState.getBoolean(USER_LEARNED_DRAWER);
			fromSavedInstanceState = true;
		} else {
			// Restaurar el estado desde las preferencias
			SharedPreferences prefs = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
			exerciseOrdinal = prefs.getInt(LAST_EXERCISE, Screen.BINARY.ordinal());
			mUserLearnedDrawer = prefs.getBoolean(USER_LEARNED_DRAWER, false);
		}

		if (exerciseOrdinal >= Screen.values().length) {
			// Esto puede ocurrir cuando cambia el número de ejercicios en una actualización
			exerciseOrdinal = Screen.BINARY.ordinal();
		}

		mExercise = Screen.values()[exerciseOrdinal];

		// Cargo el fragmento con el contenido
		if (savedInstanceState == null)
			updateContentFragment();

		initializeDrawer(fromSavedInstanceState);

		mGoogleSignInClient = GoogleSignIn.getClient(this,
				new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
	}

	private boolean isSignedIn() {
		return GoogleSignIn.getLastSignedInAccount(this) != null;
	}

	private void signInSilently() {
		mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
				new OnCompleteListener<GoogleSignInAccount>() {
					@Override
					public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
						if (task.isSuccessful()) {
							Log.d(TAG, "signInSilently(): success");
							onConnected(task.getResult());
						} else {
							Log.d(TAG, "signInSilently(): failure", task.getException());
							onDisconnected();
						}
					}
				});
	}

	private void startSignInIntent() {
		startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		Log.d(TAG, "onActivityResult()");

		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task =
					GoogleSignIn.getSignedInAccountFromIntent(intent);

			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				onConnected(account);
			} catch (ApiException apiException) {
				String message = apiException.getMessage();
				if (message == null || message.isEmpty()) {
					message = getString(R.string.signin_other_error);
				}

				onDisconnected();

				new AlertDialog.Builder(this)
						.setMessage(message)
						.setNeutralButton(android.R.string.ok, null)
						.show();
			}
		}
	}

	private void onConnected(GoogleSignInAccount googleSignInAccount) {
		Log.d(TAG, "onConnected(): connected to Google APIs");

		//mAchievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
		mLeaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);
		//mEventsClient = Games.getEventsClient(this, googleSignInAccount);
		mPlayersClient = Games.getPlayersClient(this, googleSignInAccount);

		// Show sign-out button on main menu
		//mMainMenuFragment.setShowSignInButton(false);

		// Show "you are signed in" message on win screen, with no sign in button.
		//mWinFragment.setShowSignInButton(false);
        mSignInButton.setText("Desconectar");

		// Set the greeting appropriately on main menu
		mPlayersClient.getCurrentPlayer().addOnCompleteListener(this);


		// If we have accomplishments to push, push them
		if (!mOutbox.isEmpty()) {
			pushAccomplishments();
		}

		/*
		loadAndPrintEvents();
		*/
	}

	private void onDisconnected() {
		Log.d(TAG, "onDisconnected()");

		//mAchievementsClient = null;
		mLeaderboardsClient = null;
		mPlayersClient = null;

		// Show sign-in button on main menu
		//mMainMenuFragment.setShowSignInButton(true);

		// Show sign-in button on win screen
		//mWinFragment.setShowSignInButton(true);
		mSignInButton.setText(R.string.sign_in);
		mUserName.setText(R.string.not_connected);

		//mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");

		// Since the state of the signed in user can change when the activity is not active
		// it is recommended to try and sign in silently from when the app resumes.
		signInSilently();
	}

	private void signOut() {
		Log.d(TAG, "signOut()");

		if (!isSignedIn()) {
			Log.w(TAG, "signOut() called, but was not signed in!");
			return;
		}

		mGoogleSignInClient.signOut().addOnCompleteListener(this,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						boolean successful = task.isSuccessful();
						Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));

						onDisconnected();
					}
				});
	}

	private void handleException(Exception e, String details) {
		int status = 0;

		if (e instanceof ApiException) {
			ApiException apiException = (ApiException) e;
			status = apiException.getStatusCode();
		}

		String message = getString(R.string.status_exception_error, details, status, e);

		new AlertDialog.Builder(MainActivity.this)
				.setMessage(message)
				.setNeutralButton(android.R.string.ok, null)
				.show();
	}

	@Override
	public void onPause() {
		super.onPause();

		// Guardar las preferencias
		SharedPreferences prefs = getSharedPreferences(PREFERENCES,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putInt(LAST_EXERCISE, mExercise.ordinal());
		prefsEditor.putBoolean(USER_LEARNED_DRAWER, mUserLearnedDrawer);
		prefsEditor.apply();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Guardar el estado de la actividad
		savedInstanceState.putInt(LAST_EXERCISE, mExercise.ordinal());
		savedInstanceState.putBoolean(USER_LEARNED_DRAWER, mUserLearnedDrawer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflar el menú		
		if (isDrawerOpen()) {
			// TODO: Si el Drawer está desplegado no deben mostrarse iconos de
			// acción
			getMenuInflater().inflate(R.menu.main, menu);
		} else {
			getMenuInflater().inflate(R.menu.main, menu);
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		switch(item.getItemId()){
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.action_help:
			Intent goToHelp = new Intent(this, HelpActivity.class);
			startActivity(goToHelp);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(GravityCompat.START);
	}
	
	private void updateContentFragment() {
		Fragment fragment = mExercise.toFragment();

		if (fragment instanceof BaseExerciseFragment) {
			((BaseExerciseFragment) fragment).setScoreListener(this);
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragment, "Hola");
		fragmentTransaction.commit();

	}

    private void showRemoteLeaderboards() {
    	if (!isSignedIn()) {
			Toast.makeText(this, getString(R.string.error_not_logged_in),
					Toast.LENGTH_LONG).show();
			return;
		}

        mLeaderboardsClient.getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_UNUSED);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(e, getString(R.string.leaderboards_exception));
                    }
                });
    }

    private void initializeDrawer(boolean fromSavedState) {
		mDrawerLayout = findViewById(R.id.drawer_layout);
		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		// Mostrar el icono del drawer
		final ActionBar actionBar = getSupportActionBar();
		mDrawerToggle = new ActionBarDrawerToggle(this, // Actividad que lo aloja
				mDrawerLayout, // El layout
				R.string.drawer_open, R.string.drawer_close) {

			// Se llama cuando el Drawer se acaba de cerrar
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				actionBar.setTitle(mTitle);
				// Actualizar las acciones en el Action Bar
				supportInvalidateOptionsMenu();
			}

			// Se llama cuando el Drawer se acaba de abrir
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				actionBar.setTitle(mDrawerTitle);
				// Actualizar las acciones en el Action Bar
				mUserLearnedDrawer = true;
				supportInvalidateOptionsMenu();
			}
		};

		// Si el usuario no ha desplegado alguna vez el Drawer
		mTitle = mExercise.toString();
		if (!mUserLearnedDrawer && !fromSavedState) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			actionBar.setTitle(mDrawerTitle);
		} else {			
			setTitle(mTitle);
		}

		mDrawerLayout.addDrawerListener(mDrawerToggle);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		View headerView = navigationView.getHeaderView(0);
        mSignInButton = headerView.findViewById(R.id.sign_in_button);
		mSignInButton.setOnClickListener(this);

		mUserName = headerView.findViewById(R.id.user_name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    	if (item.getItemId() == R.id.nav_remote_highscores) {
			// Remote leaderboards are handled with an Activity by Google Play Games
			showRemoteLeaderboards();
		} else {
			Screen screen = Screen.fromNavId(item.getItemId());

			mExercise = Screen.fromNavId(item.getItemId());
			mTitle = mExercise.toString();
			updateContentFragment();
		}

		mDrawerLayout.closeDrawer(GravityCompat.START);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_in_button:
				if (isSignedIn()) {
					signOut();
				} else {
					startSignInIntent();
				}
				break;
		}
	}

	@Override
	public void onComplete(@NonNull Task<Player> task) {
		String displayName;
		if (task.isSuccessful()) {
			displayName = task.getResult().getDisplayName();
		} else {
			Exception e = task.getException();
			handleException(e, getString(R.string.players_exception));
			displayName = "???";
		}

		mUserName.setText(displayName);
	}

    @Override
    public void onNewScore(int score) {
        mOutbox.mScore = score;
        pushAccomplishments();
    }

    private void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, try again later
            return;
        }

        if (mOutbox.mScore >= 0) {
            mLeaderboardsClient.submitScore(getString(R.string.leaderboard_binary__beginner),
                    mOutbox.mScore);
            mOutbox.mScore = -1;
        }
    }
}
