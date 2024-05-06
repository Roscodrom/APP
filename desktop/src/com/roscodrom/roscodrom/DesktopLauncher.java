package com.roscodrom.roscodrom;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.roscodrom.roscodrom.Roscodrom;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		CommonWebSockets.initiate();
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Roscodrom");
		config.setWindowedMode(480,800);
		new Lwjgl3Application(new Roscodrom(), config);
	}
}
