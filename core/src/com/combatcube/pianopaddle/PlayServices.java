package com.combatcube.pianopaddle;

/**
 * Created by Andrew on 2016-10-28.
 */

public interface PlayServices {
    void signIn();
    void signOut();
    void rateGame();
    void unlockAchievement(String id);
    void incrementAchievement(String id, int amount);
    void submitScore(String id, int highScore);
    void showAchievement();
    void showScore(String id);
    boolean isSignedIn();
}
