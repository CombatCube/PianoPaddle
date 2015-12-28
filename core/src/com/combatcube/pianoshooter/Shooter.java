package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;
    private boolean available;

    public Shooter(Rectangle rect) {
        this.rect = rect;
        this.available = true;
    }

    public void moveX(float v) {
        rect.setX(v);
    }

    public void moveY(float tick) {
        rect.setY(tick);
    }

    public Rectangle getRect() {
        return rect;
    }

    public float getCenterX() {
        return rect.x + (rect.getWidth() / 2);
    }

    public void setWidth(int width) {
        rect.width = width;
    }

    public boolean contains(float x, float width) {
        return x < getRect().getX() + getRect().getWidth()
                && x + width > getRect().getX();
    }

    public boolean isAvailable() {
        return available;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.available = isAvailable;
    }

    public void moveCenterX(float pos) {
        moveX(pos - getRect().getWidth()/2);
    }
}