package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Entity {
	private Vector2 pos;
	private Vector2 size;
	
	public Entity(Vector2 pos, Vector2 size) {
		
	}
	
	public Entity(float x, float y, float w, float h) {
		this.pos = new Vector2(x, y);
		this.size = new Vector2(w, h);
	}

}
