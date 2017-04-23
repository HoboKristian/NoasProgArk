package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends MoveableEntity implements PowerupListener{
	Vector2 velocity;
	Class<?> powerupClass;
	float powerupDuration = 0;
	int numberOfBombs;
	public boolean invertedControls = false;

	public List<PowerupEffectWrapper> powerups = new ArrayList<>();

	private final Vector2 fast_velocity   = new Vector2(GameState.BLOCK_SIZE    , GameState.BLOCK_SIZE    );
	private final Vector2 slow_velocity   = new Vector2(GameState.BLOCK_SIZE / 4, GameState.BLOCK_SIZE / 4);
	private final Vector2 normal_velocity = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
	private final Vector2 freeze_velocity = new Vector2(0, 0);

	public Player(Vector2 pos, Vector2 size, Texture tex) {
		super(pos, size, tex);
		this.velocity = normal_velocity.cpy();
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
		if (powerup instanceof PowerupWalkFaster) {
			this.powerupWalkFaster();
		} else if (powerup instanceof PowerupInvertTouchpad) {
			ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "invert");
		} else if (powerup instanceof PowerupWalkSlower) {
			ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "slower");
		} else if (powerup instanceof PowerupWalkFreeze) {
			ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "freeze");
		} else if (powerup instanceof PowerupMysterybox) {
			Random rand = new Random();
			int n = rand.nextInt(4);
			switch (n) {
				case 0:
					this.powerupWalkFaster();
					break;
				case 1:
					ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "invert");
					break;
				case 2:
					ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "slower");
					break;
				case 3:
					ClientConnection.getInstance().sendPowerup(GameState.getInstance().gameId, GameState.getInstance().gameOpponent, "freeze");
					break;
				default:
					break;
			}
		}
	}

	public void powerupInvertControls() {
		powerups.add(new PowerupEffectWrapper(5, Color.YELLOW, new PowerupEffect() {
			@Override
			public void start() { invertedControls = true; }

			@Override
			public void end() { invertedControls = false; }
		}));
	}

	public void powerupWalkFaster() {
		ClientConnection.getInstance().sendBuff(GameState.getInstance().gameId, GameState.getInstance().name, "faster", 5000);
		powerups.add(new PowerupEffectWrapper(5, Color.RED, new PowerupEffect() {
			@Override
			public void start() { velocity = fast_velocity.cpy(); }

			@Override
			public void end() { velocity = normal_velocity.cpy(); }
		}));
	}

	public void powerupWalkSlower() {
		powerups.add(new PowerupEffectWrapper(5, Color.GREEN, new PowerupEffect() {
			@Override
			public void start() { velocity = slow_velocity.cpy(); }

			@Override
			public void end() { velocity = normal_velocity.cpy(); }
		}));
	}

	public void powerupWalkFreeze() {
		powerups.add(new PowerupEffectWrapper(5, Color.BLUE, new PowerupEffect() {
			@Override
			public void start() { velocity = freeze_velocity.cpy(); }

			@Override
			public void end() { velocity = normal_velocity.cpy(); }
		}));
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
	public float baseDuration;
	public PowerupEffect powerupEffect;
	public Color powerupColor;
	PowerupEffectWrapper(float duration, Color col, PowerupEffect effect) {
		this.durationLeft = duration;
		this.baseDuration = duration;

		this.powerupColor = col;

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