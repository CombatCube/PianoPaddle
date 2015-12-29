package com.combatcube.pianoshooter;

import com.leff.midi.event.NoteOn;

/**
 * Class to hold a note event.
 * Created by andrew on 12/25/2015.
 */
public class Note implements Comparable<Note> {
    public int pitch;
    public long onTime;
    public long duration;
    public boolean diatonic;
    public boolean played;
    public boolean missed;
    NoteOn noteOn;

    public Note(NoteOn noteOn, long duration) {
        this.pitch = noteOn.getNoteValue();
        this.onTime = noteOn.getTick();
        this.duration = duration;
        this.diatonic = false;
        this.played = false;
        this.missed = false;
        this.noteOn = noteOn;
    }

    @Override
    public int compareTo(Note o) {
        int retVal = Long.compare(this.onTime, o.onTime);
        if (retVal == 0) {
            retVal = Long.compare(this.duration, o.duration);
        }
        if (retVal == 0) {
            retVal = Integer.compare(this.pitch, o.pitch);
        }
        return retVal;
    }

    public void setVelocity(int velocity) {
        noteOn.setVelocity(velocity);
    }
}
