package com.combatcube.pianoshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class PianoShooter extends Game {
    private CsoundAdapter csoundAdapter;
    private Shooter primaryShooter;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private NinePatch notePatch;

    private float diatonicWidth;
    private float chromaticWidth;
    private float shooterWidth;
    private SoundEngine soundEngine;
    private EventMap eventMap;
    private int range;
    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        soundEngine = new SoundEngine(csoundAdapter);
        loadAssets();
        eventMap = soundEngine.getMainEventMap();
        range = eventMap.maxNote - eventMap.minNote;
        chromaticWidth = 1600 / (float) (range + 1);
        diatonicWidth = chromaticWidth * 12 / (float) 7;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        shooterWidth = diatonicWidth * 10;
        primaryShooter = new Shooter(new Rectangle(0, 25, shooterWidth, 20));
        soundEngine.startPlaying();
    }


    @Override
    public void render () {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);

        // Move camera and shooters according to input
        boolean nonDiatonic;
        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            // Mouse input
            Vector3 mousePos = new Vector3();
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            float mouseCenterX = (mousePos.x) * (1600 / (float) Gdx.graphics.getWidth());
            nonDiatonic = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            primaryShooter.setWidth((int) (shooterWidth));
            primaryShooter.moveCenterX(mouseCenterX);
        } else {
            // Touch input
            Vector3 touch0Pos = new Vector3();
            touch0Pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            nonDiatonic = Gdx.input.isTouched(1);
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
        for (Note note : eventMap.noteEvents) {
            notePatch.setColor(getNoteColor(note));
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(note.pitch);
            note.diatonic = (scaleDegree != -1);
            float x = noteX(note.pitch, scaleDegree);
            float width = note.diatonic ? diatonicWidth : chromaticWidth;
            float y = (float) (note.tick - soundEngine.getCurrentTick());
            float height = note.duration;
            notePatch.draw(batch, x, y, width, height);
            // Do collision
            if (!note.missed && !note.played && note.tick <= soundEngine.getCurrentTick()) {
                if (primaryShooter.contains(x, width)) {
                    if (note.diatonic || nonDiatonic) {
                        csoundAdapter.playNote(11, soundEngine.ticksToSeconds(note.duration), note.pitch, note.velocity);
                        note.played = true;
                    } else {
                        note.missed = true;
                    }
                } else {
                    note.missed = true;
                }
            }
        }
        drawPiano();
        batch.end();

        // Draw paddle
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(nonDiatonic ? Color.NAVY : Color.FOREST);
        renderer.rect(primaryShooter.getRect().x,
                primaryShooter.getRect().y,
                primaryShooter.getRect().width,
                primaryShooter.getRect().height);

        // Draw piano
        renderer.end();
    }

    private void drawPiano() {
        int j = 0;
        for (int i = 0; i < range; i++) {
            int pitch = eventMap.minNote + i;
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(pitch);
            if (!Key.isBlackKey(pitch)) {
                notePatch.setColor(scaleDegree != -1 ? Color.GREEN : Color.WHITE);
                notePatch.draw(batch, j * diatonicWidth, -200f, diatonicWidth, 200f);
                j++;
            }
        }
        for (int i = 0; i < range; i++) {
            int pitch = eventMap.minNote + i;
            int scaleDegree = soundEngine.getKey().pitchToScaleDegree(pitch);
            if (Key.isBlackKey(pitch)) {
                notePatch.setColor(scaleDegree != -1 ? Color.FOREST : Color.BLACK);
                notePatch.draw(batch, i * chromaticWidth - 0.5f * chromaticWidth, -100f, chromaticWidth * 1.33f, 100);
            }
        }
    }

    private Color getNoteColor(Note note) {
        if (note.tick > soundEngine.getCurrentTick()) {
            if (soundEngine.getKey().pitchToScaleDegree(note.pitch) == -1) {
                if (Key.isBlackKey(note.pitch)) {
                    return Color.NAVY;
                } else {
                    return Color.BLUE;
                }
            } else {
                if (Key.isBlackKey(note.pitch)) {
                    return Color.FOREST;
                } else {
                    return Color.GREEN;
                }
            }
        } else {
            if (!note.missed) {
                if (!Key.isBlackKey(note.pitch)) {
                    return Color.WHITE;
                } else {
                    return Color.BLACK;
                }
            } else {
                return Color.RED;
            }
        }
    }

    private float noteX(int pitch, int scaleDegree) {
        float x = (pitch - eventMap.minNote) * chromaticWidth;
        switch (scaleDegree) {
            case 0: // Do
                x -= chromaticWidth * 0 / (float) 7;
                break;
            case 1: // Re
                x -= chromaticWidth * 2 / (float) 7;
                break;
            case 2: // Mi
                x -= chromaticWidth * 4 / (float) 7;
                break;
            case 3: // Fa
                x += chromaticWidth * 1 / (float) 7;
                break;
            case 4: // So
                x -= chromaticWidth * 1 / (float) 7;
                break;
            case 5: // La
                x -= chromaticWidth * 3 / (float) 7;
                break;
            case 6: // Ti
                x -= chromaticWidth * 5 / (float) 7;
                break;
            default: // Non-diatonic
                break;
        }
        return x;
    }


    private void loadAssets() {
        TextureAtlas textures = new TextureAtlas("textures.pack");
        notePatch = textures.createPatch("notepatch");
        notePatch.setColor(Color.WHITE);
    }


}
