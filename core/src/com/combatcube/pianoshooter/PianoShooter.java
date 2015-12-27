package com.combatcube.pianoshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
    private Shooter shooter;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer renderer;
    private TextureAtlas textures;
    private NinePatch notePatch;
    private MidiFile midiFile;
    private MidiTrack mainTrack;
    private MidiTrack otherTrack;

    private NoteMap noteMap;
    private NoteMap otherNoteMap;
    private int range;
    private Array<NoteBox> noteBoxes;

    private float bpm = 100;
    private float tempoScale = 1f;

    public boolean isPlaying;
    private long ppq = 480;
    private double currentTick = -COUNT_IN * ppq;
    private Key key = new Key(PitchClass.A_FLAT);
    private long startTime;

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
        shooter = new Shooter(new Rectangle(0, (float)currentTick, 300, 100));
        noteBoxes = new Array<NoteBox>();

        float chromaticWidth = 1600/(float)(range+1);
        float diatonicWidth = chromaticWidth*12/(float)7;
        for(Note note : noteMap.getNotes()) {
            int scaleDegree = key.pitchToScaleDegree(note.pitch);
            float width = chromaticWidth;
            float x = (note.pitch - noteMap.minNote)*chromaticWidth;
            if (scaleDegree != -1) {
                note.diatonic = true;
            }
            if (note.diatonic) {
                width = diatonicWidth;
                x = (note.pitch - noteMap.minNote)*chromaticWidth;
                switch (scaleDegree) {
//                    case 0: // Do
//                        x -= diatonicWidth / 2;
                    case 1: // Re
                        x -= chromaticWidth*2/(float)7;
                        break;
                    case 2: // Mi
                        x -= chromaticWidth*4/(float)7;
                        break;
                    case 3: // Fa
                        x += chromaticWidth*1/(float)7;
                        break;
                    case 4: // So
                        x -= chromaticWidth*1/(float)7;
                        break;
                    case 5: // La
                        x -= chromaticWidth*3/(float)7;
                        break;
                    case 6: // Ti
                        x -= chromaticWidth*5/(float)7;
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

//        checkTempo();
        Iterator<NoteBox> iter = noteBoxes.iterator();
        while(iter.hasNext()) {
            NoteBox noteBox = iter.next();
            setNoteColor(noteBox);
            notePatch.draw(batch, noteBox.rect.x, noteBox.rect.y, noteBox.rect.width, noteBox.rect.height);
            // Do collision
//            if (noteBox.touchBox.overlaps(shooter.getTouchBox()) && !noteBox.touched) {
            if (noteBox.note.onTime < currentTick) {
                if (!noteBox.passed && noteBox.rect.overlaps(shooter.getRect())) {
                    csoundAdapter.playNote(11, ticksToSeconds(noteBox.note.duration), noteBox.note.pitch);
                    noteBox.touched = true;
                }
                noteBox.passed = true;
            }
        }
        batch.draw(textures.findRegion("rect"), shooter.getRect().getX(), shooter.getRect().getY(), shooter.getRect().getWidth(), shooter.getRect().getHeight());
        batch.end();
        // Debug
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.MAGENTA);
//        for(NoteBox noteBox : noteBoxes) {
//            renderer.rect(noteBox.rect.x, noteBox.rect.y, noteBox.rect.width, noteBox.rect.height);
//        }
        renderer.line(0f, (float)currentTick, 0f, 1600f, (float)currentTick, 0f);
        renderer.end();
        camera.update();

        // Process input
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        // Move camera and rect according to input
        camera.unproject(touchPos);
        shooter.moveX(touchPos.x - shooter.getRect().getWidth()/2);
//        shooter.moveY((float) (currentTick - shooter.getRect().height));
//        camera.position.y = (float) (currentTick + 200);
//        float newY = (float) secondsToTicks((TimeUtils.millis() - startTime)/(float)MILLIS_PER_S);
        if (isPlaying) {
            shooter.moveY((float) currentTick - shooter.getRect().getHeight() + 50);
            camera.position.y = shooter.getRect().y;
        }
    }

    private void setNoteColor(NoteBox noteBox) {
        if (noteBox.touched) {
            notePatch.setColor(Color.GREEN);
        } else if (noteBox.rect.y > currentTick){
            if (key.isBlackKey(noteBox.note.pitch)) {
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
        mainTrack = SoundEngine.getTrack(midiFile, 0);
        otherTrack = SoundEngine.getTrack(midiFile, 1);
        noteMap = new NoteMap(mainTrack);
        otherNoteMap = new NoteMap(otherTrack);
        range = noteMap.maxNote - noteMap.minNote;
//        key = PitchClass.C;
    }

    private void loadAssets() {
        textures = new TextureAtlas("textures.pack");
        notePatch = textures.createPatch("notepatch");
        notePatch.setColor(Color.WHITE);
        try {
            midiFile = CsoundAdapter.loadMidi("Spider_Dance.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPlaying() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                for (Note note : noteMap.getNotes()) {
//                    csoundAdapter.scheduleNote(11, ticksToSeconds(note.onTime), ticksToSeconds(note.duration), note.pitch);
//                }
                for (Note note : otherNoteMap.getNotes()) {
                    csoundAdapter.scheduleNote(12, ticksToSeconds((double) note.onTime + COUNT_IN*ppq), ticksToSeconds((double) note.duration), note.pitch);
                }
                startTime = TimeUtils.millis();
                isPlaying = true;
                csoundAdapter.play();
                // Correction factor = time since starting - actual time
                long offset = (long) (TimeUtils.timeSinceMillis(startTime)
                                - (csoundAdapter.getTime() * MILLIS_PER_S));
                while(isPlaying) {
                    currentTick = secondsToTicks(TimeUtils.timeSinceMillis(startTime + offset) / (double) MILLIS_PER_S) - COUNT_IN*ppq;
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
