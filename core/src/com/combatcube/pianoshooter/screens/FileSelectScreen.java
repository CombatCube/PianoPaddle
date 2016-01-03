package com.combatcube.pianoshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.combatcube.pianoshooter.MidiFilenameFilter;
import com.combatcube.pianoshooter.PianoShooter;

import java.io.File;

/**
 * Created by Andrew on 1/2/2016.
 */
public class FileSelectScreen implements Screen {
    private PianoShooter game;
    private String[] fileNames;
    private int selectedFile = 0;

    public FileSelectScreen(PianoShooter game) {
        this.game = game;
        File file = Gdx.files.local("").file();
        MidiFilenameFilter filter = new MidiFilenameFilter();
        fileNames = file.list(filter);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (selectedFile < fileNames.length - 1) {
                selectedFile += 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (selectedFile > 0) {
                selectedFile -= 1;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game, fileNames[selectedFile]));
            dispose();
        }
        game.batch.begin();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            if (i == selectedFile) {
                game.font.setColor(Color.YELLOW);
            } else {
                game.font.setColor(Color.WHITE);
            }
            game.font.draw(game.batch, fileName, 200, 100*(i-selectedFile) + 400);
        }
        game.batch.end();
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
