package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class PowerupMysterybox extends StaticEntity {
    private List<PowerupListener> listeners = new ArrayList<PowerupListener>();
    private boolean pickedUp;

    public PowerupMysterybox(Vector2 pos, Vector2 size) {
        super(pos, size, TextureLoader.getInstance().getTextureForType(GameState.EntityType.POWERUP_MYSTERYBOX));
        this.pickedUp = false;
    }

    @Override
    public boolean shouldRemove() {
        return this.pickedUp;
    }

    @Override
    public void collide() {
        this.pickedUp = true;
        this.notifyPowerupListeners(this);
    }

    public void registerPowerupListener (PowerupListener listener) {
        // Add the listener to the list of registered listeners
        this.listeners.add(listener);
    }
    public void unregisterPowerupListener (PowerupListener listener) {
        // Remove the listener from the list of the registered listeners
        this.listeners.remove(listener);
    }
    protected void notifyPowerupListeners (PowerupMysterybox powerup) {
        // Notify each of the listeners in the list of registered listeners
        for (PowerupListener listener : listeners) {
            listener.powerupPickedUp(powerup);
        }
    }
}
