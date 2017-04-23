package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
	public static enum BoxType {OPEN, BOX, STONE, DOOR, FLAG};
	public static enum EntityType {BOMB, FLASH_BOMB, KEY, PLAYER, OPPONENT, POWERUP_WALK_FAST, POWERUP_WALK_SLOWER, POWERUP_WALK_FREEZE, POWERUP_INVERT_TOUCHPAD};
	public static enum DialogType {INVITED_PLAYER, INVITED_BY_PLAYER}

	public static int WIDTH;
	public static int HEIGHT;

	public Vector2 opponentPos = new Vector2(0, 0);
	
	public static int BLOCK_SIZE = 4;

	private Tile[][] map;
	private Tile[][] baseMap;

	private static final GameState instance = new GameState();

	public String gameId;
	public String gameWinner;
	public boolean gameFinished;
	public String name = getSaltString();

	public static enum RenderState {GAME, MENU, LIST};
	private RenderState renderState;
	private List<GameStateListener> statelisteners = new ArrayList<GameStateListener>();

	public static GameState getInstance(){
		return instance;
	}

	private DialogType dialogToShow;
	public Dialog currentShowingDialog;

	protected String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) {
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

	private GameState() {
		FileHandle file = Gdx.files.internal("map.txt");
		String text = file.readString();
		String[] lines = text.split("\n");
		HEIGHT = lines.length;
		WIDTH = lines[0].split(" ").length;
		String[] reversed_lines = new String[lines.length];
		for (int i = 0; i < lines.length; i++) {
			reversed_lines[i] = lines[lines.length - i - 1];
		}
		baseMap = new Tile[WIDTH][HEIGHT];
		int y = 0;
		for (String l : reversed_lines) {
			int x = 0;
			for (String a : l.split(" ")) {
				BoxType b;
				switch (Integer.parseInt(a)) {
					case 0:
						b = GameState.BoxType.OPEN;
						break;
					case 1:
						b = GameState.BoxType.BOX;
						break;
					case 2:
						b = GameState.BoxType.STONE;
						break;
					case 3:
						b = GameState.BoxType.FLAG;
						break;
					default:
						b = GameState.BoxType.OPEN;
						break;
				}
				Vector2 pos = new Vector2(x * GameState.BLOCK_SIZE, y * GameState.BLOCK_SIZE);
				Vector2 size = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
				baseMap[x][y] = new Tile(pos, size, b);
				x++;
			}
			y++;
		}
		loadMap();
	}

	public void loadMap() {
		map = new Tile[WIDTH][HEIGHT];
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				map[x][y] = baseMap[x][y];
			}
		}
	}
	
	public Tile getTile(int x, int y) {
		if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) {
			System.out.println("GetTile x,y must be within the gamestatemap");
			return null;
		}
		return map[x][y];
	}
	
	public void setTile(Tile t, int x, int y) throws IllegalArgumentException {
		if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0) {
			System.out.println("SetTile x,y must be within the gamestatemap");
			return;
		}
		map[x][y] = t;
	}

	public void setDialogToShow(DialogType dialogType) {
		this.dialogToShow = dialogType;
	}

	public Dialog getDialogToShow() {
		if (this.dialogToShow == null)
			return null;

		Dialog dialog = null;
		switch (this.dialogToShow) {
			case INVITED_BY_PLAYER:
				dialog = GameDialog.getInstance().getInvitedByPlayerDialog();
				break;
			case INVITED_PLAYER:
				dialog = GameDialog.getInstance().getInvitedPlayerDialog();
				break;
		}
		this.dialogToShow = null;
		currentShowingDialog = dialog;
		return dialog;
	}

	public void setRenderState(RenderState renderState) {
		this.renderState = renderState;
		notifyGameStateListeners(this.renderState);
	}

	public RenderState getRenderState() {
		return this.renderState;
	}

	public void setGameFinished(String winner) {
		this.gameFinished = true;
		this.gameWinner = winner;
	}

	public void registerGameStateListener (GameStateListener listener) {
		// Add the listener to the list of registered listeners
		this.statelisteners.add(listener);
	}
	public void unregisterGameStateListener (GameStateListener listener) {
		// Remove the listener from the list of the registered listeners
		this.statelisteners.remove(listener);
	}
	protected void notifyGameStateListeners (RenderState renderState) {
		// Notify each of the listeners in the list of registered listeners
		for (GameStateListener listener : this.statelisteners) {
			listener.gameStateChanged(renderState);
		}
	}
}

