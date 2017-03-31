package com.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.GameState.BoxType;
import com.mygdx.game.GameState.EntityType;

public class TextureLoader {
	private static HashMap<BoxType, Texture> map = new HashMap<BoxType, Texture>();
	private static HashMap<EntityType, Texture> entity = new HashMap<EntityType, Texture>();
	private static final TextureLoader instance = new TextureLoader();

	public static TextureLoader getInstance(){
		return instance;
	}
	
	private TextureLoader() {
		map.put(BoxType.STONE, new Texture(Gdx.files.internal("Tiles/castleCenter.png")));
		map.put(BoxType.BOX, new Texture(Gdx.files.internal("Tiles/box.png")));
		map.put(BoxType.DOOR, new Texture(Gdx.files.internal("Tiles/door.png")));
		map.put(BoxType.FLAG, new Texture(Gdx.files.internal("Items/goal.png")));

		entity.put(EntityType.BOMB, new Texture(Gdx.files.internal("Items/darkbomb.png")));
		entity.put(EntityType.FLASH_BOMB, new Texture(Gdx.files.internal("Items/bombFlash.png")));
		entity.put(EntityType.KEY, new Texture(Gdx.files.internal("Items/keyYellow.png")));
		entity.put(EntityType.PLAYER, new Texture(Gdx.files.internal("Player/p1_front.png")));
		entity.put(EntityType.POWERUP_WALK_FAST, new Texture(Gdx.files.internal("Items/gemBlue.png")));
		entity.put(EntityType.POWERUP_WALK_WALL, new Texture(Gdx.files.internal("Items/gemYellow.png")));
		entity.put(EntityType.POWERUP_INVERT_TOUCHPAD, new Texture(Gdx.files.internal("Items/gemYellow.png")));
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
