package com.combatcube.pianoshooter;

import com.badlogic.gdx.Gdx;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundEngine {
    public static MidiTrack getTrack(MidiFile midiFile, int trackNo) {
        if (trackNo < 0 || trackNo > midiFile.getTrackCount()) {
            return null;
        }
        MidiTrack track = midiFile.getTracks().get(trackNo);
        track.dumpEvents();
        Iterator<MidiEvent> it = track.getEvents().iterator();
        List<MidiEvent> eventsToRemove = new ArrayList<MidiEvent>();
        while (it.hasNext()) {
            MidiEvent event = it.next();
            if (!(event instanceof NoteOn)
                    && !(event instanceof NoteOff)
                    && !(event instanceof Tempo)) {
                eventsToRemove.add(event);
            }
        }
        for (MidiEvent event : eventsToRemove) {
            track.removeEvent(event);
        }
        return track;
    }
}