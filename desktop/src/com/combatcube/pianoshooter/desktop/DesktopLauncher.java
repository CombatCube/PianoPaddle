package com.combatcube.pianoshooter.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.combatcube.pianoshooter.PianoShooter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Piano Shooter";
		config.width = 1600;
		config.height = 900;
        DesktopCsoundAdapter csoundAdapter = new DesktopCsoundAdapter();
        new LwjglApplication(new PianoShooter(csoundAdapter), config);
    }
}

