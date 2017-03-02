package com.mygdx.game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class GameState {
	public static enum BoxType {OPEN, BOX, STONE, DOOR};
	public static enum EntityType {BOMB, FLASH_BOMB, KEY, PLAYER, POWERUP_WALK_FAST, POWERUP_WALK_WALL};
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static int BLOCK_SIZE = 4;
	
	private Tile[][] map;
	
	public GameState() {
		Path path = FileSystems.getDefault().getPath("map.txt");
		try {
			List<String> lines = Files.readAllLines(path);
			Collections.reverse(lines);
			HEIGHT = lines.size();
			WIDTH = lines.get(0).split(" ").length;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
