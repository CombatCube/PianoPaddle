package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;
    private Rectangle touchBox;
    private boolean available;

    public Shooter(Rectangle rect) {
        this.rect = rect;
        this.touchBox = new Rectangle(rect);
        this.available = true;
        updateTouchBox();
    }


    private void updateTouchBox() {
        touchBox.set(rect);
        touchBox.setY(rect.y + rect.height);
        touchBox.setHeight(50);
    }

    public void moveX(float v) {
        rect.setX(v);
        updateTouchBox();
    }

    public void moveY(float tick) {
        rect.setY(tick);
        updateTouchBox();
    }

    public Rectangle getRect() {
        return rect;
    }

    public Rectangle getTouchBox() {
        return touchBox;
    }

    public float getCenterX() {
        return rect.x + (rect.getWidth() / 2);
    }

    public void setWidth(int width) {
        rect.width = width;
    }

    public boolean contains(NoteBox noteBox) {
        return noteBox.rect.x < getRect().getX() + getRect().getWidth()
                &&  noteBox.rect.x + noteBox.rect.getWidth() > getRect().getX();
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