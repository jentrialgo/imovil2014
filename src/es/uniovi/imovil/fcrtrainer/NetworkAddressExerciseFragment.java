package es.uniovi.imovil.fcrtrainer;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkAddressExerciseFragment extends BaseExerciseFragment implements View.OnClickListener {
	
	View rootView;
	int n;
	int i;
	String[] ip;
	String[] mask;
	String[] net;
	TextView tvi;
	TextView tvm;
	TextView tv1;
	TextView tv2;
	EditText et;
	ImageView imageviewsolution;
	Button banswer;
	Button bsolution;
	Handler handler;
	

	public static NetworkAddressExerciseFragment newInstance() {
		
		NetworkAddressExerciseFragment fragment = new NetworkAddressExerciseFragment();
		return fragment;
	}
	
	public NetworkAddressExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_network_address, container, false);
		
		ip=getResources().getStringArray(R.array.ips);
		mask=getResources().getStringArray(R.array.masks);
		net=getResources().getStringArray(R.array.nets);
		n=ip.length;
		i=0;
		tvi = (TextView) rootView.findViewById(R.id.tv_ip);
		tvm = (TextView) rootView.findViewById(R.id.tv_mask);
		tv1 = (TextView) rootView.findViewById(R.id.st_tv_ip);
		tv2 = (TextView) rootView.findViewById(R.id.st_tv_mask);
		
		et = (EditText) rootView.findViewById(R.id.et_net);
		imageviewsolution=(ImageView) rootView.findViewById(R.id.image_network_address_solution);
		
		tvi.setText(ip[i]);
		tvm.setText(mask[i]);
		
		banswer = (Button) rootView.findViewById(R.id.but_ans);
		banswer.setOnClickListener(this);
		
		bsolution = (Button) rootView.findViewById(R.id.but_solution);
		bsolution.setOnClickListener(this);
		
		handler= new Handler();
		
		return rootView;
		
		
	}
	
	
	@Override
	public void onClick(View v){
		if (v.getId() == R.id.but_ans){
			checkAnswer();
		}
		else if (v.getId() == R.id.but_solution){
			solutionNetworkAddress();
		}
		
	}
	
	/*public void showFailureDialog(){

		//Se crea el alert dialog que mostrara dos botones, uno de comprobar y otro para volver a intentarlo
		
		final AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.failed_logic_gate);
		alertDialog.setMessage(R.string.dialog_try_logic_gate);
		alertDialog.setCancelable(true);
		alertDialog.setPositiveButton(R.string.try_again_logic_gate,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//et.setText("");
				dialog.cancel();
			}
		});
		alertDialog.setNegativeButton(R.string.solution_logic_gate,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				et.setText(net[i]);
				
			}
		});

		AlertDialog alert11 = alertDialog.create();
		alert11.show();
	}*/
	
	public void checkAnswer(){
		if(net[i].equals(et.getText().toString())){
				et.setTextColor(Color.GREEN);
	        	imageviewsolution.setImageResource(R.drawable.correct); 
	        	if(i<net.length-1){	
				 handler.postDelayed(new Runnable() {
			            public void run() {
			            	imageviewsolution.setImageResource(0);  
			            	i++;
			    			et.setTextColor(Color.BLACK);
			    			tvi.setText(ip[i]);
			    			tvm.setText(mask[i]);
			    			et.setText("");
			    			
			            }
			        }, 1500);
			}	
			else{
				 handler.postDelayed(new Runnable() {
			            public void run() {
			            	imageviewsolution.setImageResource(0);  
			            	banswer.setVisibility(Button.GONE);
							bsolution.setVisibility(Button.GONE);
							et.setVisibility(EditText.GONE);
							tvi.setVisibility(TextView.GONE);
							tvm.setVisibility(TextView.GONE);
							tv1.setVisibility(TextView.GONE);
							tv2.setText(R.string.end_train);
							tv2.setTextSize(47);
							imageviewsolution.setVisibility(ImageView.GONE);
			            }
			        }, 1500);
				
				
			}
			
		}
		else {
			et.setTextColor(Color.RED);
			imageviewsolution.setImageResource(R.drawable.incorrect);
			 handler.postDelayed(new Runnable() {
		            public void run() {
		            	et.setTextColor(Color.BLACK);
		    			imageviewsolution.setImageResource(0);
		            }
		        }, 1500);


	}
	}
	
	public void solutionNetworkAddress(){
		et.setTextColor(Color.BLACK);
		et.setText(net[i]);
	}
}


