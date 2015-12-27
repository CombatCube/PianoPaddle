package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by andrew on 12/26/2015.
 */
public class NoteBox {
    public Note note;
    public Rectangle rect;
    public Rectangle touchBox;
    public boolean touched;

    public NoteBox(Note note, Rectangle rect) {
        this.note = note;
        this.rect = rect;
        this.touched = false;
        touchBox = new Rectangle(rect);
        updateTouchBox();
    }

    private void updateTouchBox() {
        touchBox.set(rect);
        touchBox.height = 40;
        touchBox.y -= 5;
    }
}
