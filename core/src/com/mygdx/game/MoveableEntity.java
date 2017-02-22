package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public abstract class MoveableEntity extends Entity {
	public MoveableEntity(Vector2 pos, Vector2 size, Texture texture) {
		super(pos, size, texture);
	}
	
	public void moveEntity(Vector2 movement) {
		this.setPos(this.getPos().add(movement));
	}
	
	protected void setPos(Vector2 pos) {
		super.pos = pos;
	}
}
