package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
	public Texture img;
	public Vector2 pos;
	public Player(int x, int y) {
		pos = new Vector2(x, y);
		img = new Texture(Gdx.files.internal("Player/p1_duck.png"));
	}
	
	public void move (Vector2 movement) {
		this.move(movement.x, movement.y);
	}
	public void move(double delta_x, double delta_y) {
		pos.x += delta_x;
		pos.y += delta_y;
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public Rectangle getHitbox() {
		return new Rectangle(pos.x + GameState.BLOCK_SIZE / 4, pos.y + GameState.BLOCK_SIZE / 4,
							GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
	}
	
	public Vector2 getCenterPos() {
		return new Vector2(pos.x + GameState.BLOCK_SIZE / 2, pos.y + GameState.BLOCK_SIZE / 2);
	}
	
	public Texture img() {
		return img;
	}

}
