package com.combatcube.pianopaddle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.combatcube.pianopaddle.MidiFilenameFilter;
import com.combatcube.pianopaddle.PianoPaddle;

/**
 * Created by Andrew on 1/2/2016.
 */
public class FileSelectScreen implements Screen {
    private PianoPaddle game;
    private FileHandle[] fileNames;
    private int selectedFile = 0;
    private boolean justTouched;
    private OrthographicCamera camera;
    private Stage stage;
    private Table table;
    private Table container;
    private TextureAtlas atlas;
    private TextButton.TextButtonStyle textButtonStyle;
    private ScrollPane scrollPane;

    public FileSelectScreen(PianoPaddle game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        camera.update();
        MidiFilenameFilter filter = new MidiFilenameFilter();
        fileNames = Gdx.files.internal("midi").list(filter);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.font;

        stage = new Stage();

        table = new Table();
        table.setDebug(true);
        table.align(Align.center);

        container = new Table();
        container.setFillParent(true);

        scrollPane = new ScrollPane(table);

        table.add();
        table.row();
        for (FileHandle fileHandle : fileNames) {
            final String name = fileHandle.name();
            TextButton button = new TextButton(fileHandle.nameWithoutExtension().replace('_', ' '), textButtonStyle);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    startGame(name);
                }
            });
            table.add(button).align(Align.left).space(100);
            table.row();
        }
        table.add();

        stage.addActor(container);
        container.add(scrollPane);
    }

    @Override
    public void show() {
        game.multiplexer.addProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scrollPane.validate();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void startGame(String name) {
        game.inProgress = true;
        game.setScreen(new GameScreen(game, name));
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
        game.multiplexer.removeProcessor(stage);
    }

    @Override
    public void dispose() {

    }
}
