package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends MoveableEntity{
	public Player(Vector2 pos, Vector2 size, Texture tex) {
		super(pos, size, tex);
	}
	public void move(Vector2 movement) {
		this.move(movement.x, movement.y);
	}
	public void move(float delta_x, float delta_y) {
		super.moveEntity(new Vector2(delta_x, delta_y));
	}
	
	@Override
	public Rectangle getCollisionRectangle() {
		Rectangle r = super.getCollisionRectangle();
		Rectangle playerR = new Rectangle(r.x + r.width / 4, r.y + r.height / 4, r.width / 2, r.height / 2);
		return playerR;
	}
	
	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void update(float delta_t) {
		// TODO Auto-generated method stub
		
	}

}
