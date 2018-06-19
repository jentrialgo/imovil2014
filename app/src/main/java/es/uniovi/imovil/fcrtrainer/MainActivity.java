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

import java.util.ArrayList;

import com.google.android.gms.analytics.GoogleAnalytics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import es.uniovi.imovil.fcrtrainer.SectionedDrawerAdapter.Group;

public class MainActivity extends AppCompatActivity implements
		ListView.OnItemClickListener {

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

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private int mExerciseResIndex;
	private boolean mUserLearnedDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mTitle = getTitle();
		mDrawerTitle = mTitle;
		boolean fromSavedInstanceState = false;

		if (savedInstanceState != null) {
			// Recuperar el estado tras una interrupción
			mExerciseResIndex = savedInstanceState.getInt(LAST_EXERCISE);
			mUserLearnedDrawer = savedInstanceState
					.getBoolean(USER_LEARNED_DRAWER);
			fromSavedInstanceState = true;
		} else {
			// Restaurar el estado desde las preferencias
			SharedPreferences prefs = getSharedPreferences(PREFERENCES,
					Context.MODE_PRIVATE);
			mExerciseResIndex = prefs.getInt(LAST_EXERCISE, R.string.binary);
			mUserLearnedDrawer = prefs.getBoolean(USER_LEARNED_DRAWER, false);
		}

		// Cargo el fragmento con el contenido

		if (savedInstanceState == null)
			updateContentFragment();

		initializeDrawer(fromSavedInstanceState);		
	}

	@Override
	public void onPause() {
		super.onPause();

		// Guardar las preferencias
		SharedPreferences prefs = getSharedPreferences(PREFERENCES,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putInt(LAST_EXERCISE, mExerciseResIndex);
		prefsEditor.putBoolean(USER_LEARNED_DRAWER, mUserLearnedDrawer);
		prefsEditor.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Guardar el estado de la actividad
		savedInstanceState.putInt(LAST_EXERCISE, mExerciseResIndex);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int newExerciseIndex = (Integer) parent.getItemAtPosition(position);
		if (newExerciseIndex != mExerciseResIndex) {
			// Cambiar el fragmento de contenido actual
			mExerciseResIndex = newExerciseIndex;
			updateContentFragment();
			mTitle = getString(mExerciseResIndex);
		}
		// Cerrar el Drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mDrawerList);
	}
	
	private void updateContentFragment() {
		Fragment fragment = FragmentFactory
				.createExercise(mExerciseResIndex);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();

		fragmentTransaction.replace(R.id.content_frame, fragment, "Hola");
		fragmentTransaction.commit();
	}

	private void initializeDrawer(boolean fromSavedState) {
		// Contenido organizado en secciones
		ArrayList<Group<String, Integer>> sections = createDrawerEntries();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new SectionedDrawerAdapter(this,
				R.layout.drawer_list_item, R.layout.drawer_list_header,
				sections));

		// Listener
		mDrawerList.setOnItemClickListener(this);

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
		mTitle = getString(mExerciseResIndex);
		if (!mUserLearnedDrawer && !fromSavedState) {
			mDrawerLayout.openDrawer(mDrawerList);
			actionBar.setTitle(mDrawerTitle);
		} else {			
			setTitle(mTitle);
		}

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private ArrayList<Group<String, Integer>> createDrawerEntries() {
		ArrayList<Group<String, Integer>> sections = new ArrayList<Group<String,
				Integer>>();

		addSection(sections, R.string.codes, R.array.codes);
		addSection(sections, R.string.digital_systems, R.array.digital_systems);
		addSection(sections, R.string.networks, R.array.networks);
		addSection(sections, R.string.highscores, R.array.highscores);

		return sections;
	}

	private void addSection(ArrayList<Group<String, Integer>> sections,
			int sectionNameId, int childrenArrayId) {
		Group<String, Integer> group;
		group = new Group<String, Integer>(getString(sectionNameId));

		TypedArray array = getResources().obtainTypedArray(childrenArrayId);

		group.children = new Integer[array.length()];
		for (int i = 0; i < array.length(); i++) {
			int defaultId = 0;
			group.children[i] = array.getResourceId(i, defaultId);
		}
		
		array.recycle();
		
		sections.add(group);
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
}
