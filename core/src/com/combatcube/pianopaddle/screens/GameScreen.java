package com.combatcube.pianopaddle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.combatcube.pianopaddle.Chord;
import com.combatcube.pianopaddle.Constants;
import com.combatcube.pianopaddle.DrawEventVisitor;
import com.combatcube.pianopaddle.EventMap;
import com.combatcube.pianopaddle.Key;
import com.combatcube.pianopaddle.Note;
import com.combatcube.pianopaddle.PianoPaddle;
import com.combatcube.pianopaddle.Shooter;
import com.combatcube.pianopaddle.SoundEngine;
import com.leff.midi.event.MidiEvent;

import static com.combatcube.pianopaddle.PianoPaddle.playServices;

public class GameScreen implements Screen {
    private PianoPaddle game;

    public static final int SCREEN_WIDTH = 1600;
    public static final int SHOOTER_SPEED = 10;
    public static final int SCREEN_HEIGHT = 900;
    private static final int EARLY_TIME = 50;
    private static final int LATE_TIME = 100;
    private static final int POWERUP_TIME = 480; // 480 ticks = 1 quarter note
    private EventMap eventMap;

    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;

    private float diatonicWidth;
    private float chromaticWidth;
    private Array<Shooter> shooters;
    private float shooterWidth;
    private int score;
    private float noteScale = 0.75f;
    private boolean chromatic;
    private double chromaticTimer;
    private DrawEventVisitor drawVisitor;
    private int currentStreak = 0;
    private int multiplier = 1;
    private int missStreak = 0;
    private boolean perfect = true;
    private String songName;

    public GameScreen(final PianoPaddle game, String filename) {
        this.game = game;
        songName = filename;
        game.soundEngine.init(filename);
        eventMap = game.soundEngine.getEventMap();
        eventMap.findIntervals(eventMap.trackNotes);
        int range = eventMap.maxNote - eventMap.minNote;
        chromaticWidth = 40;
        diatonicWidth = chromaticWidth * 12 / (float) 7;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (range + 1) * chromaticWidth, SCREEN_HEIGHT);
        camera.translate(eventMap.minNote * chromaticWidth, 0);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        chromaticTimer = 0;
        drawVisitor = new DrawEventVisitor(game.renderer);
        drawVisitor.screenWidth = SCREEN_WIDTH;
        drawVisitor.range = range;
        shooters = new Array<Shooter>();
        shooters.add(new Shooter(11, 15, chromaticWidth, 40, Color.PINK));
        shooters.add(new Shooter(7, 10, chromaticWidth, 30, Color.ORANGE));
        shooters.add(new Shooter(3, 6, chromaticWidth, 20, Color.GREEN));
        shooters.add(new Shooter(0, 2, chromaticWidth, 10, Color.BLUE));
        game.soundEngine.run();
    }

    @Override
    public void show() {
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();
        drawScore();
        drawStreak();
        game.batch.end();
        game.renderer.setProjectionMatrix(camera.combined);

        double currentTick = game.soundEngine.getCurrentTick();
        drawVisitor.currentTick = currentTick;
        drawVisitor.noteScale = noteScale;
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            doMouseInput();
        } else {
            doTouchInput();
        }
//        doKeyboardInput();
        if (chromatic && (currentTick > chromaticTimer + POWERUP_TIME)) {
            chromatic = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            noteScale += 0.1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            noteScale -= 0.1f;
        }
        camera.position.y = 200;
        camera.update();
        hudCamera.update();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        game.renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawPiano();
        drawEvents(currentTick);
        drawPaddle();
