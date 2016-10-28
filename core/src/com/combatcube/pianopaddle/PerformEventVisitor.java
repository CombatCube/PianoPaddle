package com.combatcube.pianopaddle;

import com.leff.midi.event.Controller;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

/**
 * Class ot perform certain MidiEvents.
 * Created by andrew on 12/29/2015.
 */
public class PerformEventVisitor implements EventVisitor {
    private final int PEDAL_EVENT = 64;
    private SoundEngine engine;

    public PerformEventVisitor(SoundEngine engine) {
        this.engine = engine;
    }

    @Override
    public void visit(Note note) {
        if (!note.passed) {
            engine.playNote(note);
        }
    }

    public void visit(NoteOn noteOn) {
        engine.playNote(noteOn);
    }

    @Override
    public void visit(NoteOff noteOff) {
        engine.playNote(noteOff);
    }

    @Override
    public void visit(Controller cc) {
        if (cc.getControllerType() == PEDAL_EVENT) {
            if (cc.getValue() > 64) {
                engine.engagePedal(true);
            } else {
                engine.engagePedal(false);
            }
        }
    }

    @Override
    public void visit(KeySignature keySig) {
        engine.setKey(new Key(PitchClass.C.successor(keySig.getKey())));
    }

    @Override
    public void visit(TimeSignature timeSig) {

    }

    @Override
    public void visit(Tempo tempo) {
        engine.setBpm(tempo.getBpm());
    }

    @Override
    public void visit(Chord chord) {

    }

}
