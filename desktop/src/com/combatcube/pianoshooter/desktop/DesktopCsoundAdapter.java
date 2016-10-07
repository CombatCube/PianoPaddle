package com.combatcube.pianoshooter.desktop;

import com.combatcube.pianoshooter.CsoundAdapter;

import csnd6.Csound;
import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;

/**
 * Desktop implementation of Csound class.
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
        csound.SetOption("-+rtaudio=mme"); // for Windows; use CoreAudio? for Mac, PortAudio for Linux
        csound.SetOption("-odac");
        csound.SetOption("-b512");
        csound.SetOption("-B2048");
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
                    ampChannel.SetValue(0, 1.0);
//                    double val = ampChannel.GetValue(0);
//                    System.out.println(val);
                }
            }
        });
    }

    @Override
    public void start() {
        csound.Start();
        perfThread.start();
    }

    @Override
    public double getTime() {
        return csound.GetScoreTime();
    }

    @Override
    public void playNote(int inst, double duration, int pitch, int velocity) {
        csound.InputMessage(String.format(iStatement, inst, 0.0f, duration, pitch, velocity));
    }

    @Override
    public void readScore() {
        csound.ReadScore(score);
    }

    @Override
    public void stop() {
        csound.Stop();
    }


}