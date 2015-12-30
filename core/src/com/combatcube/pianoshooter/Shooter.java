package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;

    public Shooter(Rectangle rect) {
        this.rect = rect;
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

    public void moveCenterX(float pos) {
        rect.setX(pos - getRect().getWidth() / 2);
    }
}