package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;
    private float noteWidth;

    public Shooter(Rectangle rect, float noteWidth) {
        this.rect = rect;
        this.noteWidth = noteWidth;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setWidth(int width) {
        rect.width = width;
    }

    public boolean contains(float x, float width) {
        return x < getRect().getX() + getRect().getWidth()
                && x + width > getRect().getX();
    }

    public boolean contains(Note note) {
        return contains(note.getNoteValue() * noteWidth, noteWidth);
    }

    public void moveCenterX(float pos) {
        rect.setX(pos - getRect().getWidth() / 2);
    }
}