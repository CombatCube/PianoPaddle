package com.combatcube.pianoshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.combatcube.pianoshooter.MidiFilenameFilter;
import com.combatcube.pianoshooter.PianoShooter;

import java.io.File;

/**
 * Created by Andrew on 1/2/2016.
 */
public class FileSelectScreen implements Screen {
    private PianoShooter game;
    private FileHandle[] fileNames;
    private int selectedFile = 0;
    private boolean justTouched;
    private OrthographicCamera camera;

    public FileSelectScreen(PianoShooter game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        camera.update();
        FileHandle file = Gdx.files.internal("midi");
        MidiFilenameFilter filter = new MidiFilenameFilter();
        fileNames = file.list(filter);
        justTouched = true;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            changeFile(-1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            changeFile(1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        }
        if (Gdx.input.isTouched()) {
            if (!justTouched) {
                justTouched = true;
                Vector3 mousePos = new Vector3();
                mousePos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                if (0 < mousePos.y && mousePos.y < Gdx.graphics.getHeight()/3) {
                    changeFile(-1);
                }
                if (Gdx.graphics.getHeight()/3 < mousePos.y && mousePos.y < 2*Gdx.graphics.getHeight()/3) {
                    startGame();
                }
                if (2*Gdx.graphics.getHeight()/3 < mousePos.y && mousePos.y < Gdx.graphics.getHeight()) {
                    changeFile(1);
                }
            }
        } else {
            justTouched = false;
        }
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (int i = 0; i < fileNames.length; i++) {
            if (i == selectedFile) {
                game.font.setColor(Color.YELLOW);
            } else {
                game.font.setColor(Color.WHITE);
            }
            game.font.draw(game.batch, fileNames[i].name(), 200, 100*(selectedFile-i) + 400);
        }
        game.batch.end();
    }

    private void startGame() {
        game.setScreen(new GameScreen(game, fileNames[selectedFile].name()));
    }

    private void changeFile(int increment) {
        if (0 <= selectedFile + increment
                && selectedFile + increment <= fileNames.length - 1) {
            selectedFile += increment;
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
