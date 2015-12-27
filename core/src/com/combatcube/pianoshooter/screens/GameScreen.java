package com.combatcube.pianoshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by andrew on 12/24/2015.
 */
public class GameScreen implements Screen {

    private static Stage noteStage = null;

    @Override
    public void show() {
        noteStage = new Stage();
        Gdx.input.setInputProcessor(noteStage);
//        noteStage.addActor();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
