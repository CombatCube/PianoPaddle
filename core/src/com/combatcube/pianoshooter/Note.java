package com.combatcube.pianoshooter;

/**
 * Created by andrew on 12/25/2015.
 */
public class Note implements Comparable<Note> {
    public int pitch;
    public boolean diatonic;
    public long onTime;
    public long duration;

    public Note(int pitch, long onTime, long duration) {
        this.pitch = pitch;
        this.diatonic = false;
        this.onTime = onTime;
        this.duration = duration;
    }

    @Override
    public int compareTo(Note other) {
        return Long.compare(this.onTime, other.onTime);
    }
}
