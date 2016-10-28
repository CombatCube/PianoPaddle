package com.combatcube.pianopaddle.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.combatcube.pianopaddle.PianoPaddle;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Piano Shooter";
		config.width = 1600;
		config.height = 900;
        DesktopCsoundAdapter csoundAdapter = new DesktopCsoundAdapter();
        new LwjglApplication(new PianoPaddle(csoundAdapter), config);
    }
}

