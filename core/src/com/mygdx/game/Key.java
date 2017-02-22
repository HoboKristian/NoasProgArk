package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Key extends StaticEntity {
	boolean taken;
	private List<KeyListener> listeners = new ArrayList<>();
	public Key(Vector2 pos, Vector2 size, Texture tex) {
		super(pos, size, tex);
		this.taken = false;
	}
	
	@Override
	public void collide() {
		this.taken = true;
		this.notifyKeyListeners(this);
	}
	
	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return this.taken;
	}

	@Override
	public void update(float delta_t) {
		// TODO Auto-generated method stub
	}
	
	public void registerKeyListener (KeyListener listener) {
		// Add the listener to the list of registered listeners
		this.listeners.add(listener);
	}
	public void unregisterKeyListener (KeyListener listener) {
		// Remove the listener from the list of the registered listeners
		this.listeners.remove(listener);
	}
	protected void notifyKeyListeners (Key key) {
		// Notify each of the listeners in the list of registered listeners
		this.listeners.forEach(listener -> listener.keyUnlocked(key));
	}

}
