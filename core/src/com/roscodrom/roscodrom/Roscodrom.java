package com.roscodrom.roscodrom;

import com.badlogic.gdx.Game;

public class Roscodrom extends Game {
	MainMenuScreen mainMenuScreen;
	IndividualGame individualGame;
	MultiplayerGame multiplayerGame;
	Coliseum coliseum;
	Options options;
	Profile profile;
	GameOver gameOver;

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
	public void showGameOver(int points) {
		gameOver = new GameOver(points);
		setScreen(gameOver);
	}
}
