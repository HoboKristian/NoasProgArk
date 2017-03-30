package com.mygdx.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

public class GameState {
	public static enum BoxType {OPEN, BOX, STONE, DOOR};
	public static enum EntityType {BOMB, FLASH_BOMB, KEY, PLAYER, POWERUP_WALK_FAST, POWERUP_WALK_WALL};
	
	public static int WIDTH;
	public static int HEIGHT;

	public Vector2 opponentPos = new Vector2(0, 0);
	
	public static int BLOCK_SIZE = 4;
	
	private Tile[][] map;
	
	private static final GameState instance = new GameState();

	public String gameId;
	public String name = getSaltString();
	public static enum RenderState {GAME, MENU, LIST};
	private RenderState renderState;
	private List<GameStateListener> listeners = new ArrayList<GameStateListener>();

	public static GameState getInstance(){
		return instance;
	}

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
		map = new Tile[WIDTH][HEIGHT];
		int y = 0;
		for (String l : lines) {
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
					default:
						b = GameState.BoxType.OPEN;
						break;
				}
				Vector2 pos = new Vector2(x * GameState.BLOCK_SIZE, y * GameState.BLOCK_SIZE);
				Vector2 size = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
				map[x][y] = new Tile(pos, size, b);
				x++;
			}
			y++;
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

	public void setRenderState(RenderState renderState) {
		this.renderState = renderState;
		notifyGameStateListeners(this.renderState);
	}

	public void registerGameStateListener (GameStateListener listener) {
		// Add the listener to the list of registered listeners
		this.listeners.add(listener);
	}
	public void unregisterGameStateListener (GameStateListener listener) {
		// Remove the listener from the list of the registered listeners
		this.listeners.remove(listener);
	}
	protected void notifyGameStateListeners (RenderState renderState) {
		// Notify each of the listeners in the list of registered listeners
		for (GameStateListener listener : listeners) {
			listener.gameStateChanged(renderState);
		}
	}
}

