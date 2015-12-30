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
                        // NoteOn with vel 0
                    } else if (noteOns.get(noteValue) != null) {
                        NoteOn noteOn1 = noteOns.get(noteValue).removeFirst();
                        Note note = new Note(noteOn1, noteOn.getTick() - noteOn1.getTick());
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
                        events.add(note);
                        events.add(noteOff);
                        if (addNotes) {
                            trackNotes.add(note);
                        }
                    }
                } else {
                    events.add(event);
                }
            }
        }
        events.sort();
        trackNotes.sort();
    }

}
