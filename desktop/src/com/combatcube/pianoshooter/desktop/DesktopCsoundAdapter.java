package com.combatcube.pianoshooter.desktop;

import com.combatcube.pianoshooter.CsoundAdapter;

import csnd6.*;

/**
 * Created by andrew on 12/26/2015.
 */

public class DesktopCsoundAdapter extends CsoundAdapter {

    private Csound csound;
    private Thread perfThread;
    private String score = "";
    private double play = 1;
    private CsoundMYFLTArray ampChannel;

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
                "  endin");
        score = "i 99 0 360; audio output instrument also keeps performance going\n";
        ampChannel = new CsoundMYFLTArray(1);
        csound.GetChannelPtr(ampChannel.GetPtr(), "amp",
                controlChannelType.CSOUND_CONTROL_CHANNEL.swigValue() |
                        controlChannelType.CSOUND_INPUT_CHANNEL.swigValue());
        perfThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ampChannel.SetValue(0, 0.3);
                ampChannel.SetValue(0, 0.9);
                while (csound.PerformKsmps() == 0) {
                    ampChannel.SetValue(0, play);
//                    double val = ampChannel.GetValue(0);
//                    System.out.println(val);
                }
            }
        });
    }

    @Override
    public void play() {
        csound.ReadScore(score);
        csound.Start();
        perfThread.start();
    }

    @Override
    public double getTime() {
        return csound.GetScoreTime();
    }

    @Override
    public void playNote(int inst, double duration, int pitch) {
        csound.InputMessage("i " + inst + " " + 0 + " " + duration + " " + pitch + " 100");
    }
    @Override
    public void setChannel(String channel, double value) {
        play = value;
    }
    @Override
    public void scheduleNote(int inst, double onTime, double duration, int pitch) {
       score += "i " + inst + " " + onTime + " " + duration + " " + pitch + " 100\n";
    }
}