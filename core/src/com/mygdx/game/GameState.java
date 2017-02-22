package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class GameState {
	public static enum BoxType {OPEN, BOX, STONE, DOOR};
	public static enum EntityType {BOMB, FLASH_BOMB, KEY};
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static int BLOCK_SIZE = 4;
	
	private Tile[][] map;
	int[][] testMap = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0,0,0,0,0,0,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,2,2,2,2,2,2,2,1,1,1,1,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,0,0,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,0,0,0,0,2,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,2,2,1,1,1,0,0,0,0,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
	
	public GameState() {
		GameState.WIDTH = testMap.length;
		GameState.HEIGHT = testMap[0].length;
		map = new Tile[WIDTH][HEIGHT];
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				BoxType b;
				switch (testMap[x][y]) {
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
}
