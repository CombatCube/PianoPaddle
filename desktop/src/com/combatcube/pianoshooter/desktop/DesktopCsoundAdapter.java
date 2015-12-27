package com.combatcube.pianoshooter.desktop;

import com.combatcube.pianoshooter.CsoundAdapter;

import csnd6.*;

/**
 * Created by andrew on 12/26/2015.
 */

public class DesktopCsoundAdapter extends CsoundAdapter {

    private Csound csound;
    private CsoundMYFLTArray ampChannel;
    private Thread perfThread;

    public DesktopCsoundAdapter() {
        csound = new Csound();
    }

    @Override
    public void load() {
        csound.SetOption("-odac");
        csound.SetOption("-B256");
        csound.CompileOrc(ORCHESTRA);
        score = "i 99 0 360; audio output instrument also keeps performance going\n";
        ampChannel = new CsoundMYFLTArray(1);
        csound.GetChannelPtr(ampChannel.GetPtr(), "amp",
                controlChannelType.CSOUND_CONTROL_CHANNEL.swigValue() |
                        controlChannelType.CSOUND_INPUT_CHANNEL.swigValue());
        perfThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (csound.PerformKsmps() == 0) {
                    ampChannel.SetValue(0, amp);
//                    double val = ampChannel.GetValue(0);
//                    System.out.println(val);
                }
            }
        });
    }

    @Override
    public void play() {
        csound.Start();
        perfThread.start();
    }

    @Override
    public double getTime() {
        return csound.GetScoreTime();
    }

    @Override
    public void playNote(int inst, double duration, int pitch) {
        csound.InputMessage(String.format(iStatement, inst, 0, duration, pitch));
    }

    @Override
    public void setupScore() {
        csound.ReadScore(score);
    }
}