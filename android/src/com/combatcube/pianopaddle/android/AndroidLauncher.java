package com.combatcube.pianopaddle.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.combatcube.pianopaddle.PianoPaddle;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        AndroidCsoundAdapter csoundAdapter = new AndroidCsoundAdapter(getBaseContext().getApplicationInfo().nativeLibraryDir);
        initialize(new PianoPaddle(csoundAdapter), config);
	}
}
