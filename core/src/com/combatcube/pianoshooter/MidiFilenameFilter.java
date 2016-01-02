package com.combatcube.pianoshooter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter for midi files.
 * Created by Andrew on 1/1/2016.
 */
public class MidiFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".mid");
    }
}
