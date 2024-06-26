package com.roscodrom.roscodrom;

import com.badlogic.gdx.Game;

public class RoscodromGame extends Game {
	MainMenuScreen mainMenuScreen;
	IndividualGame individualGame;
	MultiplayerGame multiplayerGame;
	Coliseum coliseum;
	Options options;
	Profile profile;

	@Override
	public void create () {
		mainMenuScreen = new MainMenuScreen();
		setScreen(mainMenuScreen);
	}

	public void showMainMenuScreen() {
		mainMenuScreen = new MainMenuScreen();
		setScreen(mainMenuScreen);
	}
	public void showIndividualGame() {
		individualGame = new IndividualGame();
		setScreen(individualGame);
	}
	public void showMultiplayerGame() {
		multiplayerGame = new MultiplayerGame();
		setScreen(multiplayerGame);
	}
	public void showColiseum() {
		coliseum = new Coliseum();
		setScreen(coliseum);
	}
	public void showOptions() {
		options = new Options();
		setScreen(options);
	}
	public void showProfile() {
		profile = new Profile();
		setScreen(profile);
	}
}
