package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Bomb extends Entity {
	Texture bombImg, flashImg;
	
	float timeToDetonate;
	float timeToRemove;
	static float BOMBTIMER = 1;
	static float FLASHTIMER = 0.5f;
	
	boolean detonating;
	
	public Bomb(Vector2 pos, Vector2 size, Texture bImg, Texture fImg) {
		super(pos, size);
		bombImg = bImg;
		flashImg = fImg;
		detonating = true;
		timeToDetonate = Bomb.BOMBTIMER;
		timeToRemove = Bomb.FLASHTIMER;
	}
	
	public Bomb(Texture bImg, Texture fImg) {
		super(0, 0, 0 ,0);
		new Bomb(new Vector2(0, 0), new Vector2(0, 0),bImg, fImg);
	}
	
	@Override
	public void update(float delta_t) {
		if (detonating) {
			timeToDetonate -= delta_t;
			if (timeToDetonate <= 0)
				detonating = false;
		} else {
			timeToRemove -= delta_t;
		}
	}
	
	@Override
	public boolean shouldRemove() {
		if (timeToRemove <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Texture getTexture() {
		if (detonating && timeToDetonate > 0)
			return this.bombImg;
		else
			return this.flashImg;
	}
	
	public void dispose() {
		bombImg.dispose();
		flashImg.dispose();
	}
}
