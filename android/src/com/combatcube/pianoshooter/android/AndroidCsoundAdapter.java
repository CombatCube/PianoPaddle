package com.combatcube.pianoshooter.android;

import com.badlogic.gdx.Gdx;
import com.combatcube.pianoshooter.CsoundAdapter;
import com.csounds.CsoundObj;

import java.io.File;

/**
 * Created by andrew on 12/26/2015.
 */
public class AndroidCsoundAdapter extends CsoundAdapter {

    static String OPCODE6DIR;
    private CsoundObj csoundObj;

    public AndroidCsoundAdapter(String nativeLibraryDir) {
        OPCODE6DIR = nativeLibraryDir;
        initCsoundObj();
        csoundObj.setMessageLoggingEnabled(true);
    }

    @Override
    public void load() {
        csoundObj.getCsound().SetOption("-odac");
        csoundObj.getCsound().SetOption("-B512");
        csoundObj.getCsound().SetOption("-+rtmidi=null");
        csoundObj.getCsound().SetOption("-+rtaudio=alsa");
        csoundObj.startCsound(Gdx.files.local("tmp/playmidi.csd").file());
    }

    @Override
    public void setTempo(float tempo) {
        csoundObj.inputMessage("t 0 " + tempo);
    }

    @Override
    public void playNote(int inst, float onTime, float duration, int pitch) {
        csoundObj.inputMessage("i " + inst + " 0 " + duration + " " + pitch + " 100");
    }

    private void initCsoundObj() {
        File file = new File(OPCODE6DIR);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String pluginPath = files[i].getAbsoluteFile()
                    .toString();
            try {
                System.load(pluginPath);
            } catch (Throwable ex) {
            }
        }
        // This must be set before the Csound object is created.
        csnd6.csndJNI.csoundSetGlobalEnv("OPCODE6DIR", OPCODE6DIR);
        csoundObj = new CsoundObj();
    }
}