//        drawTickLine();
        game.renderer.end();
        if (currentTick > game.soundEngine.totalTicks + SoundEngine.COUNT_OUT * game.soundEngine.ppq) {
            if (perfect) {
                playServices.unlockAchievement("CgkIoYKMtJsREAIQAA");
            }
            Preferences prefs;
            if (playServices.isSignedIn()) {
                 prefs = Gdx.app.getPreferences(playServices.getPlayerId());
            } else {
                prefs = Gdx.app.getPreferences("me");
            }
            if (!prefs.contains(songName)) {
                playServices.incrementAchievement("CgkIoYKMtJsREAIQAg", 1);
                prefs.putInteger(songName, score);
            } else {
                int prevScore = prefs.getInteger(songName);
                if (score >= prevScore) {
                    prefs.putInteger(songName, score);
                }
            }
            if (songName.equals("Moonlight_Sonata.mid")) {
                playServices.unlockAchievement("CgkIoYKMtJsREAIQAQ");
            }
            prefs.flush();
            playServices.submitScore(Constants.myMap.get(songName), score);
            game.endGame();
        }
    }

    private void drawScore() {
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, "" + score, 0, 900);
    }

    private void drawStreak() {
        game.font.setColor(Color.YELLOW);
        game.font.draw(game.batch, currentStreak + ": " + multiplier + "x", 800, 900);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        game.soundEngine.pause();
    }

    @Override
    public void resume() {
        game.soundEngine.resume();
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        game.soundEngine.dispose();
    }

    private void drawTickLine() {
        game.renderer.setColor(Color.NAVY);
        game.renderer.rect(0, 0, 109 * chromaticWidth, 5);
    }

    private void drawEvents(double currentTick) {
        // Draw notes
        int topCutoff = SCREEN_HEIGHT;
        int bottomCutoff = -200;
        for (Note note : eventMap.trackNotes) {
            if ((note.getTick() - currentTick) * noteScale < bottomCutoff
                    || (topCutoff < (note.getTick() - currentTick) * noteScale)) {
                continue;
            }
            // Do collision
            if (!note.touched
                    && note.getTick() - EARLY_TIME <= currentTick
                    && currentTick < note.getTick() + LATE_TIME) {
                for (Shooter shooter : shooters) {
                    if (shooter.contains(note) && !note.touched) {
                        note.touched = true;
                        if (note.passed) {
                            // TODO: Add "late" text
                            game.soundEngine.playNote(note.getChannel(), note.getNoteValue(), note.originalVelocity);
                            note.late = true;
                            score += 30 * multiplier;
                        } else {
                            score += 50 * multiplier;
                        }
                        incrementStreak();
                    }
                }
                if (!note.touched && !note.passed) {
                    // "Missed" the note at first chance (EARLY_TIME) - do not let engine play
                    note.passed = true;
                }
            }
            if (!note.touched
                    && note.getTick() + LATE_TIME < currentTick) {
                note.missed = true;
                perfect = false;
                // TODO: Add "miss" text
                incrementMissStreak();
            }
            note.accept(drawVisitor);
        }
        for (MidiEvent event : eventMap.trackEvents) {
            if ((event.getTick() - currentTick) * noteScale < bottomCutoff
                    || (topCutoff < (event.getTick() - currentTick) * noteScale)) {
                continue;
            }
            event.accept(drawVisitor);
        }
        for (Chord chord : eventMap.trackChords) {
            if ((chord.getTick() - currentTick) * noteScale < bottomCutoff
                    || (topCutoff < (chord.getTick() - currentTick) * noteScale)) {
                continue;
            }
            chord.accept(drawVisitor);
        }
    }

    private void incrementMissStreak() {
        currentStreak = 0;
        missStreak += 1;
        if (missStreak >= 50) {
            playServices.unlockAchievement("CgkIoYKMtJsREAIQBA");
        }
    }

    private void incrementStreak() {
        missStreak = 0;
        currentStreak += 1;
        if (currentStreak < 10) {
            multiplier = 1;
        } else if (10 <= currentStreak && currentStreak < 20) {
            multiplier = 2;
        } else if (20 <= currentStreak && currentStreak < 30) {
            multiplier = 3;
        } else {
            multiplier = 4;
        }
        if (currentStreak >= 100) {
            playServices.unlockAchievement("CgkIoYKMtJsREAIQAw");
        }
    }

    public void drawDebug() {
//        for (Note note : eventMap.trackNotes) {
//            renderer.setColor(Color.GREEN);
//            float x = noteX(note.getNoteValue());
//            float width = chromaticWidth;
//            float y = (float) (note.getTick() - currentTick) * noteScale;
//            renderer.rect(x, y - EARLY_TIME * noteScale, width, EARLY_TIME * noteScale);
//            renderer.setColor(Color.YELLOW);
//            renderer.rect(x, y, width, LATE_TIME * noteScale);
//        }
    }

    private void drawPaddle() {
        // Draw paddle
        for (Shooter shooter : shooters) {
            shooter.draw(game.renderer);
        }
    }

    private void doTouchInput() {
        Vector3 touch0Pos = new Vector3();
        touch0Pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        chromatic = Gdx.input.isTouched(1);
        for (Shooter shooter : shooters) {
            shooter.moveCenterX(
                    (touch0Pos.x) * (camera.viewportWidth / (float) Gdx.graphics.getWidth()) + camera.position.x - camera.viewportWidth / 2);
        }
    }

    private void doMouseInput() {
        Vector3 mousePos = new Vector3();
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && (!chromatic)) {
            chromatic = true;
            chromaticTimer = game.soundEngine.getCurrentTick();
        }
        for (Shooter shooter : shooters) {
            shooter.moveCenterX(
                    (mousePos.x) * (camera.viewportWidth / (float) Gdx.graphics.getWidth()) + camera.position.x - camera.viewportWidth / 2);
        }
    }

    private void drawPiano() {
        // White (lower) keys
        for (int i = 21; i < 109; i++) {
            if (!Key.isBlackKey(i)) {
//                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.WHITE : Color.WHITE);
//                notePatch.draw(batch, notePianoX(pitch), -200f, diatonicWidth, 150f);
                game.renderer.setColor(Color.WHITE);
                game.renderer.rect(notePianoX(i), -200f, diatonicWidth, 150f);
            }
        }
        // Black (upper) keys
        for (int i = 21; i < 109; i++) {
            if (Key.isBlackKey(i)) {
//                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.BLACK : Color.BLACK);
//                notePatch.draw(batch, notePianoX(pitch), -150f, chromaticWidth, 100);
                game.renderer.setColor(Color.BLACK);
                game.renderer.rect(notePianoX(i), -150f, chromaticWidth, 100);
            }
        }
    }

    public float notePianoX(int pitch) {
        float x = (pitch) * chromaticWidth;
        switch (pitch % 12) {
            case 0: // Do
                x -= chromaticWidth * 0 / (float) 7;
                break;
            case 2: // Re
                x -= chromaticWidth * 2 / (float) 7;
                break;
            case 4: // Mi
                x -= chromaticWidth * 4 / (float) 7;
                break;
            case 5: // Fa
                x += chromaticWidth * 1 / (float) 7;
                break;
            case 7: // So
                x -= chromaticWidth * 1 / (float) 7;
                break;
            case 9: // La
                x -= chromaticWidth * 3 / (float) 7;
                break;
            case 11: // Ti
                x -= chromaticWidth * 5 / (float) 7;
                break;
            default: // Non-diatonic
                break;
        }
        return x;
    }

}
