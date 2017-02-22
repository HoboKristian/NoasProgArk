package com.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameState.BoxType;
import com.mygdx.game.GameState.EntityType;

public class Tiles {
	private static HashMap<BoxType, Texture> map = new HashMap<BoxType, Texture>();
	private static HashMap<EntityType, Texture> entity = new HashMap<EntityType, Texture>();
	private static final Tiles instance = new Tiles();

	public static Tiles getInstance(){
		return instance;
	}
	
	private Tiles() {
		map.put(BoxType.STONE, new Texture(Gdx.files.internal("Tiles/castleCenter.png")));
		map.put(BoxType.BOX, new Texture(Gdx.files.internal("Tiles/box.png")));
		map.put(BoxType.DOOR, new Texture(Gdx.files.internal("Tiles/boxExplosive.png")));
		
		entity.put(EntityType.BOMB, new Texture(Gdx.files.internal("Items/bomb.png")));
		entity.put(EntityType.FLASH_BOMB, new Texture(Gdx.files.internal("Items/bombFlash.png")));
		entity.put(EntityType.KEY, new Texture(Gdx.files.internal("Items/keyYellow.png")));
		entity.put(EntityType.PLAYER, new Texture(Gdx.files.internal("Player/p3_front.png")));
	}
	
	public Texture getTextureForType(Object type) {
		if (type instanceof BoxType) {
			return map.get(type);
		} else if (type instanceof EntityType) {
			return entity.get(type);
		}
		return null;
	}

}
