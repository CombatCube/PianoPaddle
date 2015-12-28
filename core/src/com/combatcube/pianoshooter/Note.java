package com.combatcube.pianoshooter;

/**
 * Class to hold a note event.
 * Created by andrew on 12/25/2015.
 */
public class Note extends Event {
    public int pitch;
    public boolean diatonic;
    public long duration;
    public int velocity;
    public boolean played;
    public boolean missed;

    public Note(int pitch, long onTime, long duration, int velocity) {
        super(onTime);
        this.pitch = pitch;
        this.diatonic = false;
        this.duration = duration;
        this.velocity = velocity;
        this.played = false;
        this.missed = false;
    }

    @Override
    public void performEvent(SoundEngine engine) {
        engine.playNote(this);
    }
}
