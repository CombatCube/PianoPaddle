package com.combatcube.pianoshooter;

import com.badlogic.gdx.math.Rectangle;

public class Shooter {
    private Rectangle rect;
    private Rectangle touchBox;

    public Shooter(Rectangle rect) {
        this.rect = rect;
        this.touchBox = new Rectangle(rect);
        updateTouchBox();
    }


    private void updateTouchBox() {
        touchBox.set(rect);
        touchBox.setY(rect.y + rect.height - 5);
        touchBox.setHeight(5);
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
}