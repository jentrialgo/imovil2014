package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.R.string;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;


public class FloatingPointExerciseFragment extends BaseExerciseFragment {
	

	private TextView Values;
	private TextView Iee_binary;
	private TextView Tv_Decimal;
	private TextView Tv_Sign;
	private TextView Tv_Exponent;
	private TextView Tv_Mantissa;
	private TextView Tv_Points;
	private EditText Decimal;
	private EditText Sign;
	private EditText Exponent;
	private EditText Mantissa;
	private EditText Response;
	private Button Check;
	private Button Toggle;
	private Button Solution;
	boolean isBinary = true;
	boolean convert = true;
	boolean game = false;
	int minX = -50; 
	int maxX = 50; 
	int minY = 0;
	int maxY = 15;
	int finalX = 0;
	int fraction_helper = 0;
	float decimal_value_f = 0.0f;
	int x = 0;
	int mantissa;
	int sign,exp;
	int s= 0;    // signbit
	int realexp = 0; // exponent
	int mlastOne = 0;
	int pointsCounter = 0;
	String ComparisonString, CheckString;
	String mantissaAsString;
	String shortMantissa;
	
	int fAsIntBits;
	String fAsBinaryString;
//	String fAsBinaryStringPos;
//	String fAsBinaryStringDel;
//	String fAsBinaryStringPosDel;
	String bitRepresentationDel;
	String bitRepresentation;
	String bitRepresentationDivided;

	private static final int GAMEMODE_MAXQUESTIONS = 5;
	
	
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
    	Tv_Decimal = (TextView) rootView.findViewById(R.id.tv_decimal);
    	Tv_Sign = (TextView) rootView.findViewById(R.id.tv_s);
    	Tv_Exponent = (TextView) rootView.findViewById(R.id.tv_exp);
    	Tv_Mantissa = (TextView) rootView.findViewById(R.id.tv_mant);
    	Tv_Points = (TextView) rootView.findViewById(R.id.tv_points);
     	Iee_binary = (TextView) rootView.findViewById(R.id.theme);
     	Decimal = (EditText) rootView.findViewById(R.id.ed_decimal);
		Sign = (EditText) rootView.findViewById(R.id.ed_sign);
		Exponent = (EditText) rootView.findViewById(R.id.ed_exponent);
		Mantissa = (EditText) rootView.findViewById(R.id.ed_mantissa);
		Check = (Button) rootView.findViewById(R.id.btn_check);
		Solution= (Button) rootView.findViewById(R.id.btn_getsolution);
		Toggle = (Button) rootView.findViewById(R.id.btn_togglebinary);
		
		Sign.setText(null);
		Exponent.setText(null);
		Mantissa.setText(null);
		
		// OnCreate
		generateRandomNumbers();
		RemoveZeroes();
		Values.setText(Float.toString(decimal_value_f));
		isBinary = false;
		
		fAsIntBits = Float.floatToRawIntBits(decimal_value_f); 
		Decimal.setVisibility(View.GONE);
		Tv_Decimal.setVisibility(View.GONE);
		
		bitRepresentationDivided = bitRepresentationDel.substring(0, 1) + " " + 
				  bitRepresentationDel.substring(1, 9) + " " +
				  bitRepresentationDel.substring(9);
		
		
			Check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Check: EditText Fields empty?
//				if ((Sign.getEditableText().toString() != "") &
//				    (Exponent.getEditableText().toString() != "") &
//				    (Mantissa.getEditableText().toString() != ""))
				
					RemoveZeroes();
					
