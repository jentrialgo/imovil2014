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
			CompruebaRespuesta();

		}

	}
	
	public void showFailureDialog(){

		//Se crea el alert dialog que mostrara dos botones, uno de comprobar y otro para volver a intentarlo
		
		final AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.failed_logic_gate);
		alertDialog.setMessage(R.string.dialog_try_logic_gate);
		alertDialog.setCancelable(true);
		alertDialog.setPositiveButton(R.string.try_again_logic_gate,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				edit.setText("");
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton(R.string.solution_logic_gate,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				edit.setText(logicstring[contador]);
				
			}
		});

		AlertDialog alert11 = alertDialog.create();
		alert11.show();
	}


public void CompruebaRespuesta(){
	//Buscamos el id del textID y cogemos el valor que tiene en ese momento y
	//lo ponemos en mayúsculas para compararlo

	edit=(EditText) rootView.findViewById(R.id.edit);
	Editable texto= edit.getText();
	String textos= texto.toString().toUpperCase();

	/*Se comprueba si lo que hay en la posicion del string fijada por el contodar es igual a
	lo que hay dentro del edittext*/

	if(logicstring[contador].equals(textos)){
		
		/*Si el contador es menor que la longitud del string, se aumenta el contador y
		 * se muestra la siguiente imagen
		 */
		
		if(contador<logicstring.length-1){
			contador++;
			logicgate.setText(logicstring[contador]);
			edit.setText("");
			imageview.setImageResource(arrayimage.getResourceId(contador, 0));
		}
		
		// Si no, cuando ya se ha recorrido el string, se pone invisible todo el layout
		//y solo se muestra un texto de que se ha acabado
		
		else {
			imageview.setVisibility(ImageView.GONE);
			logicgate.setVisibility(TextView.VISIBLE);
			buttoncheck.setVisibility(Button.GONE);
			edit.setVisibility(EditText.GONE);
			logicgate.setText(R.string.done_logic_gate);
			arrayimage.recycle();
		}
	}
	
	//Si no es igual es texto del string con el del editText, se muestra un Alert Dialog
	
	else {
		showFailureDialog();


	}
	}
}
