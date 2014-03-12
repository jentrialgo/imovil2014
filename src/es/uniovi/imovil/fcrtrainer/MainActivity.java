package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ListView;
import es.uniovi.imovil.fcrtrainer.SectionedDrawerAdapter.Group;

public class MainActivity extends ActionBarActivity implements
		ListView.OnItemClickListener {

	/**
	 * Nombre del fichero de preferencias.
	 */
	private static final String PREFERENCES = "preferences";
	/**
	 * Preferencia donde se almacena el ïúltimo ejercicio accedido.
	 */
	private static final String LAST_EXERCISE = "last_exercise";
	/**
	 * Preferencia que indica que el usuario sabe manejar el drawer. La guía
	 * de Android recomienda mostrar el Drawer abierto hasta que el usuario lo
	 * haya desplegado al menos una vez.
	 */
	private static final String USER_LEARNED_DRAWER = "user_learned_drawer";

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private int mExerciseIndex;
	private boolean mUserLearnedDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = getTitle();
		mDrawerTitle = mTitle;
		boolean fromSavedInstanceState = false;

		if (savedInstanceState != null) {
			// Recuperar el estado tras una interrupción
			mExerciseIndex = savedInstanceState.getInt(LAST_EXERCISE);
			mUserLearnedDrawer = savedInstanceState
					.getBoolean(USER_LEARNED_DRAWER);
			fromSavedInstanceState = true;
		} else {
			// Restaurar el estado desde las preferencias
			SharedPreferences prefs = getSharedPreferences(PREFERENCES,
					Context.MODE_PRIVATE);
			mExerciseIndex = prefs.getInt(LAST_EXERCISE,
					ExerciseFragmentFactory.BINARY_EXERCISE_INDEX);
			mUserLearnedDrawer = prefs.getBoolean(USER_LEARNED_DRAWER, false);
		}

		// Cargo el fragmento con el contenido
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
		prefsEditor.putInt(LAST_EXERCISE, mExerciseIndex);
		prefsEditor.putBoolean(USER_LEARNED_DRAWER, mUserLearnedDrawer);
		prefsEditor.commit();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Guardar el estado de la actividad
		savedInstanceState.putInt(LAST_EXERCISE, mExerciseIndex);
		savedInstanceState.putBoolean(USER_LEARNED_DRAWER, mUserLearnedDrawer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflar el menú
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
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
		// TODO: Manejar otras acciones del Action Bar

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
		if (position != mExerciseIndex) {
			// Cambiar el fragmento de contenido actual
			mExerciseIndex = position;
			updateContentFragment();
			mTitle = ((TextView) view.findViewById(R.id.entry_text)).getText();
		}
		// Cerrar el Drawer
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void updateContentFragment() {
		BaseExerciseFragment fragment = ExerciseFragmentFactory
				.createExercise(mExerciseIndex);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragment);
		fragmentTransaction.commit();
	}

	private void initializeDrawer(boolean fromSavedState) {
		// Contenido organizado en secciones
		ArrayList<Group<String, String>> sections = createDrawerEntries();
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
				R.drawable.ic_drawer, // El icono del drawer
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
		if (!mUserLearnedDrawer && !fromSavedState) {
			mDrawerLayout.openDrawer(mDrawerList);
			actionBar.setTitle(mDrawerTitle);
		} else {
			mTitle = (String) mDrawerList.getAdapter().getItem(mExerciseIndex);
			setTitle(mTitle);
		}

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private ArrayList<Group<String, String>> createDrawerEntries() {
		ArrayList<Group<String, String>> sections = new ArrayList<Group<String, String>>();

		addSection(sections, R.string.codes, R.array.codes);
		addSection(sections, R.string.digital_systems, R.array.digital_systems);
		addSection(sections, R.string.networks, R.array.networks);

		return sections;
	}

	private void addSection(ArrayList<Group<String, String>> sections, int sectionNameId,
			int childrenArrayId) {
		Group<String, String> group;
		group = new Group<String, String>(getString(sectionNameId));
		group.children = getResources().getStringArray(childrenArrayId);
		sections.add(group);
	}
}
