package com.endlessmaze.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.endlessmaze.game.EndlessMaze;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.height=720;
                config.width=1280;
		new LwjglApplication(new EndlessMaze(), config);
	}
}
