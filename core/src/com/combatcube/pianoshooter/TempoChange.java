package com.combatcube.pianoshooter;

/**
 * Class to contain a tempo change event.
 * Created by andrew on 12/28/2015.
 */
public class TempoChange extends Event {
    public float newBpm;

    public TempoChange(long tick, float bpm) {
        super(tick);
        this.newBpm = bpm;
    }

    @Override
    public void performEvent(SoundEngine engine) {
        engine.setBpm(newBpm);
    }
}
