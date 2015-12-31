package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class to hold time-based events.
 * Created by andrew on 12/25/2015.
 */
public class EventMap {
    public Array<MidiEvent> events;
    public Array<Note> trackNotes;
    public Array<MidiEvent> trackEvents;
    public int minNote;
    public int maxNote;

    public EventMap() {
        events = new Array<MidiEvent>();
        trackNotes = new Array<Note>();
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
                    if (noteOns.get(noteValue) != null) {
                        NoteOn noteOn = noteOns.get(noteValue).removeFirst();
                        Note note = new Note(noteOn, noteOff.getTick() - noteOn.getTick());
                        note.scaleDegree = key.pitchToScaleDegree(note.getNoteValue());
                        events.add(note);
                        events.add(noteOff);
                        if (addNotes) {
                            trackNotes.add(note);
                        }
                    }
                } else {
                    events.add(event);
                    trackEvents.add(event);
                }
            }
        }
        events.sort();
        trackNotes.sort();
    }

}
