package es.uniovi.imovil.fcrtrainer;

import java.util.Date;
import java.util.Random;

import org.json.JSONException;

import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;



public class NetworkMaskExerciseFragment extends BaseExerciseFragment implements OnClickListener{
	
	
	
	private TextView enunciado;
	private EditText solucion;
	private Button botonR;
	private Button botonC;
	private String[]preguntas;
	private String[]respuestas;
	private ImageView imagen;
	private int i;
	private Random r;
	private Handler handler;
	private boolean gameMode = false;
	private int points;
	private int currentQuestionCounter = 1;
	private static final int POINTS_FOR_QUESTION = 10;
	private static final int MAX_QUESTIONS = 5;
	private static final long GAME_DURATION_MS = 5 * 1000 * 60; 
	private boolean won=false;
	private View rootView;
	
	
	public static NetworkMaskExerciseFragment newInstance() {
		
		NetworkMaskExerciseFragment fragment = new NetworkMaskExerciseFragment();
		return fragment;
	}
	
	public NetworkMaskExerciseFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		rootView = inflater.inflate(R.layout.fragment_networkmask, container, false);
		r=new Random();
		i=r.nextInt(5);
		preguntas=new String[5];
		respuestas=new String[5];
		handler=new Handler();
		imagen=(ImageView)rootView.findViewById(R.id.resultimage);
		enunciado=(TextView)rootView.findViewById(R.id.TextEnun2);
		solucion=(EditText)rootView.findViewById(R.id.respuesta);
		botonR=(Button)rootView.findViewById(R.id.button1);
		botonC=(Button)rootView.findViewById(R.id.button2);
		preguntas=getResources().getStringArray(R.array.preguntas);
		
		respuestas=getResources().getStringArray(R.array.respuestas);
		
		
		enunciado.setText(preguntas[i]);
		botonR.setOnClickListener(this);
		botonC.setOnClickListener(this);
		
		
		
		return rootView;
	}
	
	
	public void onClick(View v){
		
		if(v.getId()==R.id.button1){
				Log.i("EOOOO", solucion.getText().toString());
				Log.i("EIIIII", respuestas[i]);
			if(solucion.getText().toString().equals(respuestas[i])){
				//Toast.makeText(getActivity(), "correcto", 2000).show();
				//imagen.setImageResource(R.drawable.correct);
				//imagen.setVisibility(View.VISIBLE);
				if(gameMode){
					gameModeControl();
				}
				showAnimationAnswer(true);
				handler.postDelayed(new Runnable(){
					public void run(){
						imagen.setImageResource(0);	
						i=r.nextInt(5);
						enunciado.setText(preguntas[i]);
						solucion.setText("");
					}
				}, 1500);
				
				
			}else{
				
				//imagen.setImageResource(R.drawable.incorrect);
				//imagen.setVisibility(View.VISIBLE);
				showAnimationAnswer(false);
				handler.postDelayed(new Runnable(){
					public void run(){
						imagen.setImageResource(0);	
						i=r.nextInt(5);
						enunciado.setText(preguntas[i]);
						solucion.setText("");
					}
				}, 1500);
				
				
				
				
			}
				
				
				
		}
		
		
		if(v.getId()==R.id.button2){
			
			solucion.setText(respuestas[i]);
		
			handler.postDelayed(new Runnable(){
				public void run(){
						
					i=r.nextInt(5);
					enunciado.setText(preguntas[i]);
					solucion.setText("");
				}
			}, 1500);	
			
			
		
	
		
		
	}
	
	
   }
	
	
	
	private void gameModeControl() {
		increasePoints(POINTS_FOR_QUESTION);

		if (currentQuestionCounter >= MAX_QUESTIONS) {
			// won
			this.won = true;
			this.endGame();

		}

		if (currentQuestionCounter < MAX_QUESTIONS && getRemainingTimeMs() <= 0) {
			// lost --> no time left...
			this.won = false;
			this.endGame();
		}
		currentQuestionCounter++;
	}

	private void increasePoints(int val) {
		this.points = this.points + val;
		updatePointsTextView(this.points);
	}

	private void updatePointsTextView(int p) {
		TextView tvPoints = (TextView) rootView.findViewById(R.id.points);
		tvPoints.setText(getResources().getString(R.string.points)+ " "+ String.valueOf(p));
	}


	


	@Override
	public void startGame() {
		setGameDuration(GAME_DURATION_MS);
		
		// set starting points of textview
		updatePointsTextView(0); 

		super.startGame();
		updateToGameMode();
	}

	private void updateToGameMode() {
		gameMode = true;

		//	newQuestion();

		Button solution = (Button) rootView.findViewById(R.id.button2);
		solution.setVisibility(View.INVISIBLE);
		//Button change = (Button) rootView.findViewById(R.id.change);
		//change.setVisibility(View.INVISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.points);
		points.setVisibility(View.VISIBLE);

	}

	private void updateToTrainMode() {
		gameMode = false;

		Button solution = (Button) rootView.findViewById(R.id.button2);
		solution.setVisibility(View.VISIBLE);
		//Button change = (Button) rootView.findViewById(R.id.change);
		//change.setVisibility(View.VISIBLE);

		TextView points = (TextView) rootView.findViewById(R.id.points);
		points.setVisibility(View.INVISIBLE);
	}

	@Override
	public void cancelGame() {
		super.cancelGame();
		updateToTrainMode();
	}

	@Override
	void endGame() {
		//convert to seconds
		int remainingTimeInSeconds = (int) super.getRemainingTimeMs() / 1000; 
		//every remaining second gives one extra point.
		this.points = (int) (this.points + remainingTimeInSeconds);

		if (this.won)
			savePoints();

		dialogGameOver();

		super.endGame();

		updateToTrainMode();

		reset();
	}

	private void reset() {
		this.points = 0;
		this.currentQuestionCounter = 0;
		this.won = false;

		updatePointsTextView(0);
	}

	private void savePoints() {
		String username = getResources().getString(R.string.default_user_name);
		try {

			HighscoreManager.addScore(getActivity().getApplicationContext(),
					this.points, R.string.binary, new Date(), username);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Simple GameOver Dialog
	private void dialogGameOver() {
		String message = getResources().getString(R.string.lost);

		if (this.won) {
			message = getResources().getString(R.string.won) + " "
					+ getResources().getString(R.string.points) + " "
					+ this.points;
		}

		Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getResources().getString(R.string.game_over));
		alert.setMessage(message);
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// nothing to do...
			}
		});
		alert.show();

	}


	
	
	
	
	
	
	
}

