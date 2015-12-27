package com.combatcube.pianoshooter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by andrew on 12/26/2015.
 */
public class Key {
    ArrayList<PitchClass> pitchClasses;
    public Key(PitchClass pitchClass) {
        pitchClasses = new ArrayList<PitchClass>();
        if (PitchClass.C_FLAT.ordinal() <= pitchClass.ordinal()
                && pitchClass.ordinal() <= PitchClass.C_SHARP.ordinal()) {
            addPitch(pitchClass, 0);
            addPitch(pitchClass, 2);
            addPitch(pitchClass, 4);
            addPitch(pitchClass, -1);
            addPitch(pitchClass, 1);
            addPitch(pitchClass, 3);
            addPitch(pitchClass, 5);
        }
    }

    private void addPitch(PitchClass pc, int distance) {
        pitchClasses.add(pc.successor(distance));
    }

    public int pitchToScaleDegree(int pitch) {
        pitch = pitch % 12;
        Iterator<PitchClass> it = pitchClasses.iterator();
        while (it.hasNext()) {
            PitchClass pc = it.next();
            if (pc.toInt() == pitch) {
                return pitchClasses.indexOf(pc);
            }
        }
        return -1;
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
}