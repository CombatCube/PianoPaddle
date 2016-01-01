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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.leff.midi.event.MidiEvent;

public class PianoShooter extends Game {
    public static final int SCREEN_WIDTH = 1920;
    public static final int SHOOTER_SPEED = 10;
    public static final int SCREEN_HEIGHT = 900;
    private static final int EARLY_TIME = 50;
    private static final int LATE_TIME = 100;
    private static final int POWERUP_TIME = 480; // 480 ticks = 1 quarter note
    private CsoundAdapter csoundAdapter;
    private SoundEngine soundEngine;
    private EventMap eventMap;
    private int range;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private BitmapFont font;
    private NinePatch notePatch;
    private TextureRegion bowRegion;
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
    private double chromaticTimer;
    private DrawEventVisitor drawVisitor;

    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        soundEngine = new SoundEngine(csoundAdapter);
        TextureAtlas textureAtlas = new TextureAtlas("textures.pack");
        notePatch = textureAtlas.createPatch("notepatch");
        bowRegion = textureAtlas.findRegion("bow");
        eventMap = soundEngine.getEventMap();
        EventMap.findIntervals(eventMap.trackNotes);
        range = eventMap.maxNote - eventMap.minNote;
        chromaticWidth = 40;
        diatonicWidth = chromaticWidth * 12 / (float) 7;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (range + 1) * chromaticWidth, 900);
        camera.translate(eventMap.minNote * chromaticWidth, 0);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        font = new BitmapFont();
        chromaticTimer = 0;
        drawVisitor = new DrawEventVisitor(renderer);
        drawVisitor.screenWidth = SCREEN_WIDTH;
        drawVisitor.range = range;
        shooters = new Array<Shooter>();
        shooters.add(new Shooter(11, 15, chromaticWidth, Color.PURPLE));
        shooters.add(new Shooter(7, 10, chromaticWidth, Color.ORANGE));
        shooters.add(new Shooter(3, 6, chromaticWidth, Color.GREEN));
        shooters.add(new Shooter(0, 2, chromaticWidth, Color.BLUE));
        soundEngine.startPlaying();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);

        double currentTick = soundEngine.getCurrentTick();
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

        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, soundEngine.getKey().pitchClass.toString(), 50, 125);
        font.draw(batch, "Score: " + score, 1500, 125);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        drawPiano();
        drawEvents(currentTick);
        drawPaddle();
        drawTickLine();
        renderer.end();
    }

    private void drawTickLine() {
        renderer.setColor(Color.NAVY);
        renderer.rect(0, 0, 109 * chromaticWidth, 5);
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
                    if (shooter.contains(note)) {
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
                }
            }
            if (!note.touched
                    && note.getTick() + LATE_TIME < currentTick) {
                note.missed = true;
            }
            note.accept(drawVisitor);
        }
        for (MidiEvent event : eventMap.trackEvents) {
            event.accept(drawVisitor);
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
            shooter.draw(renderer);
        }
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
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && (!chromatic)) {
            chromatic = true;
            chromaticTimer = soundEngine.getCurrentTick();
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
                renderer.setColor(Color.WHITE);
                renderer.rect(notePianoX(i), -200f, diatonicWidth - 1, 150f);
            }
        }
        // Black (upper) keys
        for (int i = 21; i < 109; i++) {
            int pitch = i;
            if (Key.isBlackKey(pitch)) {
//                notePatch.setColor(soundEngine.getKey().pitchToScaleDegree(pitch) != -1 ? Color.BLACK : Color.BLACK);
//                notePatch.draw(batch, notePianoX(pitch), -150f, chromaticWidth, 100);
                renderer.setColor(Color.BLACK);
                renderer.rect(notePianoX(pitch), -150f, chromaticWidth, 100);
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
