package com.combatcube.pianoshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.combatcube.pianoshooter.screens.GameScreen;
import com.combatcube.pianoshooter.screens.MainMenuScreen;

/**
 * Main game class.
 * Created by Andrew on 1/1/2016.
 */
public class PianoShooter extends Game {

    public SpriteBatch batch;
    public ShapeRenderer renderer;
    public BitmapFont font;
    public SoundEngine soundEngine;

    public PianoShooter(CsoundAdapter csoundAdapter) {
        this.soundEngine = new SoundEngine(csoundAdapter);
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();
        this.font = new BitmapFont(Gdx.files.internal("geo72.fnt"));
        this.screen = new MainMenuScreen(this);
    }

    @Override
    public void render() {
        super.render(); //important!
    }
}