					if (isBinary == true){
						
						
//						CheckString = Integer.toString(sign) + Integer.toString(exp)
//										+ Integer.toString(mantissa);
						
						//CheckString = Integer.toString(sign) + Integer.toString(exp)
						//		+ shortMantissa;
					
						
						ComparisonString = Decimal.getEditableText().toString().trim();
						 
						
						if (ComparisonString.equals(Float.toString(decimal_value_f)))
						{
							showAnimationAnswer(true);
							if (game)
								updateGameState();
							generateRandomNumbers();
							RemoveZeroes();
							
							bitRepresentationDivided = bitRepresentationDel.substring(0, 1) + " " + 
									  bitRepresentationDel.substring(1, 9) + " " +
									  bitRepresentationDel.substring(9);

							Values.setText(bitRepresentationDivided);
							Decimal.setText(null);
							
						} else 
							showAnimationAnswer(false);
						
								
					} else {
						
//						CheckString = Integer.toBinaryString(sign) + Integer.toBinaryString(exp)
//								+ Integer.toBinaryString(mantissa);
//					
//					
//						ComparisonString = Sign.getText().toString() + 
//								   Exponent.getText().toString() +
//								   Mantissa.getText().toString();
						
					
						
					//	CheckString = Integer.toBinaryString(sign) + Integer.toBinaryString(exp)
						//		+ shortMantissa;
						
				
				ComparisonString = Sign.getEditableText().toString().trim() + 
						   		   Exponent.getEditableText().toString().trim() +
						   		   Mantissa.getEditableText().toString().trim();	
				
				if (bitRepresentationDel.equals(ComparisonString) || bitRepresentation.equals(ComparisonString)) 
				{
					showAnimationAnswer(true);
					if (game)
						updateGameState();
					generateRandomNumbers();
					RemoveZeroes();
					Values.setText(Float.toString(decimal_value_f));
					Sign.setText(null);
					Exponent.setText(null);
					Mantissa.setText(null);
					
				} else 
					showAnimationAnswer(false);		
				
					}

				
		
			}});

			Solution.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
				
				if(isBinary){
					Decimal.setText(Float.toString(decimal_value_f));
				}
				else {
					Sign.setText(bitRepresentationDel.substring(0, 1));
					Exponent.setText(bitRepresentationDel.substring(1, 9));
					Mantissa.setText(bitRepresentationDel.substring(9));
				}
		
				
			}});
			
		
		Toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
			generateRandomNumbers();
			RemoveZeroes();
			
			
			if(isBinary)
			{
//				float f = -18.4f;
				
				//int fAsIntBits = Float.floatToIntBits(decimal_value_f); 
				
				
//				Sign.setText(Integer.toBinaryString(sign)); 
//				Exponent.setText(Integer.toBinaryString(exp)); 
//				Mantissa.setText(Integer.toBinaryString(mantissa));
				
				
				
				
				Values.setText(Float.toString(decimal_value_f));
				Iee_binary.setText("Convierte a IEE 754/binario formato!");
				
				Decimal.setVisibility(View.GONE);
				Tv_Decimal.setVisibility(View.GONE);
				Sign.setVisibility(View.VISIBLE);
				Exponent.setVisibility(View.VISIBLE);
				Mantissa.setVisibility(View.VISIBLE);
				Tv_Sign.setVisibility(View.VISIBLE);
				Tv_Exponent.setVisibility(View.VISIBLE);
				Tv_Mantissa.setVisibility(View.VISIBLE);
				
				Sign.setText(null);
				Exponent.setText(null);
				Mantissa.setText(null);
				
//				DecimalToBinary();
//				setResultSeperate();
				isBinary=false;
			} else
			{
//				DecimalToBinary();
//				setResultIEE();
				
//				  float f = 43.0f;
				 
//			 	int sign = ((fAsIntBits &  0x80000000) >> 31) & 0x1;
//				int exp = (fAsIntBits &  0x7f800000) >> 23;
//				int mantissa = (fAsIntBits &  0x007fffff); 
			
					
//					Sign.setText(Integer.toBinaryString(sign)); 
//					Exponent.setText(Integer.toBinaryString(exp)); 
//					Mantissa.setText(Integer.toBinaryString(mantissa));
					//fAsIntBits = Float.floatToIntBits(decimal_value_f); 
					
					bitRepresentationDivided = bitRepresentationDel.substring(0, 1) + " " + 
													  bitRepresentationDel.substring(1, 9) + " " +
													  bitRepresentationDel.substring(9);
				
					Values.setText(bitRepresentationDivided);
					
					Iee_binary.setText("Convierte a decimal formato!");
					
					Decimal.setVisibility(View.VISIBLE);
					Tv_Decimal.setVisibility(View.VISIBLE);
					Sign.setVisibility(View.GONE);
					Exponent.setVisibility(View.GONE);
					Mantissa.setVisibility(View.GONE);
					Tv_Sign.setVisibility(View.GONE);
					Tv_Exponent.setVisibility(View.GONE);
					Tv_Mantissa.setVisibility(View.GONE);
					
					Decimal.setText(null);
					
					
//				Sign.setText(Integer.toString(s)); 
//				Exponent.setText(Integer.toString(realexp)); 
//				Mantissa.setText(Integer.toString(mantissa).substring(1)); 
				isBinary=true;
			}
			
			}});


		
	return rootView;
}
	
