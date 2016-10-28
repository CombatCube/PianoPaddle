package com.combatcube.pianopaddle;

/**
 * Created by andrew on 12/26/2015.
 */
public enum PitchClass {
    F_DOUBLEFLAT {@Override public String toString() { return "Fbb"; } },
    C_DOUBLEFLAT {@Override public String toString() { return "Cbb"; } },
    G_DOUBLEFLAT {@Override public String toString() { return "Gbb"; } },
    D_DOUBLEFLAT {@Override public String toString() { return "Dbb"; } },
    A_DOUBLEFLAT {@Override public String toString() { return "Abb"; } },
    E_DOUBLEFLAT {@Override public String toString() { return "Ebb"; } },
    B_DOUBLEFLAT {@Override public String toString() { return "Bbb"; } },
    F_FLAT {@Override public String toString() { return "Fb"; } },
    C_FLAT {@Override public String toString() { return "Cb"; } },
    G_FLAT {@Override public String toString() { return "Gb"; } },
    D_FLAT {@Override public String toString() { return "Db"; } },
    A_FLAT {@Override public String toString() { return "Ab"; } },
    E_FLAT {@Override public String toString() { return "Eb"; } },
    B_FLAT {@Override public String toString() { return "Bb"; } },
    F {@Override public String toString() { return "F"; } },
    C {@Override public String toString() { return "C"; } },
    G {@Override public String toString() { return "G"; } },
    D {@Override public String toString() { return "D"; } },
    A {@Override public String toString() { return "A"; } },
    E {@Override public String toString() { return "E"; } },
    B {@Override public String toString() { return "B"; } },
    F_SHARP {@Override public String toString() { return "F#"; } },
    C_SHARP {@Override public String toString() { return "C#"; } },
    G_SHARP {@Override public String toString() { return "G#"; } },
    D_SHARP {@Override public String toString() { return "D#"; } },
    A_SHARP {@Override public String toString() { return "A#"; } },
    E_SHARP {@Override public String toString() { return "E#"; } },
    B_SHARP {@Override public String toString() { return "B#"; } },
    F_DOUBLESHARP {@Override public String toString() { return "Fx"; } },
    C_DOUBLESHARP {@Override public String toString() { return "Cx"; } },
    G_DOUBLESHARP {@Override public String toString() { return "Gx"; } },
    D_DOUBLESHARP {@Override public String toString() { return "Dx"; } },
    A_DOUBLESHARP {@Override public String toString() { return "Ax"; } },
    E_DOUBLESHARP {@Override public String toString() { return "Ex"; } },
    B_DOUBLESHARP {@Override public String toString() { return "Bx"; } };

    public PitchClass successor(int amount) {
        if (0 < ordinal() + amount && ordinal() + amount < values().length) {
            return values()[ordinal() + amount];
        }
        else {
            return null;
        }
    };

    // Returns an integer from 0 to 11 where 0 is C and 11 is B.
    public int toInt() {
        int distance = ordinal() - PitchClass.C.ordinal();
        int retPitch = distance * 7;
        retPitch %= 12;
        if (retPitch < 0) {
            retPitch += 12;
        }
        return retPitch;
    }
}
