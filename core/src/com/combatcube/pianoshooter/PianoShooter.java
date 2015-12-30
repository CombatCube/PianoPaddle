package com.combatcube.pianoshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PianoShooter extends Game {
    public static final int SCREEN_WIDTH = 1600;
    public static final int SHOOTER_SPEED = 10;
    public static final int SCREEN_HEIGHT = 900;
    private static final int EARLY_TIME = 50;
    private static final int LATE_TIME = 100;
    private CsoundAdapter csoundAdapter;
    private SoundEngine soundEngine;
    private EventMap eventMap;
    private int range;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private BitmapFont font;
    private NinePatch notePatch;
    //    private Color diatonicColor = new Color(0.0f, 0.75f, 0.0f, 0.25f);
    private Color diatonicColor = new Color(1f, 1f, 1f, 0.75f);
    private Color chromaticColor = new Color(1f, 1f, 0f, 0.75f);

    private float diatonicWidth;
    private float chromaticWidth;
    private Array<Shooter> shooters;
    private float shooterWidth;
    private int score;
    private float noteScale = 0.75f;
    private boolean chromatic;

    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        soundEngine = new SoundEngine(csoundAdapter);
        notePatch = new TextureAtlas("textures.pack").createPatch("notepatch");
        eventMap = soundEngine.getEventMap();
        range = eventMap.maxNote - eventMap.minNote;
        chromaticWidth = SCREEN_WIDTH / (float) (range + 1);
        diatonicWidth = chromaticWidth * 12 / (float) 7;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, 900);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        shooterWidth = diatonicWidth * 10;
        shooters = new Array<Shooter>();
        shooters.add(new Shooter(new Rectangle(0, 0, shooterWidth, 100)));
//        shooters.add(new Shooter(new Rectangle(SCREEN_WIDTH-shooterWidth, 0, shooterWidth, 100)));
        soundEngine.startPlaying();
    }


    @Override
    public void render () {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        // Move camera and shooters according to input
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            doMouseInput();
        } else {
            doTouchInput();
        }
//        doKeyboardInput();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            noteScale += 0.1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            noteScale -= 0.1f;
        }
        camera.position.y = 200;
        camera.update();

        batch.begin();
        drawPiano();
        // Draw notes
        double currentTick = soundEngine.getCurrentTick();
        int topCutoff = SCREEN_HEIGHT;
        int bottomCutoff = -200;
        for (Note note : eventMap.trackNotes) {
            if ((note.getTick() - currentTick + note.duration) * noteScale < bottomCutoff
                    || (topCutoff < (note.getTick() - currentTick) * noteScale)) {
                continue;
            }
            notePatch.setColor(getNoteColor(note));
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(note.getNoteValue());
            note.diatonic = (scaleDegree != -1);
            float x = noteX(note.getNoteValue());
            float width = Key.isBlackKey(note.getNoteValue()) ? chromaticWidth : chromaticWidth;
            float y = (float) (note.getTick() - currentTick) * noteScale;
            float height = note.duration * noteScale;
            notePatch.draw(batch, x, y, width, height);
            // Do collision
            if (!note.touched
                    && note.getTick() - EARLY_TIME <= currentTick
                    && currentTick < note.getTick() + LATE_TIME) {
                for (Shooter shooter : shooters) {
                    if (shooter.contains(x, width)
                            && (note.diatonic || chromatic)) {
                        note.touched = true;
                        if (note.passed) {
                            soundEngine.playNote(note.getChannel(), note.getNoteValue(), note.originalVelocity);
                        }
                        score += 50;
                    }
                }
                if (!note.touched && !note.passed) {
                    // "Missed" the note at first chance (EARLY_TIME) - do not let engine play
                    note.passed = true;
                    note.setVelocity(0);
                }
            }
            if (!note.touched
                    && note.getTick() + LATE_TIME < currentTick) {
                note.missed = true;
            }
        }
        font.setColor(Color.YELLOW);
        font.draw(batch, soundEngine.getKey().pitchClass.toString(), 50, 125);
        font.draw(batch, "Score: " + score, 1500, 125);
        batch.end();
        // Draw paddle
        Gdx.gl.glEnable(GL20.GL_BLEND);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(chromatic ? chromaticColor : diatonicColor);
        for (Shooter shooter : shooters) {
            renderer.rect(shooter.getRect().x - 10,
                    shooter.getRect().y,
                    10,
                    shooter.getRect().height);
            renderer.rect(shooter.getRect().x + shooter.getRect().getWidth(),
                    shooter.getRect().y,
                    10,
                    shooter.getRect().height);
            renderer.rect(shooter.getRect().x,
                    shooter.getRect().y,
                    shooter.getRect().getWidth(),
                    10);
        }
        renderer.setColor(Color.NAVY);
        renderer.rect(0, 0, SCREEN_WIDTH, 5);

