package com.combatcube.pianoshooter;

/**
 * Class to contain time-based events.
 * Created by andrew on 12/28/2015.
 */
public abstract class Event implements Comparable<Event> {
    public long tick;

    public Event(long tick) {
        this.tick = tick;
    }

    @Override
    public int compareTo(Event other) {
        return Long.compare(this.tick, other.tick);
    }

    public abstract void performEvent(SoundEngine engine);
}
