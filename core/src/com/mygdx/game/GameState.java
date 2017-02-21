package com.mygdx.game;

public class GameState {
	public static enum BoxType {OPEN, BOX, STONE};
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static int BLOCK_SIZE = 4;
	
	BoxType[][] map;
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
		map = new BoxType[WIDTH][HEIGHT];
		BoxType[] values = BoxType.values();
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				map[x][y] = values[testMap[x][y]];
			}
		}
	}
	
	public BoxType getState(int x, int y) {
		return map[x][y];
	}	
	
	public void setState(BoxType state, int x, int y) throws IllegalArgumentException {
		if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0)
			throw new IllegalArgumentException();
		map[x][y] = state;
	}
}
