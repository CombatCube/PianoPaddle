package com.combatcube.pianoshooter;

import com.badlogic.gdx.Gdx;
import com.leff.midi.MidiFile;

import java.io.IOException;

/**
 * Adapter for Csound, a low-level sound processing engine
 * Created by andrew on 12/24/2015.
 */
public abstract class CsoundAdapter {
    public static final String ORCHESTRA = "sr = 44100\n" +
            "ksmps = 32\n" +
            "nchnls = 2\n" +
            "0dbfs = 1\n" +
            "\n" +
            "giEngine1     fluidEngine                                            ; start fluidsynth engine\n" +
            "giEngine2     fluidEngine                                            ; start fluidsynth engine\n" +
            "iSfNum1      fluidLoad          \"synthgms.sf2\", giEngine1, 1         ; load a soundfont\n" +
            "iSfNum2      fluidLoad          \"synthgms.sf2\", giEngine2, 1         ; load a soundfont\n" +
            "             fluidProgramSelect giEngine1, 1, iSfNum1, 0, 1         ; direct each midi channel to a particular soundfont\n" +
            "             fluidProgramSelect giEngine2, 2, iSfNum2, 0, 1\n" +
            "\n" +
            "  massign 0,0\n" +
            "  massign 1,11\n" +
            "  massign 2,12\n" +
            "\n" +
            "  instr 11                                                           ;fluid synths for midi channels 1\n" +
            "    ;mididefault   60, p3 ; Default duration of 60 -- overridden by score.\n" +
            "    midinoteonkey p4, p5 ; Channels MIDI input to pfields.\n" +
            "    iKey    =    p4                                           ; read in midi note number\n" +
            "    iVel    =    p5                                            ; read in key velocity\n" +
            "    fluidNote    giEngine1, 1, iKey, iVel                            ; apply note to relevant soundfont\n" +
            "  endin\n" +
            "  \n" +
            "  instr 12                                                           ;fluid synths for midi channels 1\n" +
            "    ;mididefault   60, p3 ; Default duration of 60 -- overridden by score.\n" +
            "    midinoteonkey p4, p5 ; Channels MIDI input to pfields.\n" +
            "    iKey    =    p4                                             ; read in midi note number\n" +
            "    iVel    =    p5                                            ; read in key velocity\n" +
            "    fluidNote    giEngine2, 2, iKey, iVel                            ; apply note to relevant soundfont\n" +
            "  endin\n" +
            "\n" +
            "  instr 99; gathering of fluidsynth audio and audio output\n" +
            "    kamplitude1 chnget    \"amp\"\n" +
            "    ;kamplitude1 = 1\n" +
            "    kamplitude2 = 1\n" +
            "    aSigL1,aSigR1      fluidOut          giEngine1; read all audio from the given soundfont\n" +
            "    aSigL2,aSigR2      fluidOut          giEngine2; read all audio from the given soundfont\n" +
            "    outs               (aSigL1 * kamplitude1) + (aSigL2 * kamplitude2), \\\n" +
            "                       (aSigR1 * kamplitude1) + (aSigR2 * kamplitude2)\n" +
            "  endin";
    protected static final String iStatement = "i %d %f %f %d %d\n";
    protected String score = "";
    protected double amp = 1.0;

    public static MidiFile loadMidi(String fileName) throws IOException {
        Gdx.files.internal(fileName).copyTo(Gdx.files.local("tmp/tmp.mid"));
        return new MidiFile(Gdx.files.local("tmp/tmp.mid").file());
    }

    public void init() {
        Gdx.files.internal("playmidi.csd").copyTo(Gdx.files.local("tmp/playmidi.csd"));
        if (!Gdx.files.local("tmp/synthgms.sf2").exists()) {
            Gdx.files.internal("synthgms.sf2").copyTo(Gdx.files.local("tmp/synthgms.sf2"));
        }
    }

    public abstract void start();
    public abstract double getTime();
    public abstract void load();

    public abstract void playNote(int inst, double duration, int pitch, int velocity);

    public void setAmpValue(double value) {
        amp = value;
    }

    public abstract void readScore();
}
