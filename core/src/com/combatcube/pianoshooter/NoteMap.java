package com.combatcube.pianoshooter;

import com.badlogic.gdx.utils.Array;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by andrew on 12/25/2015.
 */
public class NoteMap {
    private TreeSet<Tempo> tempoMap;
    public int minNote;
    public int maxNote;
    private Array<Note> notes;

    public NoteMap() {
        tempoMap = new TreeSet<Tempo>();
        notes = new Array<Note>();
        minNote = Integer.MAX_VALUE;
        maxNote = Integer.MIN_VALUE;
    }

    public NoteMap(MidiTrack track) {
        this();
        if (track != null) {
            Iterator<MidiEvent> it = track.getEvents().iterator();
            HashMap<Integer, NoteOn> noteOns = new HashMap<Integer, NoteOn>();
            while(it.hasNext()) {
                MidiEvent event = it.next();
                if (event instanceof Tempo) {
                    Tempo tempo = (Tempo) event;
                    tempoMap.add(tempo);
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
                        notes.add(new Note(
                                        noteValue,
                                        noteOn.getTick(),
                                        noteOff.getTick() - noteOn.getTick()
                                )
                        );
                    };
                }
            }
        }
        notes.sort();
    }

    public TreeSet<Tempo> getTempoMap() {
        return tempoMap;
    }

    public void setTempoMap(TreeSet<Tempo> tempoMap) {
        this.tempoMap = tempoMap;
    }

    public Array<Note> getNotes() {
        return notes;
    }
}
