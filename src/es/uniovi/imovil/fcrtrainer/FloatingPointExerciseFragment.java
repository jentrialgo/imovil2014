package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;
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
	int minX = -50; 
	int maxX = 50; 
	int minY = 0;
	int maxY = 7;
	int finalX = 0;
	int fraction_helper = 0;
	float fraction_helper2 = 0.0f;
	int x = 0;
	int mantissa = 0; // global var for decimaltoIEE
	int s= 0;    // signbit
	int realexp = 0; // exponent
	int mlastOne = 0;
	String todo;
	
	
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
		Check = (Button) rootView.findViewById(R.id.btn_check);
		Solution= (Button) rootView.findViewById(R.id.btn_getsolution);
		Toggle = (Button) rootView.findViewById(R.id.btn_togglebinary);
		
	
		
		Toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			//generateRandomNumbers();
			
			if(isBinary)
			{
				float f = -18.4f;
				
				int fAsBinary = Float.floatToIntBits(f); 
				
				
				int sign = ((fAsBinary &  0x80000000) >> 31) & 0x1;
				int exp = (fAsBinary &  0x7f800000) >> 23;
				int mantissa = (fAsBinary &  0x007fffff); 
				
				Sign.setText(Integer.toBinaryString(sign)); 
				Exponent.setText(Integer.toBinaryString(exp)); 
				Mantissa.setText(Integer.toBinaryString(mantissa));
				
				Values.setText(Integer.toBinaryString(fAsBinary));
				
//				DecimalToBinary();
//				setResultSeperate();
				isBinary=false;
			} else
			{
//				DecimalToBinary();
//				setResultIEE();
				
				  float f = 43.0f;
				  int fAsBinary = Float.floatToIntBits(f); 
				  
				  	int sign = ((fAsBinary &  0x80000000) >> 31) & 0x1;
					int exp = (fAsBinary &  0x7f800000) >> 23;
					int mantissa = (fAsBinary &  0x007fffff); 
					
					Sign.setText(Integer.toBinaryString(sign)); 
					Exponent.setText(Integer.toBinaryString(exp)); 
					Mantissa.setText(Integer.toBinaryString(mantissa));
					
					Values.setText(Integer.toBinaryString(fAsBinary));
					
//				Sign.setText(Integer.toString(s)); 
//				Exponent.setText(Integer.toString(realexp)); 
//				Mantissa.setText(Integer.toString(mantissa).substring(1)); 
				isBinary=true;
			}
			
			}});


		
	return rootView;
}
	
	public void generateRandomNumbers(){
		
	// Random numbers in range of: 0-50
	Random rand = new Random(); 
	finalX = rand.nextInt(maxX - minX)  + minX;
	
	// Random numbers in range of: 0-15
	Random rand_helper = new Random(); 
	fraction_helper = rand_helper.nextInt(maxY - minY) + minY;
	fraction_helper2 = fraction_helper * 0.125f + finalX;
	// fraction helper2 = random(0-15)*0.125 + random(0-50)

	
	}
		

		
		public void DecimalToBinary(){
			
			int exp;	  
			int excess = 0;
			int j = 0;		// counter var for EXP
			
			
			// Check: -x? & set Signbit
			if (fraction_helper2<0)
			  {
				s=1;
				fraction_helper2 = fraction_helper2*-1;
			  }
			else s=0;
			
			exp = (int)fraction_helper2;
			
			float nachkommateil = fraction_helper2 - exp;
			
			mantissa = 0;
			
			// Value of the real Exponent
			// Excess + Exponent
			
			//Excess : 2^(n-1) - 1
			excess = 127;
			
			
			// integer value of the float: Fraction_helper2
			realexp = (int) fraction_helper2;  
			
			
			do {
				if (realexp == 0)
					break;
				realexp = realexp / 2;
				j++;
			} while (realexp > 0);
			// Find out the Exponent
			
			realexp = excess + j-1;
			// Exp = 127 + the number of 2^j
			
			
			mantissa = (int)fraction_helper2;
			
						
			for(int i=1;i<24;i++)
			{
				
				if(mantissa == 0)
					break;
				// If the fraction part is 0.0 then break the loop
				
				nachkommateil = nachkommateil*2;
				// * 2 the fraction: e.g. 0.3*2 = 0.6
				
				if(nachkommateil<1)
				{
					mantissa = mantissa * 2;
					// When its lower 1 -> leftshift mantissa: 100[0]
					
				} else if(nachkommateil>1)
				{
					mantissa = mantissa * 2 + 1 ;
					nachkommateil = nachkommateil - 1;
					// if its greater then: reduce nachkommateil by 1 and : mantissa: 100[1]
					
				} else if(nachkommateil==1)
				{
					mantissa = mantissa * 2 + 1;
					nachkommateil = nachkommateil - 1;
					// if we have nachkommateil = 1 -> break and mantissa like nachkommateil >1 
					break;
				}
			}
			
//			mlastOne = 0;
//		
//				mlastOne = Integer.toBinaryString(mantissa).lastIndexOf("1");
//			
		}
		
		// routine for setting the result into a string
		public void setResultIEE(){
			todo = Integer.toBinaryString(s) + " " + Integer.toBinaryString(realexp) + " " + Integer.toBinaryString(mantissa).substring(1);
		}
		
		// routine for setting the result in the editText boxes
		public void setResultSeperate(){
						// Set the results in the editboxes
						Sign.setText(Integer.toBinaryString(s)); 
						Exponent.setText(Integer.toBinaryString(realexp)); 
						if (mantissa ==0){
							Mantissa.setText("");
						}
						else {
							Mantissa.setText(Integer.toBinaryString(mantissa).substring(1));
						}
						
		}
	}


