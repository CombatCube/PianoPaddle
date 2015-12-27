package com.combatcube.pianoshooter;

import com.badlogic.gdx.Gdx;
import com.leff.midi.MidiFile;

import java.io.IOException;

/**
 * Created by andrew on 12/24/2015.
 */
public abstract class CsoundAdapter {

    public void init() {
        Gdx.files.internal("playmidi.csd").copyTo(Gdx.files.local("tmp/playmidi.csd"));
        if (!Gdx.files.local("tmp/synthgms.sf2").exists()) {
            Gdx.files.internal("synthgms.sf2").copyTo(Gdx.files.local("tmp/synthgms.sf2"));
        }
    }

    public static MidiFile loadMidi(String fileName) throws IOException {
        Gdx.files.internal(fileName).copyTo(Gdx.files.local("tmp/tmp.mid"));
        MidiFile midiFile = new MidiFile(Gdx.files.local("tmp/tmp.mid").file());
        return midiFile;
    }

    public abstract void play();
    public abstract double getTime();
    public abstract void load();
    public abstract void playNote(int inst, double duration, int pitch);
    public abstract void scheduleNote(int inst, double onTime, double duration, int pitch);
}
