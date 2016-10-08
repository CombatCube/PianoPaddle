package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.Controller;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to hold time-based events.
 * Created by andrew on 12/25/2015.
 */
public class EventMap {
    public Array<MidiEvent> events;
    public Array<Note> trackNotes;
    public Array<Chord> trackChords;
    public Array<MidiEvent> trackEvents;
    public int minNote;
    public int maxNote;

    public EventMap() {
        events = new Array<MidiEvent>();
        trackNotes = new Array<Note>();
        trackChords = new Array<Chord>();
        trackEvents = new Array<MidiEvent>();
        minNote = Integer.MAX_VALUE;
        maxNote = Integer.MIN_VALUE;
    }

    public void addTrackEvents(MidiTrack track, boolean addNotes) {
        HashMap<Integer, LinkedList<NoteOn>> noteOns = new HashMap<Integer, LinkedList<NoteOn>>();
        if (track != null) {
            Key key = new Key(PitchClass.C);
            for (MidiEvent event : track.getEvents()) {
                if (event instanceof KeySignature) {
                    KeySignature keySig = (KeySignature) event;
                    key = new Key(PitchClass.C.successor(keySig.getKey()));
                }
                if (event instanceof NoteOn) {
                    NoteOn noteOn = (NoteOn) event;
                    if (addNotes) {
                        minNote = Math.min(minNote, noteOn.getNoteValue());
                        maxNote = Math.max(maxNote, noteOn.getNoteValue());
                    }
                    int noteValue = noteOn.getNoteValue();
                    if (noteOn.getVelocity() != 0) {
                        LinkedList<NoteOn> list = noteOns.get(noteValue);
                        if (list == null) {
                            list = new LinkedList<NoteOn>();
                        }
                        list.add(noteOn);
                        noteOns.put(noteValue, list);
                        // NoteOn with vel 0
                    } else if (noteOns.get(noteValue) != null
                            && !noteOns.get(noteValue).isEmpty()) {
                        NoteOn noteOn1 = noteOns.get(noteValue).removeFirst();
                        Note note = new Note(noteOn1, noteOn.getTick() - noteOn1.getTick());
                        note.scaleDegree = key.pitchToScaleDegree(note.getNoteValue());
                        events.add(note);
                        events.add(noteOn);
                        if (addNotes) {
                            trackNotes.add(note);
                        }
                    }
                } else if (event instanceof NoteOff) {
                    NoteOff noteOff = (NoteOff) event;
                    int noteValue = noteOff.getNoteValue();
                    if (noteOns.get(noteValue) != null
                            && !noteOns.get(noteValue).isEmpty()) {
                        NoteOn noteOn = noteOns.get(noteValue).removeFirst();
                        Note note = new Note(noteOn, noteOff.getTick() - noteOn.getTick());
                        note.scaleDegree = key.pitchToScaleDegree(note.getNoteValue());
                        events.add(note);
                        events.add(noteOff);
                        if (addNotes) {
                            trackNotes.add(note);
                        }
                    }
                } else if (event instanceof Controller) {
                    Controller cc = (Controller) event;
                    events.add(cc);
                } else {
                    events.add(event);
                    trackEvents.add(event);
                }
            }
        }
        events.sort();
        trackNotes.sort();
    }

    public void findIntervals(Array<Note> notes) {
        LinkedList<Note> notesInBeat = new LinkedList<Note>();
        LinkedList<Note> chord = new LinkedList<Note>();
        long currentBeat = 0;
        long chordTick = 0;
        int minNote = Integer.MAX_VALUE;
        int maxNote = Integer.MIN_VALUE;
        int maxInterval = 0;
        for (Note note : notes) {
            long tick = note.getTick();
            // Note is in same chord
            if (tick <= chordTick + 60) {
                minNote = Math.min(minNote, note.getNoteValue());
                maxNote = Math.max(maxNote, note.getNoteValue());
                maxInterval = Math.max(maxNote - minNote, maxInterval);
                chordTick = tick;
            }
            // Note is in same beat, but not in chord
            else if (tick < currentBeat + 480) {
                for (Note noteInChord : chord) {
                    noteInChord.interval = maxNote - minNote;
                }
                minNote = note.getNoteValue();
                maxNote = note.getNoteValue();
                if (!chord.isEmpty()) {
                    trackChords.add(new Chord(chord));
                }
                chord = new LinkedList<Note>();
                chordTick = tick;
                // Note is not in same beat
            } else {
                for (Note noteInChord : chord) {
                    noteInChord.interval = maxNote - minNote;
                }
                for (Note noteInBeat : notesInBeat) {
                    noteInBeat.interval = maxInterval;
                }
                minNote = note.getNoteValue();
                maxNote = note.getNoteValue();
                if (!chord.isEmpty()) {
                    trackChords.add(new Chord(chord));
                }
                chord = new LinkedList<Note>();
                chordTick = tick;
                maxInterval = 0;
                notesInBeat.clear();
                currentBeat = (tick / 480) * 480;
            }
            chord.add(note);
            notesInBeat.add(note);
        }
        for (Note noteInChord : chord) {
            noteInChord.interval = maxNote - minNote;
        }
        for (Note noteInBeat : notesInBeat) {
            noteInBeat.interval = maxInterval;
        }
    }

}
