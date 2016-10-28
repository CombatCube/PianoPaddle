package com.combatcube.pianopaddle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.combatcube.pianopaddle.PianoPaddle;


/**
 * Opening game screen.
 * Created by Andrew on 1/1/2016.
 */
public class MainMenuScreen implements Screen {
    final PianoPaddle game;
    OrthographicCamera camera;

    public MainMenuScreen(final PianoPaddle game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.draw(game.batch, "PianoPaddle", 800, 450);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(game.fileSelectScreen);
            this.dispose();
        }
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
