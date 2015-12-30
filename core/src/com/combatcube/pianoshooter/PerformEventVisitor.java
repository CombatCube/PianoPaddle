package com.combatcube.pianoshooter;

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
    private SoundEngine engine;

    public PerformEventVisitor(SoundEngine engine) {
        this.engine = engine;
    }

    public void visit(NoteOn noteOn) {
        engine.playNote(noteOn);
    }

    @Override
    public void visit(NoteOff noteOff) {
        engine.playNote(noteOff);
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

}