//        for (Note note : eventMap.trackNotes) {
//            renderer.setColor(Color.GREEN);
//            float x = noteX(note.getNoteValue());
//            float width = chromaticWidth;
//            float y = (float) (note.getTick()- currentTick) * noteScale;
//            renderer.rect(x, y - EARLY_TIME * noteScale, width, EARLY_TIME * noteScale);
//            renderer.setColor(Color.YELLOW);
//            renderer.rect(x, y, width, LATE_TIME* noteScale);
//        }
        renderer.end();
    }

    private void doKeyboardInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (shooters.get(0).getRect().x > 0) {
                shooters.get(0).getRect().x -= SHOOTER_SPEED;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (shooters.get(0).getRect().x + shooterWidth < shooters.get(1).getRect().x) {
                shooters.get(0).getRect().x += SHOOTER_SPEED;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            if (shooters.get(1).getRect().x > shooters.get(0).getRect().x + shooterWidth) {
                shooters.get(1).getRect().x -= SHOOTER_SPEED;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            if (shooters.get(1).getRect().x < SCREEN_WIDTH - shooterWidth) {
                shooters.get(1).getRect().x += SHOOTER_SPEED;
            }
        }
        chromatic = Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }

    private void doTouchInput() {
        Vector3 touch0Pos = new Vector3();
        touch0Pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        chromatic = Gdx.input.isTouched(1);
        shooters.get(0).moveCenterX((touch0Pos.x) * (SCREEN_WIDTH / (float) Gdx.graphics.getWidth()));
    }

    private void doMouseInput() {
        Vector3 mousePos = new Vector3();
        mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        chromatic = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        shooters.get(0).moveCenterX((mousePos.x) * (SCREEN_WIDTH / (float) Gdx.graphics.getWidth()));
    }

    private void drawPiano() {
        // White (lower) keys
        for (int i = 0; i < range + 1; i++) {
            int pitch = eventMap.minNote + i;
            if (!Key.isBlackKey(pitch)) {
                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.WHITE : Color.WHITE);
                notePatch.draw(batch, noteX(pitch), -200f, diatonicWidth, 150f);
            }
        }
        // Black (upper) keys
        for (int i = 0; i < range + 1; i++) {
            int pitch = eventMap.minNote + i;
            if (Key.isBlackKey(pitch)) {
                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.BLACK : Color.BLACK);
                notePatch.draw(batch, noteX(pitch), -150f, chromaticWidth, 100);
            }
        }
    }

    private Color getNoteColor(Note note) {
        if (soundEngine.getCurrentTick() - LATE_TIME < note.getTick()) {
            if (soundEngine.getKey().pitchToScaleDegree(note.getNoteValue()) != -1) {
                // In-key
                if (Key.isBlackKey(note.getNoteValue())) {
                    return Color.WHITE;
                } else {
                    return Color.WHITE;
                }
            } else {
                // Out of key
                if (Key.isBlackKey(note.getNoteValue())) {
                    return Color.YELLOW;
                } else {
                    return Color.YELLOW;
                }
            }
        } else {
            if (note.touched) {
                return Color.GREEN;
            } else {
                return Color.RED;
            }
        }
    }

    private float noteX(int pitch) {
        float x = (pitch - eventMap.minNote) * chromaticWidth;
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
