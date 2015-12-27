package com.combatcube.pianoshooter.desktop;

import com.combatcube.pianoshooter.CsoundAdapter;

import csnd6.*;

/**
 * Created by andrew on 12/26/2015.
 */

public class DesktopCsoundAdapter extends CsoundAdapter {

    private Csound csound;
    private CsoundPerformanceThread perfThread;

    public DesktopCsoundAdapter() {
        csound = new Csound();
    }

    @Override
    public void load() {
        csound.SetOption("-odac");
        csound.SetOption("-B256");
        csound.Compile("playmidi.csd");
        csound.Start();
        perfThread = new CsoundPerformanceThread(csound);
    }

    @Override
    public void play() {
        perfThread.FlushMessageQueue();
        perfThread.Play();
    }

    @Override
    public double getTime() {
        return csound.GetScoreTime();
    }

    @Override
    public void setTempo(float tempo) {
//        perfThread.InputMessage("t 0 " + tempo);
    }

    @Override
    public void playNote(int inst, float onTime, float duration, int pitch) {
        perfThread.InputMessage("i " + inst + " " + onTime + " " + duration + " " + pitch + " 100");
    }
}