package es.uniovi.imovil.fcrtrainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProtocolDataBaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final String TAG = null;
	
	private final Context mContext;
	private SQLiteDatabase mDataBase;
	private File mDatabaseFile;

	public ProtocolDataBaseHelper(Context context, String nombre,
			CursorFactory factory, int version) {
		super(context, nombre, factory, version);
		mContext = context;
		mDatabaseFile = mContext.getDatabasePath(DB_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase dataBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();

		if (dbExist) {
			return;
		}

		try {
			copyDataBase();
		} catch (IOException e) {
			throw new Error("Error al copiar la database");
		}
	}

	private boolean checkDataBase() {
		boolean checkdb = false;
		try {
			String myPath = mContext.getDatabasePath(DB_NAME).getAbsolutePath();
			
			File dbfile = new File(myPath);
			checkdb = dbfile.exists();
		} catch (SQLiteException e) {
			System.out.println("Database doesn't exist");
		}

		return checkdb;
	}

	private void copyDataBase() throws IOException {	
		// Create database folder
		File parent = mDatabaseFile.getParentFile();
		if (!parent.exists()) {
		    parent.mkdir();
		}

		OutputStream databaseOutputStream = new FileOutputStream(
				mDatabaseFile.getAbsolutePath());
		InputStream databaseInputStream;
		Log.v(TAG, "Copiando base de datos");
		byte[] buffer = new byte[1024];
		int length;

		databaseInputStream = mContext.getAssets().open("protocolFCR.sqlite");
		while ((length = databaseInputStream.read(buffer)) > 0) {
			databaseOutputStream.write(buffer);
		}

		databaseInputStream.close();
		databaseOutputStream.flush();
		databaseOutputStream.close();
	}

	public void openDataBase() throws SQLException {
		mDataBase = SQLiteDatabase.openDatabase(
				mDatabaseFile.getAbsolutePath(), null,
				SQLiteDatabase.OPEN_READONLY);
		Log.v(TAG, "Abriendo base de datos");
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();

		super.close();
	}

	public ArrayList<ProtocolTest> loadData() {
		ArrayList<ProtocolTest> testList = new ArrayList<ProtocolTest>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		try {
			cursor = db.rawQuery("SELECT * FROM questions", null);
		} catch (SQLException sql) {
			db.close();
			Log.v(TAG, "Error en DatabaseHelper. Problemas al cargar los test");
			return null;
		}
		while (cursor.moveToNext()) {
			ProtocolTest test = new ProtocolTest(cursor.getString(cursor
					.getColumnIndex("pregunta")), cursor.getString(cursor
					.getColumnIndex("respuesta1")), cursor.getString(cursor
					.getColumnIndex("respuesta2")), cursor.getString(cursor
					.getColumnIndex("respuesta3")), cursor.getString(cursor
					.getColumnIndex("respuesta4")), cursor.getString(cursor
					.getColumnIndex("solucion")));
			testList.add(test);
		}
		db.close();
		return testList;
	}
}
