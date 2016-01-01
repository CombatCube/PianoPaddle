package com.combatcube.pianoshooter;

import com.badlogic.gdx.graphics.Color;
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

    public static Color getNoteColor(Note note) {
        if (note.touched) {
            return Color.GREEN;
        } else if (!note.missed) {
//            if (note.scaleDegree != -1) {
                if (note.interval < 3) {
                    return Color.BLUE;
                } else if (note.interval < 7) {
                    return Color.GREEN;
                } else if (note.interval < 11) {
                    return Color.ORANGE;
                } else if (note.interval < 15) {
                    return Color.PURPLE;
                } else {
                    return Color.BLACK;
                }
//            } else {
//                // Out of key
//                if (Key.isBlackKey(note.getNoteValue())) {
//                    return Color.YELLOW;
//                } else {
//                    return Color.YELLOW;
//                }
//            }
        } else {
            return Color.RED;
        }
    }

    @Override
    public void visit(Note note) {
        renderer.setColor(getNoteColor(note));
        float width = 40;
        float x = (note.getNoteValue()) * width;
        float y = (float) (note.getTick() - currentTick) * noteScale;
        float height = note.duration * noteScale;
        renderer.rect(x, y, width, 40);
        renderer.rect(x+width/2, y, 1, height);
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
