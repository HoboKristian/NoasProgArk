package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends MoveableEntity implements PowerupListener{
	Vector2 velocity;
	Class<?> powerupClass;
	float powerupDuration = 0;
	int numberOfBombs;
	
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
		if (powerupDuration > 0) {
			powerupDuration -= delta_t;
			if (powerupDuration <= 0) {
				this.velocity = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
			}
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
		// TODO Auto-generated method stub
		powerupClass = powerup.getClass();
		if (powerup instanceof PowerupWalkFaster) {
			this.velocity = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
			powerupDuration = 5;
		}
	}

	public int getNumberOfBombs(){
		return getNumberOfBombs();
	}
}
