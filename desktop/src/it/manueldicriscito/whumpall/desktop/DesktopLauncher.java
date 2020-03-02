package it.manueldicriscito.whumpall.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import it.manueldicriscito.whumpall.Whumpall;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 9*50;
		config.height = 16*50;
		config.samples = 3;

		new LwjglApplication(new Whumpall(), config);
	}
}
