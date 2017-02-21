package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Key extends Entity {
	Door door;
	boolean taken;
	public Key(Vector2 pos, Vector2 size, Texture tex, Door door) {
		super(pos, size, tex);
		this.door = door;
		this.taken = false;
	}
	
	@Override
	public void collide() {
		this.taken = true;
		door.unlocked = true;
	}
	
	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return this.taken;
	}

	@Override
	public void update(float delta_t) {
		// TODO Auto-generated method stub
	}

}
