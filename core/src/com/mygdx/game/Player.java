package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Player extends MoveableEntity implements PowerupListener{
	Vector2 velocity;
	Class<?> powerupClass;
	float powerupDuration = 0;
	int numberOfBombs;
	public boolean invertedControls = false;

	List<PowerupEffectWrapper> powerups = new ArrayList<>();
	
	public Player(Vector2 pos, Vector2 size, Texture tex) {
		super(pos, size, tex);
		this.velocity = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
		numberOfBombs = 3;
	}
	public void move(Vector2 movement) {
		this.move(movement.x, movement.y);
	}
	public void move(float delta_x, float delta_y) {
		super.moveEntity(new Vector2(delta_x, delta_y));
	}
	public Vector2 getVelocity() {
		return this.velocity;
	}
	public Class<?> getPowerupClass() {
		return powerupClass;
	}
	
	public float getPowerupDuration() {
		return powerupDuration;
	}
	
	@Override
	public void update(float delta_t) {
		List<PowerupEffectWrapper> toRemove = new ArrayList<>();
		for (PowerupEffectWrapper p : powerups) {
			if (p.update(delta_t))
				toRemove.add(p);
		}
		for (PowerupEffectWrapper p : toRemove) {
			powerups.remove(p);
		}
	}
	
	@Override
	public Rectangle getCollisionRectangle() {
		Rectangle r = super.getCollisionRectangle();
		Rectangle playerR = new Rectangle(r.x + r.width / 4, r.y + r.height / 4, r.width / 2, r.height / 2);
		return playerR;
	}
	
	@Override
	public void powerupPickedUp(Entity powerup) {
		powerupClass = powerup.getClass();
		if (powerup instanceof PowerupWalkFaster) {
			powerups.add(new PowerupEffectWrapper(5, new PowerupEffect() {
				@Override
				public void start() {
					velocity = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
				}

				@Override
				public void end() {
					velocity = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
				}
			}));
		} else if (powerup instanceof PowerupInvertTouchpad) {
			System.out.println("inve");
			powerups.add(new PowerupEffectWrapper(5, new PowerupEffect() {
				@Override
				public void start() {
					System.out.println("inverted");
					invertedControls = true;
				}

				@Override
				public void end() {
					invertedControls = false;
				}
			}));
		}
	}

	public int getNumberOfBombs(){
		return this.numberOfBombs;
	}
}

interface PowerupEffect {
	public void start();
	public void end();
}

class PowerupEffectWrapper {
	public float durationLeft;
	public PowerupEffect powerupEffect;
	PowerupEffectWrapper(float duration, PowerupEffect effect) {
		this.durationLeft = duration;
		this.powerupEffect = effect;
		this.powerupEffect.start();
	}

	boolean update(float delta_t) {
		this.durationLeft -= delta_t;
		if (this.durationLeft <= 0) {
			powerupEffect.end();
			return true;
		} else {
			return false;
		}

	}
}