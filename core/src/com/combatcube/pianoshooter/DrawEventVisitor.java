package com.combatcube.pianoshooter;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

/**
 * Class to draw MidiEvents in the context of the game.
 * Created by andrew on 12/30/2015.
 */
public class DrawEventVisitor implements EventVisitor {
    public double currentTick = 0;
    public float noteScale = 0;
    public int range = 0;
    public int screenWidth;
    private ShapeRenderer renderer;

    public DrawEventVisitor(ShapeRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void visit(Note note) {
        renderer.setColor(PianoShooter.getNoteColor(note));
        float width = 40;
        float x = (note.getNoteValue()) * width;
        float y = (float) (note.getTick() - currentTick) * noteScale;
        float height = note.duration * noteScale;
        renderer.rect(x, y, width, height);
    }

    @Override
    public void visit(NoteOn noteOn) {

    }

    @Override
    public void visit(NoteOff noteOff) {

    }

    @Override
    public void visit(KeySignature keySig) {

    }

    @Override
    public void visit(TimeSignature timeSig) {

    }

    @Override
    public void visit(Tempo tempo) {

    }
}
