package es.uniovi.imovil.fcrtrainer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BinaryOffsetExerciseFragment extends BaseExerciseFragment {
	
	public static BinaryOffsetExerciseFragment newInstance() {

		BinaryOffsetExerciseFragment fragment = new BinaryOffsetExerciseFragment();
		return fragment;
	}
	
	public BinaryOffsetExerciseFragment(){
		
	}

	
	private final int offset = 128;
	private final int max = 127;
	private final int min = 0;
	private int number=0,solvedNumber=0;
	private boolean isBinary,isNext=false;
	private  TextView numberToConvertView;
	private TextView title;
	private  EditText answerField;
	private Button btnCheck;
	private Button btnToggle;
	private Button btnSolution;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_binaryoffset, container,
				false);
		
		numberToConvertView = (TextView) rootView.findViewById(R.id.numbertoconvert);
		answerField = (EditText) rootView.findViewById(R.id.answer);
		title = (TextView) rootView.findViewById(R.id.exercisetitle);
		btnCheck = (Button) rootView.findViewById(R.id.btn_check);
		btnSolution = (Button) rootView.findViewById(R.id.btn_getsolution);
		btnToggle = (Button) rootView.findViewById(R.id.btn_togglebinary);
		
		
		generateRdmNumber();
		numberToConvertView.setText(Integer.toBinaryString(number));
		isBinary = true;

		
		btnCheck.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
					if(isNext){
						
						generateRdmNumber();
						
						if(isBinary){
							numberToConvertView.setText(Integer.toBinaryString(number));
							btnCheck.setText(R.string.check);
						}else{
							numberToConvertView.setText(Integer.toString(number));
							btnCheck.setText(R.string.check);
						}
						isNext = !(isNext);
						
					}else if(isBinary){		
						
						getDecimalFromBinaryOffsetRepresentation();						
						
						if(answerField.getText().toString().equals(Integer.toString(solvedNumber))){
							showAnimationAnswer(true);
							generateRdmNumber();
							numberToConvertView.setText(Integer.toBinaryString(number));
						}else{
							showAnimationAnswer(false);							
						}
												
					}else{
						
						getBinaryOffsetRepresentationFromDecimal();										
												
						if(answerField.getText().toString().equals(Integer.toBinaryString(solvedNumber))){
							showAnimationAnswer(true);
							generateRdmNumber();
							numberToConvertView.setText(Integer.toString(number));

						}else{
							showAnimationAnswer(false);
						}
					}
			}
		});
		
		btnSolution.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isBinary){
					getDecimalFromBinaryOffsetRepresentation();
					answerField.setText(Integer.toString(solvedNumber));
					btnCheck.setText(R.string.next_binary);
				}else{
					getBinaryOffsetRepresentationFromDecimal();
					answerField.setText(Integer.toBinaryString(solvedNumber));
					btnCheck.setText(R.string.next_binary);					
				}			
				isNext = !(isNext);
			}
		});
		
		btnToggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				generateRdmNumber();
				if(isBinary){
					title.setText(R.string.convert_to_dec);
					numberToConvertView.setText(Integer.toString(number));
				}else{
					title.setText(R.string.convert_to_bin_offset);
					numberToConvertView.setText(Integer.toBinaryString(number));
				}
				isBinary = !(isBinary);
				
			}
		});
		
		
		
		return rootView;
	}
	
	public void generateRdmNumber(){
		
		number = min + (int)(Math.random() * ((max - min) + 1));
		
		}
		
		public void getBinaryOffsetRepresentationFromDecimal(){
			solvedNumber = number + offset;
			
		}
		
		public void getDecimalFromBinaryOffsetRepresentation(){
			 solvedNumber = number - offset;
		}
}
