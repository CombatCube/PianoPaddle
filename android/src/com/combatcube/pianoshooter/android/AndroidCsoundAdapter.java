package com.combatcube.pianoshooter.android;

import com.badlogic.gdx.Gdx;
import com.combatcube.pianoshooter.CsoundAdapter;
import com.csounds.CsoundObj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import csnd6.CsoundMYFLTArray;
import csnd6.controlChannelType;

/**
 * Android implementation of Csound class.
 * Created by andrew on 12/26/2015.
 */
public class AndroidCsoundAdapter extends CsoundAdapter {

    static String OPCODE6DIR;
    private CsoundObj csoundObj;
    private CsoundMYFLTArray ampChannel;
    private Thread perfThread;

    public AndroidCsoundAdapter(String nativeLibraryDir) {
        OPCODE6DIR = nativeLibraryDir;
        initCsoundObj();
        csoundObj.setMessageLoggingEnabled(true);
    }

    @Override
    public void start() {
        ampChannel = csoundObj.getInputChannelPtr("amp", controlChannelType.CSOUND_CONTROL_CHANNEL);
        perfThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!csoundObj.isStopped()) {
                    ampChannel.SetValue(0, amp);
                }
            }
        });
        csoundObj.play();
        perfThread.start();
    }

    @Override
    public double getTime() {
        return csoundObj.getCsound().GetScoreTime();
    }

    @Override
    public void load() {
        csoundObj.getCsound().SetOption("-odac");
        csoundObj.getCsound().SetOption("-b256");
        csoundObj.getCsound().SetOption("-B2048");
        csoundObj.getCsound().SetOption("-+rtmidi=null");
        csoundObj.getCsound().SetOption("-+rtaudio=alsa");
    }

    @Override
    public void playNote(int inst, double duration, int pitch, int velocity) {
        csoundObj.inputMessage(String.format(iStatement, inst, 0.0f, duration, pitch, velocity));
    }

    @Override
    public void readScore() {
        createTempFile(
                "<CsoundSynthesizer>\n" +
                        "\n" +
                        "<CsInstruments>\n" +
                        ORCHESTRA + "\n" +
                        "</CsInstruments>\n" +
                        "<CsScore>\n" +
                        "i 99 0 360; audio output instrument also keeps performance going\n" +
                        score + "\n" +
                        "e\n" +
                        "</CsScore>\n" +
                        "\n" +
                        "</CsoundSynthesizer>"
        );
        csoundObj.startCsound(Gdx.files.local("tmp/temp.csd").file());
        csoundObj.pause();

    }

    @Override
    public void stop() {
        csoundObj.stop();
    }

    private void initCsoundObj() {
        File opcodeDir = new File(OPCODE6DIR);
        File[] files = opcodeDir.listFiles();
        for (File file : files) {
            String pluginPath = file.getAbsoluteFile().toString();
            try {
                System.load(pluginPath);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        // This must be set before the Csound object is created.
        csnd6.csndJNI.csoundSetGlobalEnv("OPCODE6DIR", OPCODE6DIR);
        csoundObj = new CsoundObj();
    }

    protected File createTempFile(String csd) {
        File f = null;
        try {
            f = Gdx.files.local("tmp/temp.csd").file();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(csd.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }
}
