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

public class PianoShooter extends Game {
    private CsoundAdapter csoundAdapter;
    private SoundEngine soundEngine;
    private EventMap eventMap;
    private int range;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private BitmapFont font;
    private NinePatch notePatch;
    private Color diatonicColor = new Color(0.0f, 0.75f, 0.0f, 0.25f);
    private Color chromaticColor = new Color(0.0f, 0.0f, 0.75f, 0.25f);

    private float diatonicWidth;
    private float chromaticWidth;
    private Shooter primaryShooter;
    private float shooterWidth;
    private int score;

    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        soundEngine = new SoundEngine(csoundAdapter);
        notePatch = new TextureAtlas("textures.pack").createPatch("notepatch");
        eventMap = soundEngine.getEventMap();
        range = eventMap.maxNote - eventMap.minNote;
        chromaticWidth = 1600 / (float) (range + 1);
        diatonicWidth = chromaticWidth * 12 / (float) 7;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        shooterWidth = diatonicWidth * 5;
        primaryShooter = new Shooter(new Rectangle(0, 25, shooterWidth, 900));
        soundEngine.startPlaying();
    }


    @Override
    public void render () {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        // Move camera and shooters according to input
        boolean chromatic;
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            // Mouse input
            Vector3 mousePos = new Vector3();
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            float mouseCenterX = (mousePos.x) * (1600 / (float) Gdx.graphics.getWidth());
            chromatic = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            int width = (int) (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) ? shooterWidth * 2 : shooterWidth);
            primaryShooter.setWidth(width);
            primaryShooter.moveCenterX(mouseCenterX);
        } else {
            // Touch input
            Vector3 touch0Pos = new Vector3();
            touch0Pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            chromatic = Gdx.input.isTouched(1);
            primaryShooter.setWidth((int) (shooterWidth));
            primaryShooter.moveCenterX((touch0Pos.x) * (1600 / (float) Gdx.graphics.getWidth()));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            soundEngine.setKey(new Key(soundEngine.getKey().pitchClass.successor(1)));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            soundEngine.setKey(new Key(soundEngine.getKey().pitchClass.successor(-1)));
        }
        camera.position.y = primaryShooter.getRect().y + 300;
        camera.update();

        // Draw notes
        batch.begin();
        for (Note note : eventMap.trackNotes) {
            notePatch.setColor(getNoteColor(note));
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(note.pitch);
            note.diatonic = (scaleDegree != -1);
            float x = noteX(note.pitch);
            float width = note.diatonic ? diatonicWidth : chromaticWidth;
            float y = (float) (note.onTime - soundEngine.getCurrentTick());
            float height = note.duration;
            notePatch.draw(batch, x, y, width, height);
            // Do collision
            if (!note.missed && !note.played && note.onTime - 40 <= soundEngine.getCurrentTick()) {
                if (primaryShooter.contains(x, width)) {
                    if (note.diatonic || chromatic) {
                        note.played = true;
                        score += 50;
                    } else {
                        note.missed = true;
                        note.setVelocity(0);
                    }
                } else {
                    note.missed = true;
                    note.setVelocity(0);
                }
            }
        }
        drawPiano();
        font.setColor(Color.YELLOW);
        font.draw(batch, soundEngine.getKey().pitchClass.toString(), 50, 125);
        font.draw(batch, "Score: " + score, 1500, 125);
        batch.end();
        // Draw paddle
        Gdx.gl.glEnable(GL20.GL_BLEND);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(chromatic ? chromaticColor : diatonicColor);
        renderer.rect(primaryShooter.getRect().x - 10,
                primaryShooter.getRect().y,
                10,
                primaryShooter.getRect().height);
        renderer.rect(primaryShooter.getRect().x + primaryShooter.getRect().getWidth(),
                primaryShooter.getRect().y,
                10,
                primaryShooter.getRect().height);
        // Draw piano
        renderer.end();
    }

    private void drawPiano() {
        // White (lower) keys
        for (int i = 0; i < range + 1; i++) {
            int pitch = eventMap.minNote + i;
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(pitch);
            if (!Key.isBlackKey(pitch)) {
                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.WHITE : Color.DARK_GRAY);
                notePatch.draw(batch, noteX(pitch), -150f, diatonicWidth, 150f);
            }
        }
        // Black (upper) keys
        for (int i = 0; i < range + 1; i++) {
            int pitch = eventMap.minNote + i;
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(pitch);
            if (Key.isBlackKey(pitch)) {
                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.LIGHT_GRAY : Color.BLACK);
                notePatch.draw(batch, noteX(pitch), -100f, chromaticWidth, 100);
            }
        }
    }

    private Color getNoteColor(Note note) {
        if (note.onTime > soundEngine.getCurrentTick()) {
            if (soundEngine.getKey().pitchToScaleDegree(note.pitch) != -1) {
                return Color.WHITE;
            } else {
                return Color.BLACK;
            }
        } else {
            if (!note.missed) {
                return Color.FOREST;
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
