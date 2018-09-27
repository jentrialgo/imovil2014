package es.uniovi.imovil.fcrtrainer;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import es.uniovi.imovil.fcrtrainer.highscores.Highscore;
import es.uniovi.imovil.fcrtrainer.highscores.HighscoreManager;

public class EndGameFragment extends Fragment implements Button.OnClickListener {
    private static final String TAG = "EndGameFragment";
    private final static String ARG_SCORE = "score";
    private final static  String ARG_ID = "exerciseID";
    private static final int WRITE_REQUEST_CODE = 1;

    private int score;  //Score of the game that just ended
    private int exerciseId;

    // For sharing
    private File imagePath;

    //Declarations of layout vars
    private TextView lbScore;
    private TextView lbMotivation;
    private TextView lbHighScore;
    private ImageView ivEndGame;

    public EndGameFragment() {
        // Required empty public constructor
    }

    public static EndGameFragment newInstance(int score, Screen exercise) {
        EndGameFragment fragment = new EndGameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_ID, exercise.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.score = getArguments().getInt(ARG_SCORE);
            this.exerciseId = getArguments().getInt(ARG_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_endgame, container, false);

        Button btPlayAgain = rootView.findViewById(R.id.btPlayAgain);
        btPlayAgain.setOnClickListener(this);

        int[] ivButtons = {R.id.ibShareFacebook, R.id.ibShareInstagram, R.id.ibShareTwitter,
            R.id.ibShare};
        for (int buttonId: ivButtons) {
            ImageButton ib = rootView.findViewById(buttonId);
            ib.setOnClickListener(this);
        }

        lbScore = rootView.findViewById(R.id.lbScore);
        lbHighScore = rootView.findViewById(R.id.lbHighScore);
        lbMotivation = rootView.findViewById(R.id.lbMotivation);
        ivEndGame = rootView.findViewById(R.id.ivEndGame);

        resolveScore();

        return rootView;
    }

    /**
    * resolveScore computes the score and gives you a motivational phrase depending on the score
    * This also changes the icon on top right of the layout depending on the score
    * If the score is a Max Score, the icon is different and a new TextView (lbHighScore) appears
    */
    private void resolveScore() {
        lbScore.setText(getResources().getString(R.string.endgame_score, score));
        lbMotivation.setText(gameOverMessage(this.score));

        if(score < 5) {
            ivEndGame.setImageResource(R.drawable.incorrect);
        }

        boolean isHighscore = true;
        try {
            ArrayList<Highscore> highscores = HighscoreManager.loadHighscores(getContext(),
                    PreferenceUtils.getLevel(getActivity()));

            for (Highscore h : highscores) {
                if (h.getExercise() == exerciseId && h.getScore() > score) {
                    isHighscore = false;
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "Error al analizar el JSON: " + e.getMessage());
            Toast.makeText(getActivity(),
                    getActivity().getString(R.string.error_parsing_highscores),
                    Toast.LENGTH_LONG).show();
        }

        if (isHighscore){
            lbHighScore.setText(R.string.endgame_newrecord);
            ivEndGame.setImageResource(R.drawable.ic_star);
        }
        else {
            lbHighScore.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Depending on the score, returns a sentence
     * @param pScore
     *          Score obtained
     * @return The motivational sentence
     */
    private String gameOverMessage(int pScore) {
        if(pScore < -99)
            return getResources().getString(R.string.endgame_luck);
        else if(pScore < 1)
            return getResources().getString(R.string.endgame_verybad);
        else if (pScore <= 10)
            return getResources().getString(R.string.endgame_bad);
        else if (pScore <= 25)
            return getResources().getString(R.string.endgame_good);
        else if (pScore <= 50)
            return getResources().getString(R.string.endgame_verygood);
        else
            return getResources().getString(R.string.endgame_excellent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btPlayAgain:
                getFragmentManager().popBackStackImmediate();   //Return to the previous fragment
                break;
            case R.id.ibShareFacebook:
                shareGame("com.facebook.katana");
                break;
            case R.id.ibShareTwitter:
                shareGame("com.twitter.android");
                break;
            case R.id.ibShareInstagram:
                shareGame("com.instagram.android");
                break;
            case R.id.ibShare:
                shareGame("generic");
                break;
        }
    }

    /***
     * Crea una captura de pantalla para compartirla al final del juego
     */
    public Bitmap takeScreenshot() {
        View rootView = getActivity().findViewById(R.id.content_frame).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    private void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/scrnshot.png"); ////File imagePath
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String shareMessage() {
        return String.format(getString(R.string.share_message), score);
    }

    /**
     * for write permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Granted.
                }
                else{
                    // Denied.
                }
                break;
        }
    }

    /**
     * Intent para compartir
     */
    public void shareGame(String target){
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        } else {
            saveBitmap(takeScreenshot());
            Uri uriSreenshot = FileProvider.getUriForFile(getActivity(),
                    getActivity().getApplicationContext().getPackageName()
                            + ".es.uniovi.imovil.fcrtrainer.provider",
                    imagePath);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage());
            intent.putExtra(Intent.EXTRA_STREAM, uriSreenshot);

            if (target.equals("generic")) {
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            } else {
                intent.setPackage(target);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    CharSequence text = getString(R.string.official_app_needed);
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
