package com.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameState.BoxType;
import com.mygdx.game.GameState.EntityType;

public class Tiles {
	static HashMap<BoxType, Texture> map = new HashMap<BoxType, Texture>();
	static HashMap<EntityType, Texture> entity = new HashMap<EntityType, Texture>();
	public Tiles() {
		map.put(BoxType.STONE, new Texture(Gdx.files.internal("Tiles/castleCenter.png")));
		map.put(BoxType.BOX, new Texture(Gdx.files.internal("Tiles/box.png")));
		entity.put(EntityType.BOMB, new Texture(Gdx.files.internal("Items/bomb.png")));
		entity.put(EntityType.FLASH_BOMB, new Texture(Gdx.files.internal("Items/bombFlash.png")));
	}
	
	public Texture getTextureForBox(BoxType tile) {
		return map.get(tile);
	}

}
