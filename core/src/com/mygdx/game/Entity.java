package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	protected Vector2 pos;
	private Vector2 size;
	protected Texture texture;
	
	public Entity(Vector2 pos, Vector2 size) {
		this.pos = pos;
		this.size = size;
	}
	
	public Entity(Vector2 pos, Vector2 size, Texture texture) {
		this.pos = pos;
		this.size = size;
		this.texture = texture;
	}
	
	public Entity(float x, float y, float w, float h, Texture texture) {
		this.pos = new Vector2(x, y);
		this.size = new Vector2(w, h);
		this.texture = texture;
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
	
	public Rectangle getCollisionRectangle () {
		return new Rectangle(this.pos.x, this.pos.y, this.size.x, this.size.y);
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public boolean shouldRemove() {return false;}
	public void update(float delta_t) {}
	public void collide() {}

}