//	public void isRight(String response) {
//		
//		if (isBinary) {
//			if (response.equals(Float.toString(decimal_value_f))) {
//				showAnimationAnswer(true);
////				if (game)
////					updateGameState();
//				generateRandomNumbers();
//			} else
//				showAnimationAnswer(false);
//		} else {
//			if (response.equals(fAsIntBits)) {
//				showAnimationAnswer(true);
////				if (game)
////					updateGameState();
//				generateRandomNumbers();
//			} else
//				showAnimationAnswer(false);
//		}
//	}

//	public void showSolution() {
//		if (isBinary){
//			Sign.setText(fAsIntBits);
//			Exponent.setText(fAsIntBits);
//			Mantissa.setText(fAsIntBits);
//		}
//			
//		else
//			Sign.setText(Float.toString(decimal_value_f));
//			Exponent.setText(Float.toString(decimal_value_f));
//			Mantissa.setText(Float.toString(decimal_value_f));
//	}
	
	public void RemoveZeroes(){
		
	//mantissaAsString = Integer.toBinaryString(mantissa);
	 
	// Remove trailing zeroes
		 int lastSignificant = bitRepresentation.length() -1 ;
		 
		  while (bitRepresentation.charAt(lastSignificant) == '0') {
			    lastSignificant--;
			  }
			  lastSignificant++;

			  bitRepresentationDel = bitRepresentation.substring(0,
			      lastSignificant);
	  
	}
	
	public void generateRandomNumbers(){
		
	// Random numbers in range of: 0-50
	Random rand = new Random(); 
	finalX = rand.nextInt(maxX - minX)  + minX;
	
	// Random numbers in range of: 0-15
	Random rand_helper = new Random(); 
	fraction_helper = rand_helper.nextInt(maxY - minY) + minY;
	decimal_value_f = fraction_helper * 0.125f + finalX;
	// decimal_value_f = random(0-15)*0.125 + random(0-50)
	
	fAsIntBits = Float.floatToRawIntBits(decimal_value_f); 
	
	fAsBinaryString = Integer.toBinaryString(fAsIntBits);
	
	// IMPORTANT: Representation of the float number in binary form
	bitRepresentation = String.format("%32s", fAsBinaryString).replace(' ', '0');
	
//		if(decimal_value_f > 0)
//		fAsBinaryStringPos = "0" + fAsBinaryString;
	
	 sign = ((fAsIntBits &  0x80000000) >> 31) & 0x1;
	 exp = (fAsIntBits &  0x7f800000) >> 23;
	 mantissa = (fAsIntBits &  0x007fffff); 

	
	}
	
	
	
	/**
	 * Prepares the layout for the training and game mode.
	 * 
	 * @param training
	 *            true if the change is to the training mode
	 */
	public void setTrainingMode(boolean training) {
		if (training) {
			game = false;
			Solution.setVisibility(View.VISIBLE);
			Tv_Points.setVisibility(View.GONE);
		} else {
			game = true;
			resetGameState();
			Solution.setVisibility(View.GONE);
			Tv_Points.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Updates the game stats and if all the questions have been asked it calls
	 * the endGame() method.
	 */
	public void updateGameState() {
		pointsCounter++;
		updatePoints(pointsCounter);
		if (pointsCounter == GAMEMODE_MAXQUESTIONS)
			endGame();
	}

	public void resetGameState() {
		pointsCounter = 0;
		Tv_Points.setVisibility(View.GONE);
	}

	/**
	 * Updates the points in the UI
	 * @param points
	 */
	public void updatePoints(int points) {
		Tv_Points.setText(getString(R.string.points) + String.valueOf(points));
	}

	/**
	 * Starts the game and sets the UI
	 */
	@Override
	void startGame() {
		super.startGame();
		setTrainingMode(false);
		updatePoints(pointsCounter);
	}

	/**
	 * Called when the user cancels the game
	 */
	@Override
	void cancelGame() {
		super.cancelGame();
		setTrainingMode(true);
	}

	/**
	 * Called when the game ends
	 */
	@Override
	void endGame() {
		super.endGame();

		int remainingTime = (int) getRemainingTimeMs() / 1000;
		pointsCounter = pointsCounter + remainingTime;

		showEndGameDialog(remainingTime);

		saveScore(pointsCounter);
		setTrainingMode(true);
	}

	/**
	 * Shows a dialog with the game stats when the game is over
	 * 
	 * @param remainingTime
	 */
	public void showEndGameDialog(int remainingTime) {
		AlertDialog.Builder abuilder = new AlertDialog.Builder(getActivity());
		abuilder.setTitle(getString(R.string.game_over));

		if (remainingTime > 0)
			abuilder.setMessage(String.format(
					getString(R.string.gameisoverexp), remainingTime,
					pointsCounter));
		else
			abuilder.setMessage(String.format(
					getString(R.string.lost_time_over), pointsCounter));

		abuilder.create().show();
	}

	/**
	 * Saves the score using the Highscore Manager.
	 * 
	 * @param points
	 */
	public void saveScore(int points) {
		String user = getString(R.string.default_user_name);

		try {
			HighscoreManager.addScore(getActivity().getApplicationContext(),
					points, R.string.hexadecimal, new Date(), user);
		} catch (JSONException e) {
			Log.v(getClass().getSimpleName(), "Error when saving score");
		}
	}

		
//		public void DecimalToBinary(){
//			
//			int exp;	  
//			int excess = 0;
//			int j = 0;		// counter var for EXP
//			
//			
//			// Check: -x? & set Signbit
//			if (decimal_value_f<0)
//			  {
//				s=1;
//				decimal_value_f = decimal_value_f*-1;
//			  }
//			else s=0;
//			
//			exp = (int)decimal_value_f;
//			
//			float nachkommateil = decimal_value_f - exp;
//			
//			mantissa = 0;
//			
//			// Value of the real Exponent
//			// Excess + Exponent
//			
//			//Excess : 2^(n-1) - 1
//			excess = 127;
//			
//			
//			// integer value of the float: decimal_value_f
//			realexp = (int) decimal_value_f;  
//			
//			
//			do {
//				if (realexp == 0)
//					break;
//				realexp = realexp / 2;
//				j++;
//			} while (realexp > 0);
//			// Find out the Exponent
//			
//			realexp = excess + j-1;
//			// Exp = 127 + the number of 2^j
//			
//			
//			mantissa = (int)decimal_value_f;
//			
//						
//			for(int i=1;i<24;i++)
//			{
//				
//				if(mantissa == 0)
//					break;
//				// If the fraction part is 0.0 then break the loop
//				
//				nachkommateil = nachkommateil*2;
//				// * 2 the fraction: e.g. 0.3*2 = 0.6
//				
//				if(nachkommateil<1)
//				{
//					mantissa = mantissa * 2;
//					// When its lower 1 -> leftshift mantissa: 100[0]
//					
//				} else if(nachkommateil>1)
//				{
//					mantissa = mantissa * 2 + 1 ;
//					nachkommateil = nachkommateil - 1;
//					// if its greater then: reduce nachkommateil by 1 and : mantissa: 100[1]
//					
//				} else if(nachkommateil==1)
//				{
//					mantissa = mantissa * 2 + 1;
//					nachkommateil = nachkommateil - 1;
//					// if we have nachkommateil = 1 -> break and mantissa like nachkommateil >1 
//					break;
//				}
//			}
//			
////			mlastOne = 0;
////		
////				mlastOne = Integer.toBinaryString(mantissa).lastIndexOf("1");
////			
//		}
		
		
//		// routine for setting the result in the editText boxes
//		public void setResultSeperate(){
//						// Set the results in the editboxes
//						Sign.setText(Integer.toBinaryString(s)); 
//						Exponent.setText(Integer.toBinaryString(realexp)); 
//						if (mantissa ==0){
//							Mantissa.setText("");
//						}
//						else {
//							Mantissa.setText(Integer.toBinaryString(mantissa).substring(1));
//						}
//						
//		}
	}


