package com.combatcube.pianoshooter;

/**
 * Class to hold a key change event.
 * Created by andrew on 12/28/2015.
 */
public class KeyChange extends Event {
    public Key newKey;

    public KeyChange(long tick, Key newKey) {
        super(tick);
        this.newKey = newKey;
    }

    @Override
    public void performEvent(SoundEngine engine) {
        engine.setKey(newKey);
    }
}
