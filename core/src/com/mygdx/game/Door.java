package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Door extends Tile {
	boolean unlocked;
	public Door(Vector2 pos, Vector2 size) {
		super(pos, size, GameState.BoxType.DOOR);
		this.unlocked = false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return this.unlocked;
	}

	@Override
	public void update(float delta_t) {
		// TODO Auto-generated method stub

	}

}
