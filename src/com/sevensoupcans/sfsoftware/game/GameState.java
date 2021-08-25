package com.sevensoupcans.sfsoftware.game;

public enum GameState {
	INTRO("Intro"), TITLE_SCREEN("Title Screen"), INGAME("In Game"), GAMEOVER("Game Over"), CREDITS("Credits");
	
	private final String gameStateDescription;
	
	GameState(String gameStateDescription) {
		this.gameStateDescription = gameStateDescription;
	}

	public String getStateDescription() {
		return gameStateDescription;
	}
	
	public String getStateName() {
		return name();
	}
}
