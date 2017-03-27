package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameState.BoxType;

public class Tile extends StaticEntity {
	BoxType type;
	
	public Tile(Vector2 pos, Vector2 size, BoxType type) {
		super(pos, size, TextureLoader.getInstance().getTextureForType(type));
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	public BoxType getType() {
		return this.type;
	}
}
