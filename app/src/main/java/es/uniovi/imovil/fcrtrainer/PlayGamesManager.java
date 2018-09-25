package es.uniovi.imovil.fcrtrainer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

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

import static es.uniovi.imovil.fcrtrainer.MainActivity.RC_SIGN_IN;

public class PlayGamesManager implements OnCompleteListener<Player> {
    private static final String TAG = "PlayGamesManager";

    private GoogleSignInClient mGoogleSignInClient;
    private LeaderboardsClient mLeaderboardsClient;
    private PlayersClient mPlayersClient;

    private MainActivity mActivity;

    public void onSignInButtonClicked() {
        if (isSignedIn()) {
            signOut();
        } else {
            startSignInIntent();
        }
    }

    public void onNewScore(int score) {
        mOutbox.mScore = score;
        pushAccomplishments();
    }

    private class AccomplishmentsOutbox {
        int mScore = -1;

        boolean isEmpty() {
            return mScore < 0;
        }
    }

    // Achievements and scores we're pending to push to the cloud
    // (waiting for the user to sign in, for instance)
    private final AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

    PlayGamesManager(MainActivity activity) {
        mActivity = activity;

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

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
        Task<GoogleSignInAccount> task =
                GoogleSignIn.getSignedInAccountFromIntent(intent);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            onConnected(account);
        } catch (ApiException apiException) {
            String message = apiException.getMessage();
            if (message == null || message.isEmpty()) {
                message = mActivity.getString(R.string.signin_other_error);
            }

            onDisconnected();

            new AlertDialog.Builder(mActivity)
                    .setMessage(message)
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }
    }

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        //mAchievementsClient = Games.getAchievementsClient(this, googleSignInAccount);
        mLeaderboardsClient = Games.getLeaderboardsClient(mActivity, googleSignInAccount);
        //mEventsClient = Games.getEventsClient(this, googleSignInAccount);
        mPlayersClient = Games.getPlayersClient(mActivity, googleSignInAccount);

        // Show sign-out button on main menu
        //mMainMenuFragment.setShowSignInButton(false);

        // Show "you are signed in" message on win screen, with no sign in button.
        //mWinFragment.setShowSignInButton(false);
        mActivity.onConnected();

        // Set the greeting appropriately on main menu
        mPlayersClient.getCurrentPlayer().addOnCompleteListener(this);

        // If we have accomplishments to push, push them
        if (!mOutbox.isEmpty()) {
            pushAccomplishments();
        }

		/*
		loadAndPrintEvents();
		*/
    }

    private void onDisconnected() {
        Log.d(TAG, "onDisconnected()");

        //mAchievementsClient = null;
        mLeaderboardsClient = null;
        mPlayersClient = null;

        // Show sign-in button on main menu
        //mMainMenuFragment.setShowSignInButton(true);

        // Show sign-in button on win screen
        //mWinFragment.setShowSignInButton(true);
        mActivity.onDisconnected();

        //mMainMenuFragment.setGreeting(getString(R.string.signed_out_greeting));
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
            Toast.makeText(mActivity, mActivity.getString(R.string.error_not_logged_in),
                    Toast.LENGTH_LONG).show();
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

        if (mOutbox.mScore >= 0) {
            mLeaderboardsClient.submitScore(
                    mActivity.getString(R.string.leaderboard_binary__beginner),
                    mOutbox.mScore);
            mOutbox.mScore = -1;
        }
    }
}

