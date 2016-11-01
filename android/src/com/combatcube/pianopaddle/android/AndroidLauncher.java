package com.combatcube.pianopaddle.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.combatcube.pianopaddle.PianoPaddle;
import com.combatcube.pianopaddle.PlayServices;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

public class AndroidLauncher extends AndroidApplication implements PlayServices {
    private GameHelper gameHelper;
    private final static int requestCode = 1;
    public boolean signInFailed = false;
    public boolean signInSucceeded = false;

    @Override
	protected void onCreate (Bundle savedInstanceState) {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);

        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
        {
            @Override
            public void onSignInFailed(){
                signInFailed = true;
            }

            @Override
            public void onSignInSucceeded(){
                signInSucceeded = true;
            }
        };

        gameHelper.setup(gameHelperListener);


        super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        AndroidCsoundAdapter csoundAdapter = new AndroidCsoundAdapter(getBaseContext().getApplicationInfo().nativeLibraryDir);
        initialize(new PianoPaddle(csoundAdapter, this), config);
	}

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        gameHelper.onActivityResult(request, response, data);
    }


    @Override
    public void signIn() {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        }
        catch (Exception e)
        {
            Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void signOut() {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    signInSucceeded = false;
                    signInFailed = false;
                    gameHelper.signOut();
                }
            });
        }
        catch (Exception e)
        {
            Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void rateGame() {
//        String str = "";
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    @Override
    public void unlockAchievement(String id) {
        if (isSignedIn()) {
            Games.Achievements.unlock(gameHelper.getApiClient(), id);
        }
    }

    @Override
    public void incrementAchievement(String id, int amount) {
        if (isSignedIn()) {
            Games.Achievements.increment(gameHelper.getApiClient(), id, amount);
        }
    }

    @Override
    public void submitScore(String id, int highScore) {
        if (isSignedIn())
        {
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    id, highScore);
        }
    }

    @Override
    public void showAchievement() {
        if (isSignedIn())
        {
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        }
        else
        {
            signIn();
        }
    }

    @Override
    public void showScore() {
        if (isSignedIn())
        {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), requestCode);
        }
        else
        {
            signIn();
        }

    }

    @Override
    public boolean isSignedIn() {
        return gameHelper.isSignedIn() || signInSucceeded;
    }

    @Override
    public boolean hasSignInError() {
        return gameHelper.hasSignInError();
    }

    @Override
    public String getPlayerId() {
        return Games.Players.getCurrentPlayerId(gameHelper.getApiClient());
    }
}
