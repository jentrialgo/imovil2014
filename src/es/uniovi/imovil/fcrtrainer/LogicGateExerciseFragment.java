
package es.uniovi.imovil.fcrtrainer;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LogicGateExerciseFragment extends BaseExerciseFragment  implements OnClickListener{
	Button buttoncheck;
	String [] logicstring;
	View rootView;
	TextView logicgate;
	int contador;
	int[] myImageList;
	TypedArray arrayimage;
	ImageView imageview;
	EditText edit;

	public static LogicGateExerciseFragment newInstance() {

		LogicGateExerciseFragment fragment = new LogicGateExerciseFragment();
		return fragment;
	}

	public LogicGateExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inicializamos la variable contador con el fin de recorrer el array con las diferentes puertas 
		//logicas
		contador=0;
		rootView = inflater.inflate(R.layout.fragment_logic_gate, container, false);
		logicgate = (TextView) rootView.findViewById(R.id.logic_gate);

		//Cargamos el array con las puertas logicas
		logicstring= getResources().getStringArray(R.array.logic_gates);
		buttoncheck=(Button) rootView.findViewById(R.id.buttonlogicgate);
		buttoncheck.setOnClickListener(this);
		logicgate.setText(logicstring[0]);
		//Cargamos un array con las imagenes de las puertas logicas
		arrayimage = getResources().obtainTypedArray(R.array.logic_gates_images);
		imageview=(ImageView) rootView.findViewById(R.id.imagelogicgate);


		return rootView;
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.buttonlogicgate){

			//Buscamos el id del textID y cogemos el valor que tiene en ese momento y
			//lo ponemos en mayúsculas para compararlo

			edit=(EditText) rootView.findViewById(R.id.edit);
			Editable texto= edit.getText();
			String textos= texto.toString().toUpperCase();

			//Si lo que hay en el edittext es igual a la posicion del array de strings en la posicion
			//definida por el contador, se aumenta el contador y se cambia la imagen y el string al de 
			//la siguiente posición

			if(logicstring[contador].equals(textos)){
				if(contador<logicstring.length-1){
					contador++;
					logicgate.setText(logicstring[contador]);
					edit.setText("");
					imageview.setImageResource(arrayimage.getResourceId(contador, 0));
				}
				else {
					imageview.setVisibility(ImageView.GONE);
					logicgate.setVisibility(TextView.VISIBLE);
					buttoncheck.setVisibility(Button.GONE);
					edit.setVisibility(EditText.GONE);
					logicgate.setText("Has acabado");
					arrayimage.recycle();
				}
			}
			
			else {
				dialog();


			}
		}

	}
	public void dialog(){

		final AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("¡Has fallado!");
		alertDialog.setMessage("¿Quieres probar de nuevo o bien quieres saber la solución?");
		alertDialog.setCancelable(true);
		alertDialog.setPositiveButton("Reintentar",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				edit.setText("");
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton("Solución",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				edit.setText(logicstring[contador]);
				
			}
		});

		AlertDialog alert11 = alertDialog.create();
		alert11.show();
	}
}
