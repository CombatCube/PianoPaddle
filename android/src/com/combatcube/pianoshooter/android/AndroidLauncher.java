package com.combatcube.pianoshooter.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.combatcube.pianoshooter.PianoShooter;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        AndroidCsoundAdapter csoundAdapter = new AndroidCsoundAdapter(getBaseContext().getApplicationInfo().nativeLibraryDir);
        initialize(new PianoShooter(csoundAdapter), config);
	}
}
