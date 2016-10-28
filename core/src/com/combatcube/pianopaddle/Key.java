package com.combatcube.pianopaddle;

import java.util.ArrayList;

/**
 * Class to hold a key change event.
 * Created by andrew on 12/26/2015.
 */
public class Key {
    public PitchClass pitchClass;
    ArrayList<PitchClass> pitchClasses;
    public Key(PitchClass pitchClass) {
        if (pitchClass.ordinal() < PitchClass.C_FLAT.ordinal()
                || PitchClass.C_SHARP.ordinal() < pitchClass.ordinal()) {
            pitchClass = PitchClass.C;
        }
        this.pitchClass = pitchClass;
        pitchClasses = new ArrayList<PitchClass>();
        addPitch(pitchClass, 0);
        addPitch(pitchClass, 2);
        addPitch(pitchClass, 4);
        addPitch(pitchClass, -1);
        addPitch(pitchClass, 1);
        addPitch(pitchClass, 3);
        addPitch(pitchClass, 5);
    }

    public static boolean isBlackKey(int pitch) {
        pitch = pitch % 12;
        switch (pitch) {
            case 1:
            case 3:
            case 6:
            case 8:
            case 10:
                return true;
            default:
                return false;
        }
    }

    private void addPitch(PitchClass pc, int distance) {
        pitchClasses.add(pc.successor(distance));
    }

    public int pitchToScaleDegree(int pitch) {
        pitch = pitch % 12;
        for (PitchClass pc : pitchClasses) {
            if (pc.toInt() == pitch) {
                return pitchClasses.indexOf(pc);
            }
        }
        return -1;
    }
}