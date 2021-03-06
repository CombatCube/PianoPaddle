package com.combatcube.pianopaddle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.combatcube.pianopaddle.screens.FileSelectScreen;
import com.combatcube.pianopaddle.screens.MainMenuScreen;

/**
 * Main game class.
 * Created by Andrew on 1/1/2016.
 */
public class PianoPaddle extends Game implements InputProcessor {

    public static PlayServices playServices;
    public SpriteBatch batch;
    public ShapeRenderer renderer;
    public Skin skin;
    private TextureAtlas atlas;
    public BitmapFont font;
    public SoundEngine soundEngine;
    public Screen fileSelectScreen;
    public InputMultiplexer multiplexer;
    private MainMenuScreen mainMenuScreen;
    public boolean inProgress = false;

    public PianoPaddle(CsoundAdapter csoundAdapter, PlayServices playServices) {
        this.soundEngine = new SoundEngine(csoundAdapter);
        this.playServices = playServices;
    }

    @Override
    public void create() {
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
        this.font = new BitmapFont(Gdx.files.internal("geo72.fnt"));
        this.atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        this.skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        skin.add("geo72", font);
        mainMenuScreen = new MainMenuScreen(this);
        fileSelectScreen = new com.combatcube.pianopaddle.screens.FileSelectScreen(this);
        this.screen = mainMenuScreen;
    }

    @Override
    public void render() {
        super.render(); //important!
    }

    public void endGame() {
        Screen oldScreen = screen;
        setScreen(fileSelectScreen);
        oldScreen.dispose();
        inProgress = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.debug("MyTag", "key pressed: " + keycode);
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            if (inProgress) {
                endGame();
            }
            else if (screen == fileSelectScreen) {
                setScreen(mainMenuScreen);
            }
            else if (screen == mainMenuScreen ){
                Gdx.app.exit();
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
