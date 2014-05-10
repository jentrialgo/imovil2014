package es.uniovi.imovil.fcrtrainer;

import java.util.Date;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
	private boolean isBinary,isNext=false,isGame=false;
	private TextView numberToConvertView;
	private TextView title;
	private TextView points;
	private EditText answerField;
	private Button btnCheck;
	private Button btnToggle;
	private Button btnSolution;
	private int currentPoints = 0;
	private final int MAX_NUM_QUESTION=5;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView;
		rootView = inflater.inflate(R.layout.fragment_binaryoffset, container,
				false);

		numberToConvertView = (TextView) rootView.findViewById(R.id.numbertoconvert);
		title = (TextView) rootView.findViewById(R.id.exercisetitle);
		points = (TextView)rootView.findViewById(R.id.pnts);
		answerField = (EditText) rootView.findViewById(R.id.answer);
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

							if(isGame){
								currentPoints++;
								points.setText(getString(R.string.points) + currentPoints);

								if(currentPoints==MAX_NUM_QUESTION)
									endGame();
							}

						}else{
							showAnimationAnswer(false);							
						}

					}else{

						getBinaryOffsetRepresentationFromDecimal();										

						if(answerField.getText().toString().equals(Integer.toBinaryString(solvedNumber))){
							showAnimationAnswer(true);
							generateRdmNumber();
							numberToConvertView.setText(Integer.toString(number));

							if(isGame){					
								currentPoints++;
								points.setText(getString(R.string.points) + currentPoints);

								if(currentPoints==MAX_NUM_QUESTION)
									endGame();

							}

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
				if(isNext){
					btnCheck.setText(R.string.check);
					isNext = !(isNext);
				}
				generateRdmNumber();
				if(isBinary){
					title.setText(R.string.convert_to_bin_offset);
					numberToConvertView.setText(Integer.toString(number));
				}else{

					title.setText(R.string.convert_from_bo_to_dec);

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


	/* 							*
	 * 	Functions for the Game	*
	 * 							*/

		@Override
		void startGame() {
			super.startGame();
			setTrainingMode(false);
			points.setText(getString(R.string.points) + "0");
		}

		@Override
		void cancelGame() {
			super.cancelGame();
			setTrainingMode(true);
		}

		@Override
		void endGame() {
			super.endGame();

			int remainingTime = (int) getRemainingTimeMs() / 1000;
			currentPoints = currentPoints + remainingTime;

			showEndGameDialog(remainingTime);

			saveScore(currentPoints);
			setTrainingMode(true);
		}

		public void setTrainingMode(boolean training) {
			if (training) {
				isGame = false;
				btnSolution.setVisibility(View.VISIBLE);
				points.setVisibility(View.GONE);
				title.setTextSize(30);
			} else {
				isGame = true;

				currentPoints = 0; //reset current Points
				btnSolution.setVisibility(View.GONE);
				points.setVisibility(View.VISIBLE);
				title.setTextSize(20);
			}
		}




		public void showEndGameDialog(int remainingTime) {
			AlertDialog.Builder abuilder = new AlertDialog.Builder(getActivity());
			abuilder.setTitle(getString(R.string.game_over));

			if (remainingTime > 0)
				abuilder.setMessage(String.format(
						getString(R.string.gameisoverexp), remainingTime,
						currentPoints));
			else
				abuilder.setMessage(String.format(
						getString(R.string.lost_time_over), currentPoints));

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
				Log.v(getClass().getSimpleName(), "Error saving score");
			}
		}

}