package com.combatcube.pianoshooter.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by andrew on 12/25/2015.
 */
public class Note {
    private int onTick;
    private int offTick;
    private int pitch; // MIDI value
    private Texture texture;
    private NinePatch patch;

    public Note() {
        patch = new NinePatch();
    }

    public void draw(Batch batch, float parentAlpha) {
        //  Draw a rectangle corresponding to the note's ontime and offtime
        patch.draw(batch, (float)pitch, (float)onTick, 5.0f, (float)(offTick - onTick));
    }
}
