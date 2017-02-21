package com.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameState.BoxType;

public class Tiles {
	static HashMap<BoxType, Texture> map = new HashMap<BoxType, Texture>();
	public Tiles() {
		map.put(BoxType.STONE, new Texture(Gdx.files.internal("Tiles/castleCenter.png")));
		map.put(BoxType.BOX, new Texture(Gdx.files.internal("Tiles/box.png")));
	}
	
	public Texture getTextureForBox(BoxType tile) {
		return map.get(tile);
	}

}
