package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Bomb {
	Texture bombImg, flashImg;
	Vector2 pos;
	
	float timeToDetonate;
	float timeToRemove;
	static float BOMBTIMER = 1;
	static float FLASHTIMER = 0.5f;
	
	boolean detonating;
	
	public Bomb(Vector2 pos, Texture bImg, Texture fImg) {
		new Bomb(pos.x, pos.y, bImg, fImg);
	}
	
	public Bomb(float x, float y, Texture bImg, Texture fImg) {
		System.out.println(x);
		System.out.println(y);
		pos = new Vector2(x, y);
		bombImg = bImg;
		flashImg = fImg;
		detonating = true;
		timeToDetonate = Bomb.BOMBTIMER;
		timeToRemove = Bomb.FLASHTIMER;
	}
	
	public boolean update(float delta_h) {
		if (detonating) {
			timeToDetonate -= delta_h;
			if (timeToDetonate <= 0)
				detonating = false;
		}
		else {
			timeToRemove -= delta_h;
		}
		
		if (timeToRemove <= 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public Texture img() {
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
