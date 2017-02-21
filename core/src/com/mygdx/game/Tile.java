package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameState.BoxType;

public class Tile extends Entity {
	BoxType type;
	
	public Tile(Vector2 pos, Vector2 size, BoxType type) {
		super(pos, size, Tiles.getInstance().getTextureForType(type));
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	public BoxType getType() {
		return this.type;
	}

	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(float delta_t) {
		// TODO Auto-generated method stub

	}

}
