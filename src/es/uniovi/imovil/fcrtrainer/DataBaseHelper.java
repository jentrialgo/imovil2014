package es.uniovi.imovil.fcrtrainer;
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

public class DataBaseHelper  extends SQLiteOpenHelper{

	private static final String DB_PATH = "/data/data/es.uniovi.imovil.fcrtrainer/databases/";
	private static final String DB_NAME = "protocolFCR.sqlite";
	private static final String TAG = null;
	private final Context myContext;
	private SQLiteDatabase myDataBase;

	public DataBaseHelper(Context context, String nombre, CursorFactory factory, int version) 
	{
        super(context, nombre, factory, 1);
        this.myContext=context;
    }

	@Override
	public void onCreate(SQLiteDatabase dataBase) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
    public void createDataBase() throws IOException {
    	 
        boolean dbExist = checkDataBase();
 
        if (dbExist) {
            // Si existe, no  se hace nada.
        }
        else 
        {
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema de nuestra aplicación por lo que
            // podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();
             try
             {
                 copyDataBase();     
             } 
             catch (IOException e) 
             {     
                throw new Error("Error al copiar la database");
             }
        }
    }

	private boolean checkDataBase() {
		// TODO Auto-generated method stub
        SQLiteDatabase checkDB = null;        
        try 
        {
            String myPath = DB_PATH + DB_NAME; //Path de la BD + nombre de la misma.
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); //Solo lectura.
 
        } catch (SQLiteException e) {
        	Log.v(TAG,"Fallo. Base de datos no creada aun");
        }
 
        if (checkDB != null) //hay DB
        { 
            checkDB.close();
            return true;
        }
        else return false; //checkDB es null.
	}

	private void copyDataBase() throws IOException 
	{
		// TODO Auto-generated method stub
        OutputStream databaseOutputStream = new FileOutputStream("" + DB_PATH + DB_NAME);
        InputStream databaseInputStream;
        Log.v(TAG,"Copiando base de datos");
        byte[] buffer = new byte[1024];
        int length;
 
        databaseInputStream = myContext.getAssets().open("protocolFCR.sqlite");
        while ((length = databaseInputStream.read(buffer)) > 0) {
            databaseOutputStream.write(buffer);
        }
 
        databaseInputStream.close();
        databaseOutputStream.flush();
        databaseOutputStream.close();
	}
	
    public void openDataBase() throws SQLException {
    	 
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        Log.v(TAG,"Abriendo base de datos");
    }
	
	   @Override
	    public synchronized void close() {
	 
	        if (myDataBase != null)
	            myDataBase.close();
	 
	        super.close();
	    }

	public ArrayList<Test> loadData() 
	{
		// TODO Auto-generated method stub
		ArrayList <Test> testList = new ArrayList <Test>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor;
		try
		{
			cursor = db.rawQuery("SELECT * FROM questions",null);
		}
        catch (SQLException sql)
        {
            db.close();
            Log.v(TAG,"Error en DatabaseHelper. Problemas al cargar los test");
        	return null;
        }
		while (cursor.moveToNext())
		{
			Test test= new Test(cursor.getString(cursor.getColumnIndex("pregunta")),
					cursor.getString(cursor.getColumnIndex("respuesta1")),
					cursor.getString(cursor.getColumnIndex("respuesta2")),
					cursor.getString(cursor.getColumnIndex("respuesta3")),
					cursor.getString(cursor.getColumnIndex("solucion")));
			testList.add(test);
		}
		db.close();
		return testList;
	}
}
