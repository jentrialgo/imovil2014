package es.uniovi.imovil.fcrtrainer;

import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;


public class FloatingPointExerciseFragment extends BaseExerciseFragment {
	

	private TextView Values;
	private EditText Sign;
	private EditText Exponent;
	private EditText Mantissa;
	private EditText Response;
	private Button Check;
	private Button Toggle;
	private Button Solution;
	boolean isBinary = true;
	float minX = 0.0f; 
	float maxX = 100.0f; 
	float finalX;
	
	public static FloatingPointExerciseFragment newInstance() {

		FloatingPointExerciseFragment fragment = new FloatingPointExerciseFragment();
		return fragment;
	}

	public FloatingPointExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_floatingpoint, container,
				false);

		
     	Values = (TextView) rootView.findViewById(R.id.converting_value);
		Sign = (EditText) rootView.findViewById(R.id.ed_solution1);
		Exponent = (EditText) rootView.findViewById(R.id.ed_solution2);
		Mantissa = (EditText) rootView.findViewById(R.id.ed_solution3);
		
		
		return rootView;

	}
	
		Check.setOnClickListener(new OnClickListener() {
			
		}
		
		
		Solution.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isBinary){
					getDecimalFromBinaryOffsetRepresentation();
					Response.setText(Integer.toString(solvedNumber));
					Check.setText(R.string.next_binary);
				}else{
					getBinaryOffsetRepresentationFromDecimal();
					Response.setText(Integer.toBinaryString(solvedNumber));
					Check.setText(R.string.next_binary);					
				}			
				isNext = !(isNext);
			}
		});
		
		Toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			generateRandomNumbers();
			
			if(isBinary)
			{
				//Toast.makeText(this, "New decimal value", Toast.LENGTH_SHORT).show();
				Values.setText(Float.toString(finalX).substring(0,Float.toString(finalX).length()-3));
				isBinary=false;
			} else
			{
			//	Toast.makeText(this, "New binary value", Toast.LENGTH_SHORT).show();
				Values.setText(Float.toHexString(finalX).substring(0,Float.toHexString(finalX).length()-3));
				isBinary=true;
			}
			}
			});

		
		public void generateRandomNumbers(){
		
		Random rand = new Random(); 
		finalX = rand.nextFloat() * (maxX - minX) + minX;
		}
		
		
		public void OnButtonClick (View view) 
		{
			
			if(view.getId() == R.id.btn_togglebinary)
			{
				generateRandomNumbers();
				
				if(isBinary)
				{
					//Toast.makeText(this, "New decimal value", Toast.LENGTH_SHORT).show();
					Values.setText(Float.toString(finalX).substring(0,Float.toString(finalX).length()-3));
					isBinary=false;
				} else
				{
				//	Toast.makeText(this, "New binary value", Toast.LENGTH_SHORT).show();
					Values.setText(Float.toHexString(finalX).substring(0,Float.toHexString(finalX).length()-3));
					isBinary=true;
				} 
				
			} 
			  else if (view.getId() == R.id.btn_getsolution)
			{ 
				  DecimalToBinary();
			}
	
		}
		
		public void DecimalToBinary(){
			float x;   // decimal value
			int exp;	   // 
			int s= 0;    // signbit
			int excess = 0;
			int realexp = 0;
			int j = 0;		// counter var for EXP
			
			x=-88.29f;		// Random Value -> X (editText)
			
			// Check: -x? & set Signbit
			if (x<0)
			  {
				s=1;
				x=x*-1;
			  }
			else s=0;
			
			
			exp = (int)x;
			
			float nachkommateil = x - exp;
			int mantissa = 0;
			
			// Value of the real Exponent
			// Excess + Exponent
			
			//Excess : 2^(n-1) - 1
			excess = 127;
			
			realexp = (int) x;  
			
			do {
				realexp = realexp / 2;
				j++;
			} while (realexp > 0);
			
			
			realexp = excess + j-1;
			
			mantissa = (int)x;
			
			for(int i=1;i<24;i++)
			{
				
				nachkommateil = nachkommateil*2;
				
				if(nachkommateil<1)
				{
					mantissa = mantissa * 2;
					
				} else if(nachkommateil>1)
				{
					mantissa = mantissa * 2 + 1 ;
					nachkommateil = nachkommateil - 1;
				} else if(nachkommateil==1)
				{
					mantissa = mantissa * 2 + 1;
					nachkommateil = nachkommateil - 1;
					break;
				}
			}
			
			
			// Set the results in the editboxes
			Sign.setText(Integer.toBinaryString(s)); 
			Exponent.setText(Integer.toBinaryString(mantissa).substring(1)); 
			Mantissa.setText(Integer.toBinaryString(realexp)); 
			
		}
	}


