package com.combatcube.pianoshooter.desktop;

import com.combatcube.pianoshooter.CsoundAdapter;

import csnd6.*;

/**
 * Created by andrew on 12/26/2015.
 */

public class DesktopCsoundAdapter extends CsoundAdapter {

    private Csound csound;
    private CsoundPerformanceThread perfThread;
    private String score = "";

    public DesktopCsoundAdapter() {
        csound = new Csound();
    }

    @Override
    public void load() {
        csound.SetOption("-odac");
        csound.SetOption("-B256");
        csound.CompileOrc("sr = 44100\n" +
                "ksmps = 32\n" +
                "nchnls = 2\n" +
                "0dbfs = 1\n" +
                "\n" +
                "giEngine     fluidEngine                                            ; start fluidsynth engine\n" +
                "iSfNum1      fluidLoad          \"synthgms.sf2\", giEngine, 1         ; load a soundfont\n" +
                "             fluidProgramSelect giEngine, 1, iSfNum1, 0, 1         ; direct each midi channel to a particular soundfont\n" +
                "             fluidProgramSelect giEngine, 2, iSfNum1, 0, 1\n" +
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
                "    fluidNote    giEngine, 1, iKey, iVel                            ; apply note to relevant soundfont\n" +
                "  endin\n" +
                "  \n" +
                "  instr 12                                                           ;fluid synths for midi channels 1\n" +
                "    mididefault   60, p3 ; Default duration of 60 -- overridden by score.\n" +
                "    midinoteonkey p4, p5 ; Channels MIDI input to pfields.\n" +
                "    iKey    =    p4                                             ; read in midi note number\n" +
                "    iVel    =    p5                                            ; read in key velocity\n" +
                "    fluidNote    giEngine, 2, iKey, iVel                            ; apply note to relevant soundfont\n" +
                "  endin\n" +
                "\n" +
                "  instr 99; gathering of fluidsynth audio and audio output\n" +
                "    iamplitude = 1\n" +
                "    aSigL,aSigR      fluidOut          giEngine; read all audio from the given soundfont\n" +
                "    outs               aSigL * iamplitude, aSigR * iamplitude; send audio to outputs\n" +
                "  endin");
        score = "i 99 0 360; audio output instrument also keeps performance going\n";
        csound.Start();
        perfThread = new CsoundPerformanceThread(csound);
    }

    @Override
    public void play() {
        csound.ReadScore(score);
        perfThread.Play();
    }

    @Override
    public double getTime() {
        return csound.GetScoreTime();
    }

    @Override
    public void playNote(int inst, double duration, int pitch) {
        perfThread.InputMessage("i " + inst + " " + 0 + " " + duration + " " + pitch + " 100");
    }

    @Override
    public void scheduleNote(int inst, double onTime, double duration, int pitch) {
       score += "i " + inst + " " + onTime + " " + duration + " " + pitch + " 100\n";
    }
}