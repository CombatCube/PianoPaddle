package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class to hold time-based events.
 * Created by andrew on 12/25/2015.
 */
public class EventMap {
    public Array<MidiEvent> events;
    public Array<Note> trackNotes;
    public int minNote;
    public int maxNote;

    public EventMap() {
        events = new Array<MidiEvent>();
        trackNotes = new Array<Note>();
        minNote = Integer.MAX_VALUE;
        maxNote = Integer.MIN_VALUE;
    }

    public void addTrackEvents(MidiTrack track, boolean addNotes) {
        HashMap<Integer, LinkedList<NoteOn>> noteOns = new HashMap<Integer, LinkedList<NoteOn>>();
        if (track != null) {
            for (MidiEvent event : track.getEvents()) {
                events.add(event);
                if (addNotes) {
                    processNoteEvent(noteOns, event);
                }
            }
        }
        events.sort();
        trackNotes.sort();
    }

    private void processNoteEvent(HashMap<Integer, LinkedList<NoteOn>> noteOns, MidiEvent event) {
        if (event instanceof NoteOn) {
            NoteOn noteOn = (NoteOn) event;
            minNote = Math.min(minNote, noteOn.getNoteValue());
            maxNote = Math.max(maxNote, noteOn.getNoteValue());
            int noteValue = noteOn.getNoteValue();
            if (noteOn.getVelocity() != 0) {
                LinkedList<NoteOn> list = noteOns.get(noteValue);
                if (list == null) {
                    list = new LinkedList<NoteOn>();
                }
                list.add(noteOn);
                noteOns.put(noteValue, list);
            } else if (noteOns.get(noteValue) != null) {
                NoteOn noteOn1 = noteOns.get(noteValue).removeFirst();
                makeNote(noteOn1, noteOn.getTick() - noteOn1.getTick());
            }
        }
        if (event instanceof NoteOff) {
            NoteOff noteOff = (NoteOff) event;
            int noteValue = noteOff.getNoteValue();
            if (noteOns.get(noteValue) != null) {
                NoteOn noteOn = noteOns.get(noteValue).removeFirst();
                makeNote(noteOn, noteOff.getTick() - noteOn.getTick());
            }
        }
    }

    private void makeNote(NoteOn noteOn, long duration) {
        Note note = new Note(noteOn, duration);
        trackNotes.add(note);
    }
}
