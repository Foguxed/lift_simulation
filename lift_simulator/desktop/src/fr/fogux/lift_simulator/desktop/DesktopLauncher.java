package fr.fogux.lift_simulator.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import fr.fogux.lift_simulator.Simulateur;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1920;
        config.height = 1080;
		new LwjglApplication(new Simulateur(), config);
	}
}
