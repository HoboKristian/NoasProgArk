package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class Door extends Tile implements KeyListener{
	private boolean unlocked;
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
	public void keyUnlocked(Key key) {
		this.unlocked = true;
	}

}
