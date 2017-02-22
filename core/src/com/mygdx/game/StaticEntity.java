package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class StaticEntity extends Entity {
	private Vector2 pos;
	public StaticEntity(Vector2 pos, Vector2 size) {
		super(pos, size, null);
	}
	public StaticEntity(Vector2 pos, Vector2 size, Texture texture) {
		super(pos, size, texture);
	}
}
