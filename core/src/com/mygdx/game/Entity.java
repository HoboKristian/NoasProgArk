package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	private Vector2 pos;
	private Vector2 size;
	
	public Entity(Vector2 pos, Vector2 size) {
		this.pos = pos;
		this.size = size;
	}
	
	public Entity(float x, float y, float w, float h) {
		this.pos = new Vector2(x, y);
		this.size = new Vector2(w, h);
	}
	
	public Vector2 getPos() {
		return this.pos;
	}
	
	public Vector2 getCenterPos() {
		return new Vector2(this.pos.x + this.size.x / 2, this.pos.y + this.size.y);
	}
	
	public Vector2 getSize() {
		return this.size;
	}
	
	abstract public Texture getTexture();
	abstract public boolean shouldRemove();
	abstract public void update(float delta_t);

}
