package com.combatcube.pianopaddle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.combatcube.pianopaddle.PianoPaddle;
import com.combatcube.pianopaddle.PlayServices;


/**
 * Opening game screen.
 * Created by Andrew on 1/1/2016.
 */
public class MainMenuScreen implements Screen {
    final PianoPaddle game;

    private Stage stage;
    private Table table;
    private Skin skin;
    private Stack stack;
    private TextureAtlas atlas;
    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle textButtonStyle;
    private OrthographicCamera camera;
    private final Label titleLabel;
    private final TextButton playButton;
    private final TextButton signInButton;
    private final TextButton signOutButton;
    private final Label signInStatusLabel;

    public MainMenuScreen(final PianoPaddle game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        skin.add("geo72", game.font);

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.font;

        labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font = game.font;

        titleLabel = new Label("PianoPaddle", labelStyle);
        playButton = new TextButton("Play", textButtonStyle);
        signInButton = new TextButton("Sign in", textButtonStyle);
        signOutButton = new TextButton("Sign out", textButtonStyle);
        signOutButton.setVisible(false);
        signInStatusLabel = new Label(getSignInText(), labelStyle);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        stack = new Stack();
        stack.add(signInButton);
        stack.add(signOutButton);

        table.add(playButton).width(400).space(40);
        table.row();
        table.add(stack).width(400).space(40);
        table.row();
        table.add(signInStatusLabel);

        playButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.fileSelectScreen);
                dispose();
            }
        });

        signInButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                signInButton.setDisabled(true);
                signOutButton.setDisabled(false);
                PianoPaddle.playServices.signIn();
            }
        });

        signOutButton.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                signInButton.setDisabled(false);
                signOutButton.setDisabled(true);
                PianoPaddle.playServices.signOut();
            }
        });
    }

    private String getSignInText() {
        if (PianoPaddle.playServices.isSignedIn()) {
            return "Signed in.";
        } else {
            return "Not signed in.";
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        signInStatusLabel.setText(getSignInText());
        if (PianoPaddle.playServices.isSignedIn()) {
            signInButton.setVisible(false);
            signOutButton.setVisible(true);
        } else {
            signInButton.setVisible(true);
            signOutButton.setVisible(false);
        }
        stage.draw();
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
