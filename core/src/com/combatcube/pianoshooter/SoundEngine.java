package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.TimeUtils;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Tempo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundEngine extends Thread {
    public static final long MILLIS_PER_S = 1000;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int DEFAULT_PPQ = 480;
    public static final int COUNT_IN = 4;
    private CsoundAdapter csoundAdapter;

    private Key key = new Key(PitchClass.G_FLAT);
    private int range;

    private float bpm = 140;
    private float tempoScale = 1f;

    private long ppq = 480;
    private double currentTick = -COUNT_IN * ppq;

    private MidiFile midiFile;
    private EventMap mainEventMap;
    private EventMap otherEventMap;
    private boolean isPlaying;

    public SoundEngine(CsoundAdapter csoundAdapter) {
        isPlaying = false;
        this.csoundAdapter = csoundAdapter;
        csoundAdapter.init();
        try {
            midiFile = CsoundAdapter.loadMidi("Death_by_Glamour.mid");
        } catch (IOException e) {
            e.printStackTrace();
        }
        readNotes();
    }

    public static MidiTrack getTrack(MidiFile midiFile, int trackNo) {
        if (trackNo < 0 || trackNo > midiFile.getTrackCount()) {
            return null;
        }
        MidiTrack track = midiFile.getTracks().get(trackNo);
        track.dumpEvents();
        Iterator<MidiEvent> it = track.getEvents().iterator();
        List<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();
        while (it.hasNext()) {
            MidiEvent event = it.next();
            if (!(event instanceof NoteOn)
                    && !(event instanceof NoteOff)
                    && !(event instanceof Tempo)
                    && !(event instanceof KeySignature)) {
                eventsToRemove.add(event);
            }
        }
        for (MidiEvent event : eventsToRemove) {
            track.removeEvent(event);
        }
        return track;
    }

    private void readNotes() {
        MidiTrack mainTrack = SoundEngine.getTrack(midiFile, 0);
        MidiTrack otherTrack = SoundEngine.getTrack(midiFile, 1);
        mainEventMap = new EventMap(mainTrack);
        otherEventMap = new EventMap(otherTrack);
    }

    public void startPlaying() {
        csoundAdapter.load();
        this.start();
    }

    public double getCurrentTick() {
        return currentTick;
    }

    @Override
    public void run() {
        csoundAdapter.readScore();
        isPlaying = true;
        csoundAdapter.start();
        long prevTime = TimeUtils.millis();
        Iterator<Event> it = otherEventMap.events.iterator();
        Event nextEvent = it.next(); //assumes first note exists
        while (isPlaying) {
            long newTime = TimeUtils.millis();
            currentTick += secondsToTicks((newTime - prevTime) / (double) MILLIS_PER_S);
            prevTime = newTime;
            if (nextEvent != null) {
                if (nextEvent.tick < currentTick
                        || (nextEvent instanceof KeyChange)) {
                    nextEvent.performEvent(this);
                    if (it.hasNext()) {
                        nextEvent = it.next();
                    } else {
                        nextEvent = null;
                    }
                }
            }
        }
    }

    public float getBpm() {
        return bpm;
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public long getPpq() {
        return ppq;
    }

    public double ticksToSeconds(double tick) {
        // Q/M * T/Q = T/M
        // T / (T/M) = M
        // M * S/M = S
        return (tick / ((double) ppq * (double) bpm) * (double) SECONDS_PER_MINUTE);
    }

    public double secondsToTicks(double seconds) {
        // S / S/M = M
        // M * Q/M = Q
        // Q * T/Q = T
        return (seconds / (double) SECONDS_PER_MINUTE) * (double) bpm * (double) ppq;
    }

    public int getRange() {
        return range;
    }

    public void playNote(Note note) {
        csoundAdapter.playNote(12, ticksToSeconds(note.duration), note.pitch, note.velocity);
    }

    public EventMap getMainEventMap() {
        return mainEventMap;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
