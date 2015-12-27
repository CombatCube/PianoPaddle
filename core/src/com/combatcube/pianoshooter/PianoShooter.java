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
import com.leff.midi.event.meta.Tempo;

import java.io.IOException;
import java.util.Iterator;

public class PianoShooter extends Game {
    public static final long NANOS_PER_S = 1000000000;
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
    private int nextNoteIndex;

    private NoteMap noteMap;
    private NoteMap otherNoteMap;
    private int range;
    private Array<NoteBox> noteBoxes;
    private int ppq = 480;
    private float bpm = 115;
    private float tempoScale = 1f;

    public boolean isPlaying;
    private long tick = 0;
    private long startTime = TimeUtils.nanoTime();
    private long lastTime = startTime;
    private Key key = new Key(PitchClass.A_FLAT);

    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.csoundAdapter = csoundAdapter;
    }

    @Override
    public void create () {
        csoundAdapter.init();
        loadAssets();
        loadNotes();
        csoundAdapter.load();
        csoundAdapter.setTempo(bpm);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
        shooter = new Shooter(new Rectangle(0, 0, 300, 100));
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
        nextNoteIndex = 0;

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
            if (noteBox.touched) {
                notePatch.setColor(Color.GREEN);
            } else if (noteBox.rect.y > tick){
                if (key.isBlackKey(noteBox.note.pitch)) {
                    notePatch.setColor(Color.BLACK);
                } else {
                        notePatch.setColor(Color.WHITE);
                    }
            } else {
                notePatch.setColor(Color.RED);
            }
            notePatch.draw(batch, noteBox.rect.x, noteBox.rect.y, noteBox.rect.width, noteBox.rect.height);
            // Do collision
            if (noteBox.touchBox.overlaps(shooter.getTouchBox()) && !noteBox.touched) {
				csoundAdapter.playNote(11, 0, (noteBox.note.duration / (float) ppq), noteBox.note.pitch);
                noteBox.touched = true;
            }
        }
        batch.draw(textures.findRegion("rect"), shooter.getRect().getX(), shooter.getRect().getY(), shooter.getRect().getWidth(), shooter.getRect().getHeight());
        batch.end();
        // Debug
//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(Color.MAGENTA);
//        for(NoteBox noteBox : noteBoxes) {
//            renderer.rect(noteBox.rect.x, noteBox.rect.y, noteBox.rect.width, noteBox.rect.height);
//        }
//        renderer.end();
        camera.update();

        // Process input
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        // Move camera and rect according to input
        camera.unproject(touchPos);
        shooter.moveX(touchPos.x - 150);
        shooter.moveY((long) ((tick*tempoScale) - shooter.getRect().height));
        camera.position.y = (tick*tempoScale) + 400;
    }

    private void checkTempo() {
        if (!noteMap.getTempoMap().isEmpty()) {
            Tempo tempo = noteMap.getTempoMap().first();
            if (tick > tempo.getTick()) {
                noteMap.getTempoMap().pollFirst();
                this.bpm = tempo.getBpm();
            }
        }
    }


    private void loadNotes() {
        ppq = midiFile.getResolution();
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
                csoundAdapter.load();

                csoundAdapter.play();
                for (Note note : noteMap.getNotes()) {
                    csoundAdapter.playNote(11, note.onTime / (2 * (float) ppq), note.duration / (2 * (float) ppq), note.pitch);
                }
                // Play accompaniment
                for (Note note : otherNoteMap.getNotes()) {
                    csoundAdapter.playNote(12, note.onTime / (2 * (float) ppq), note.duration / (2 * (float) ppq), note.pitch);
                }
                isPlaying = true;
                // do something important here, asynchronously to the rendering thread
                while(isPlaying) {/// P/Q * Q/M = P/M, P/M / ms/M = P/ms
//                    tick = (long) (TimeUtils.timeSinceNanos(startTime) * (((bpm * ppq) / NANOS_PER_S)/60)) - 1920;
                    tick = (long) (csoundAdapter.getTime() * (2*ppq));
                }
            }
        }).start();
    }
}
