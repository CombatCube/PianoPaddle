package com.combatcube.pianoshooter;

import java.util.LinkedList;

/**
 * Created by Andrew on 1/3/2016.
 */
public class Chord {
    private LinkedList<Note> notes;
    private long tick;
    public int minNote = Integer.MAX_VALUE;
    public int maxNote = Integer.MIN_VALUE;

    public Chord(LinkedList<Note> notes) {
        this.notes = notes;
        this.tick = notes.peekFirst().getTick();
        for (Note note : notes) {
            minNote = Math.min(note.getNoteValue(), minNote);
            maxNote = Math.max(note.getNoteValue(), maxNote);
        }
    }

    public LinkedList<Note> getNotes() {
        return notes;
    }

    public void accept(EventVisitor visitor) {
        visitor.visit(this);
    }

    public long getTick() {
        return tick;
    }
}
