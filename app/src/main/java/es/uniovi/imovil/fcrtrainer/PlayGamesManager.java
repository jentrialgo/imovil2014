package es.uniovi.imovil.fcrtrainer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static es.uniovi.imovil.fcrtrainer.MainActivity.RC_SIGN_IN;

public class PlayGamesManager implements OnCompleteListener<Player> {
    private static final String TAG = "PlayGamesManager";

    private GoogleSignInClient mGoogleSignInClient;
    private LeaderboardsClient mLeaderboardsClient;
    private PlayersClient mPlayersClient;

    // Scores not yet submitted to the cloud
    private List<ScoreInfo> pendingScores;

    private MainActivity mActivity;

    PlayGamesManager(MainActivity activity) {
        mActivity = activity;
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
        pendingScores = new ArrayList<>();
    }

    void onSignInButtonClicked() {
        if (isSignedIn()) {
            signOut();
        } else {
            startSignInIntent();
        }
    }

    void onNewScore(Screen screen, int score) {
        ScoreInfo scoreInfo = new ScoreInfo(screen, score);
        pendingScores.add(scoreInfo);
        pushAccomplishments();
    }

    void signInSilently() {
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(mActivity,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    private void startSignInIntent() {
        mActivity.startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    void signIn(Intent intent) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            onConnected(account);
        } catch (ApiException apiException) {
            onDisconnected();

            String message = mActivity.getString(R.string.signin_other_error)
                    + apiException.getMessage();
            new AlertDialog.Builder(mActivity)
                    .setMessage(message)
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mLeaderboardsClient = Games.getLeaderboardsClient(mActivity, googleSignInAccount);
        mPlayersClient = Games.getPlayersClient(mActivity, googleSignInAccount);

        mActivity.onConnected();

        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer().addOnCompleteListener(this);

        // If we have accomplishments to push, push them
        if (!pendingScores.isEmpty()) {
            pushAccomplishments();
        }

    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        mLeaderboardsClient = null;
        mPlayersClient = null;

        mActivity.onDisconnected();
    }

    private void signOut() {
        Log.d(TAG, "signOut()");

        if (!isSignedIn()) {
            Log.w(TAG, "signOut() called, but was not signed in!");
            return;
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(mActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean successful = task.isSuccessful();
                        Log.d(TAG, "signOut(): " + (successful ? "success" : "failed"));

                        onDisconnected();
                    }
                });
    }

    private void handleException(Exception e, String details) {
        int status = 0;

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = mActivity.getString(R.string.status_exception_error, details, status, e);

        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(mActivity) != null;
    }

    void showRemoteLeaderboards() {
        if (!isSignedIn()) {
            new AlertDialog.Builder(mActivity)
                    .setMessage(mActivity.getString(R.string.error_not_logged_in))
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
            return;
        }

        mLeaderboardsClient.getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        mActivity.startActivityForResult(intent, MainActivity.RC_UNUSED);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(e, mActivity.getString(R.string.leaderboards_exception));
                    }
                });
    }

    @Override
    public void onComplete(@NonNull Task<Player> task) {
        String displayName;
        if (task.isSuccessful()) {
            displayName = task.getResult().getDisplayName();
        } else {
            Exception e = task.getException();
            handleException(e, mActivity.getString(R.string.players_exception));
            displayName = "???";
        }

        mActivity.setUserName(displayName);
    }

    private void pushAccomplishments() {
        if (!isSignedIn()) {
            // can't push to the cloud, try again later
            return;
        }

        Iterator<ScoreInfo> it = pendingScores.iterator();
        while (it.hasNext()) {
            ScoreInfo scoreInfo = it.next();
            mLeaderboardsClient.submitScore(scoreInfo.mLeaderboardId, scoreInfo.mScore);
            it.remove();
        }

    }

    /** This class represents an score to be submitted
     *
     */
    private class ScoreInfo {
        String mLeaderboardId;
        int mScore;

        ScoreInfo(Screen screen, int score) {
            mLeaderboardId = screen.toLeaderboardId();
            mScore = score;
        }
    }

}
