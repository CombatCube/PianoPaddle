package com.combatcube.pianopaddle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;
    private float noteWidth;
    private int intervalStart = 0;
    private int intervalEnd = 0;
    private Color color;

    public Shooter(int intervalStart, int intervalEnd, float noteWidth, float noteHeight, Color color) {
        this.rect = new Rectangle(0, -noteHeight, (intervalEnd + 2) * (noteWidth), noteHeight);
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.noteWidth = noteWidth;
        this.color = color;
    }

    public boolean contains(float x, float width) {
        return x < rect.getX() + rect.getWidth()
                && x + width > rect.getX();
    }

    public boolean contains(Note note) {
        return note.interval >= intervalStart && contains(note.getNoteValue() * noteWidth, noteWidth);
    }

    public void moveCenterX(float pos) {
        rect.setX(pos - rect.getWidth() / 2);
    }

    public void draw(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.rect(rect.getX(),
                rect.getY(),
                rect.getWidth(),
                rect.getHeight());
    }
}