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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.leff.midi.*;

import java.io.IOException;
import java.util.Iterator;

public class PianoShooter extends Game {
    public static final long MILLIS_PER_S = 1000;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int DEFAULT_PPQ = 480;
    public static final int COUNT_IN = 4;
    private CsoundAdapter csoundAdapter;
    private Shooter primaryShooter;
    private Shooter secondaryShooter;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private NinePatch notePatch;
    private MidiFile midiFile;

    private NoteMap noteMap;
    private NoteMap otherNoteMap;
    private int range;
    private Array<NoteBox> noteBoxes;

    private float bpm = 140;
    private float tempoScale = 1f;

    public boolean isPlaying;
    private long ppq = 480;
    private double currentTick = -COUNT_IN * ppq;
    private Key key = new Key(PitchClass.G_FLAT);
    private float diatonicWidth;
    private float chromaticWidth;
    private float shooterWidth;
    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        csoundAdapter.init();
        loadAssets();
        loadNotes();
        csoundAdapter.load();
        ppq = midiFile.getResolution();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        chromaticWidth = 1600/(float)(range+1);
        diatonicWidth = chromaticWidth *12/(float)7;
        shooterWidth = diatonicWidth*5;
        primaryShooter = new Shooter(new Rectangle(0, (float) currentTick, shooterWidth, 100));
        secondaryShooter = new Shooter(new Rectangle(0, (float) currentTick, shooterWidth/2, 100));
        noteBoxes = new Array<NoteBox>();

        for(Note note : noteMap.getNotes()) {
            int scaleDegree = key.pitchToScaleDegree(note.pitch);
            float width = chromaticWidth;
            float x = (note.pitch - noteMap.minNote)* chromaticWidth;
            if (scaleDegree != -1) {
                note.diatonic = true;
            }
            if (note.diatonic) {
                width = diatonicWidth;
                x = (note.pitch - noteMap.minNote)* chromaticWidth;
                switch (scaleDegree) {
//                    case 0: // Do
//                        x -= diatonicWidth / 2;
                    case 1: // Re
                        x -= chromaticWidth *2/(float)7;
                        break;
                    case 2: // Mi
                        x -= chromaticWidth *4/(float)7;
                        break;
                    case 3: // Fa
                        x += chromaticWidth *1/(float)7;
                        break;
                    case 4: // So
                        x -= chromaticWidth *1/(float)7;
                        break;
                    case 5: // La
                        x -= chromaticWidth *3/(float)7;
                        break;
                    case 6: // Ti
                        x -= chromaticWidth *5/(float)7;
                        break;
                }
            }
            NoteBox noteBox = new NoteBox(note, new Rectangle(
                    x,
                    note.onTime,
                    width,
                    note.duration));
            noteBoxes.add(noteBox);
        }
//        checkTempo();
        startPlaying();
    }


    @Override
    public void render () {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        batch.begin();

        for (NoteBox noteBox : noteBoxes) {
            setNoteColor(noteBox);
            notePatch.draw(batch, noteBox.rect.x, noteBox.rect.y, noteBox.rect.width, noteBox.rect.height);
            // Do collision
//            if (noteBox.touchBox.overlaps(shooter.getTouchBox()) && !noteBox.touched) {
            if (!noteBox.passed && noteBox.rect.y <= currentTick) {
                if (primaryShooter.contains(noteBox)
                        || (secondaryShooter.isAvailable() && secondaryShooter.contains(noteBox))) {
                    csoundAdapter.playNote(11, ticksToSeconds(noteBox.note.duration), noteBox.note.pitch);
//                    csoundAdapter.setAmpValue(1.0);
                    noteBox.touched = true;
                }
                noteBox.passed = true;
            }
        }
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(Color.YELLOW);
        renderer.rect(primaryShooter.getTouchBox().x, primaryShooter.getTouchBox().y, primaryShooter.getTouchBox().width, primaryShooter.getTouchBox().height);
        if (secondaryShooter.isAvailable()) {
            renderer.rect(secondaryShooter.getTouchBox().x, secondaryShooter.getTouchBox().y, secondaryShooter.getTouchBox().width, secondaryShooter.getTouchBox().height);
        }
        renderer.end();

        // Process input

        // Move camera and shooters according to input

        if (!Gdx.input.isPeripheralAvailable(Input.Peripheral.MultitouchScreen)) {
            // Mouse input
            Vector3 mousePos = new Vector3();
            mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            float mouseCenterX = (mousePos.x)*(1600/(float)Gdx.graphics.getWidth());
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                primaryShooter.setWidth((int) shooterWidth / 2);
                primaryShooter.moveCenterX(mouseCenterX + 3.5f * diatonicWidth);
                secondaryShooter.setIsAvailable(true);
            } else {
                primaryShooter.setWidth((int) (shooterWidth));
                primaryShooter.moveCenterX(mouseCenterX);
                secondaryShooter.setIsAvailable(false);
            }
            secondaryShooter.moveX(primaryShooter.getRect().getX() - 7 * diatonicWidth);
        } else {
            // Touch input
            Vector3 touch0Pos = new Vector3();
            Vector3 touch1Pos = new Vector3();
            touch0Pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            if (Gdx.input.isTouched(1)) {
                touch1Pos.set(Gdx.input.getX(1), Gdx.input.getY(1), 0);
                primaryShooter.setWidth((int) shooterWidth / 2);
                secondaryShooter.moveCenterX((touch1Pos.x) * (1600 / (float) Gdx.graphics.getWidth()));
                secondaryShooter.setIsAvailable(true);
            } else {
                primaryShooter.setWidth((int) (shooterWidth));
                secondaryShooter.setIsAvailable(false);
            }
            primaryShooter.moveCenterX((touch0Pos.x) * (1600 / (float) Gdx.graphics.getWidth()));
        }
        primaryShooter.moveY((float) currentTick - primaryShooter.getRect().getHeight());
        secondaryShooter.moveY(primaryShooter.getRect().getY());
        camera.position.y = primaryShooter.getRect().y + 400;
        camera.update();
    }

    private void setNoteColor(NoteBox noteBox) {
        if (noteBox.touched) {
            notePatch.setColor(Color.GREEN);
        } else if (noteBox.rect.y > currentTick){
            if (Key.isBlackKey(noteBox.note.pitch)) {
                notePatch.setColor(Color.BLACK);
            } else {
                notePatch.setColor(Color.WHITE);
            }
        } else {
            notePatch.setColor(Color.RED);
        }
    }

