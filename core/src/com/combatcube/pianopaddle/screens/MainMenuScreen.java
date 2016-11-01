package com.combatcube.pianopaddle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.combatcube.pianopaddle.PianoPaddle;


/**
 * Opening game screen.
 * Created by Andrew on 1/1/2016.
 */
public class MainMenuScreen implements Screen {
    final PianoPaddle game;
    private Stage stage;

    private Table table;
    private Table googlePlayTable;
    private Skin skin;
    private Stack stack;
    private TextureAtlas atlas;
    private Label.LabelStyle labelStyle;
    private TextButton.TextButtonStyle textButtonStyle;
    private OrthographicCamera camera;
    private final Label titleLabel;
    private final TextButton playButton;
    private final TextButton achievementsButton;
    private final TextButton leaderboardsButton;
    private final TextButton signInButton;
    private final TextButton signOutButton;
    private final Label signInStatusLabel;
    private final Image googlePlayImage;
    private final Label googlePlayLabel;
    private final Image leaderboardsImage;
    private final Image achievementsImage;

    public MainMenuScreen(final PianoPaddle game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1600, 900);
        atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        skin.add("geo72", game.font);

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.font;

        Texture googlePlayTexture = new Texture(Gdx.files.internal("ic_play_games_badge_green.png"));
        Texture achievementsTexture = new Texture(Gdx.files.internal("ic_play_games_badge_achievements_green.png"));
        Texture leaderboardsTexture = new Texture(Gdx.files.internal("ic_play_games_badge_leaderboards_green.png"));
        googlePlayImage = new Image(googlePlayTexture);
        googlePlayImage.setScaling(Scaling.fit);
        achievementsImage = new Image(achievementsTexture);
        achievementsImage.setScaling(Scaling.fit);
        leaderboardsImage = new Image(leaderboardsTexture);
        leaderboardsImage.setScaling(Scaling.fit);


        labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font = game.font;

        titleLabel = new Label("PianoPaddle", labelStyle);
        googlePlayLabel = new Label("Google Play", labelStyle);
        playButton = new TextButton("Play", textButtonStyle);
        achievementsButton = new TextButton("Achievements", textButtonStyle);
        leaderboardsButton = new TextButton("Leaderboards", textButtonStyle);
        signInButton = new TextButton("Sign in", textButtonStyle);
        signOutButton = new TextButton("Sign out", textButtonStyle);
        signOutButton.setVisible(false);
        signInStatusLabel = new Label(getSignInText(), labelStyle);

        stage = new Stage();
        game.multiplexer.addProcessor(game);
        game.multiplexer.addProcessor(stage);

        table = new Table();
        table.setDebug(false);
        table.setFillParent(true);
        table.align(Align.center);
        stage.addActor(table);

        table.add(titleLabel).colspan(2).space(120);
        table.row();
        table.add(playButton).colspan(2).fillX().height(200).space(80);
        table.row();

        googlePlayTable = new Table();

        googlePlayTable.setDebug(false);
        table.add(googlePlayTable);
        stack = new Stack();
        stack.add(signInButton);
        stack.add(signOutButton);

        googlePlayTable.add(googlePlayImage).width(64);
        googlePlayTable.add(googlePlayLabel);
        googlePlayTable.row();
        googlePlayTable.add(stack).colspan(2).fillX().space(40);
        googlePlayTable.row().padBottom(40);
        googlePlayTable.add(achievementsImage).width(64);
        googlePlayTable.add(achievementsButton);
        googlePlayTable.row();
        googlePlayTable.add(leaderboardsImage).width(64);
        googlePlayTable.add(leaderboardsButton);

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

        achievementsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PianoPaddle.playServices.showAchievement();
            }
        });

        leaderboardsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PianoPaddle.playServices.showScore();
            }
        });
    }

    private String getSignInText() {
        if (PianoPaddle.playServices.isSignedIn()) {
            return "Signed in.";
        } else if (PianoPaddle.playServices.hasSignInError()) {
            return "Error signing in.";
        } else {
            return "Not signed in.";
        }
    }

    @Override
    public void show() {
        game.multiplexer.addProcessor(stage);
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
        game.multiplexer.removeProcessor(stage);
    }

    @Override
    public void dispose() {

    }
}
