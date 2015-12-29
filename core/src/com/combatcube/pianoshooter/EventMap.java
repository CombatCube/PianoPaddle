package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Tempo;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Class to hold time-based events.
 * Created by andrew on 12/25/2015.
 */
public class EventMap {
    public Array<Event> events;
    public Array<Note> trackNotes;
    public int minNote;
    public int maxNote;

    public EventMap() {
        events = new Array<Event>();
        trackNotes = new Array<Note>();
        minNote = Integer.MAX_VALUE;
        maxNote = Integer.MIN_VALUE;
    }

    public void addTrackEvents(MidiTrack track, boolean addNotes) {
        if (track != null) {
            Iterator<MidiEvent> it = track.getEvents().iterator();
            HashMap<Integer, NoteOn> noteOns = new HashMap<Integer, NoteOn>();
            while(it.hasNext()) {
                MidiEvent event = it.next();
                if (event instanceof Tempo) {
                    Tempo tempo = (Tempo) event;
                    events.add(new TempoChange(tempo.getTick(), tempo.getBpm()));
                }
                if (event instanceof KeySignature) {
                    KeySignature keySig = (KeySignature) event;
                    events.add(new KeyChange(keySig.getTick(),
                            new Key(PitchClass.C.successor(keySig.getKey()))));
                }
                if (event instanceof NoteOn) {
                    NoteOn noteOn = (NoteOn) event;
                    minNote = Math.min(minNote, noteOn.getNoteValue());
                    maxNote = Math.max(maxNote, noteOn.getNoteValue());
                    if (noteOn.getVelocity() != 0) {
                        noteOns.put(noteOn.getNoteValue(), noteOn);
                    } else {
                        event = new NoteOff(noteOn.getTick(), noteOn.getChannel(), noteOn.getNoteValue(), noteOn.getVelocity());
                    }
                }
                if (event instanceof NoteOff) {
                    NoteOff noteOff = (NoteOff) event;
                    int noteValue = noteOff.getNoteValue();
                    if (noteOns.get(noteValue) != null) {
                        NoteOn noteOn = noteOns.remove(noteValue);
                        Note note = new Note(
                                noteValue,
                                noteOn.getTick(),
                                noteOff.getTick() - noteOn.getTick(),
                                noteOn.getVelocity());
                        events.add(note);
                        if (addNotes) {
                            trackNotes.add(note);
                        }
                    }
                }
            }
        }
        events.sort();
        trackNotes.sort();
    }
}