//    private void checkTempo() {
//        if (!noteMap.getTempoMap().isEmpty()) {
//            Tempo tempo = noteMap.getTempoMap().first();
//            if (currentTick > tempo.getTick()) {
//                noteMap.getTempoMap().pollFirst();
//                this.bpm = tempo.getBpm();
//            }
//        }
//    }


    private void loadNotes() {
        MidiTrack mainTrack = SoundEngine.getTrack(midiFile, 0);
        MidiTrack otherTrack = SoundEngine.getTrack(midiFile, 1);
        noteMap = new NoteMap(mainTrack);
        otherNoteMap = new NoteMap(otherTrack);
        range = noteMap.maxNote - noteMap.minNote;
//        key = PitchClass.C;
    }

    private void loadAssets() {
        TextureAtlas textures = new TextureAtlas("textures.pack");
        notePatch = textures.createPatch("notepatch");
        notePatch.setColor(Color.WHITE);
        try {
            midiFile = CsoundAdapter.loadMidi("Death_by_Glamour.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                for (Note note : noteMap.getNotes()) {
//                    csoundAdapter.scheduleNote(11, ticksToSeconds((double) note.onTime + COUNT_IN*ppq), ticksToSeconds(note.duration), note.pitch);
//                }
//                for (Note note : otherNoteMap.getNotes()) {
//                    csoundAdapter.scheduleNote(12, ticksToSeconds((double) note.onTime + COUNT_IN*ppq), ticksToSeconds((double) note.duration), note.pitch);
//                }
                csoundAdapter.readScore();
                isPlaying = true;
                csoundAdapter.start();
                long prevTime = TimeUtils.millis();
                Iterator<Note> it = otherNoteMap.getNotes().iterator();
                Note nextNote = it.next(); //assumes first note exists
                while(isPlaying) {
                    long newTime = TimeUtils.millis();
                    currentTick += secondsToTicks((newTime - prevTime) / (double) MILLIS_PER_S);
                    prevTime = newTime;
                    if (nextNote != null && nextNote.onTime < currentTick) {
                        csoundAdapter.playNote(12, ticksToSeconds(nextNote.duration), nextNote.pitch);
                        if(it.hasNext()) {
                            nextNote = it.next();
                        } else {
                            nextNote = null;
                        }
                    }
                }
            }
        }).start();
    }

    public double ticksToSeconds(double tick) {
        // Q/M * T/Q = T/M
        // T / (T/M) = M
        // M * S/M = S
        return (tick / ((double)ppq * (double)bpm)) * (double)SECONDS_PER_MINUTE;
    }

    public double secondsToTicks(double seconds) {
        // S / S/M = M
        // M * Q/M = Q
        // Q * T/Q = T
        return (seconds / (double)SECONDS_PER_MINUTE) * (double)bpm * (double)ppq;
    }
}
