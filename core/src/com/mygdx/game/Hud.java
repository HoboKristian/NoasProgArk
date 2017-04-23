package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class Hud {
	private Texture topRightTex;
	private ShapeRenderer shapeRenderer;
	private ShapeRenderer progressRenderer;

	float progressWidth = 8.0f;

	float progressImageSize = 80.0f;
	float progressPadding = 40.0f;

	public Hud() {
		topRightTex = TextureLoader.getInstance().getTextureForType(GameState.EntityType.BOMB);
		shapeRenderer = new ShapeRenderer();
		progressRenderer = new ShapeRenderer();
	}

	public void drawHud(SpriteBatch batch, List<PowerupEffectWrapper> powerups, Vector2 playerPos, Vector2 opponentPos, int topRightCount, String[] opponentBuffs, float goalY, float startY, float gameWidth, float gameHeight) {
		for (int i = 0; i < topRightCount; i++) {
			batch.draw(topRightTex, 15 + (i * 80), gameHeight - 100, 80, 80);
		}

		if (powerups.size() > 0) {
			batch.end();
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.begin(ShapeType.Filled);
			int powerupHeight = 40;
			int topPadding = 10;
			int yIndex = 0;

			for (PowerupEffectWrapper powerup : powerups) {
				float powerSize = 300 * (powerup.durationLeft / powerup.baseDuration);
				float yStart = gameHeight - (powerupHeight + topPadding) * (yIndex + 1);

				shapeRenderer.setColor(powerup.powerupColor);
				this.drawRoundedRect((gameWidth / 2) - 150, yStart - 2, powerSize, powerupHeight);
				//shapeRenderer.setColor(Color.valueOf("33abe8"));
				//this.drawRoundedRect((gameWidth / 2) - powerSize, yStart, powerSize, powerupHeight);
				yIndex++;
			}
			shapeRenderer.end();
			batch.begin();
		}

		batch.end();
		progressRenderer.begin(ShapeType.Filled);
		progressRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		progressRenderer.setColor(Color.BLACK);
		progressRenderer.rect(gameWidth - progressPadding - progressWidth, progressPadding, progressWidth, gameHeight - progressPadding * 2);
		progressRenderer.end();
		batch.begin();
		this.drawProgressImage(batch, TextureLoader.getInstance().getTextureForType(GameState.BoxType.FLAG), gameWidth, gameHeight, goalY, startY, goalY);
		this.drawProgressImage(batch, TextureLoader.getInstance().getTextureForType(GameState.EntityType.OPPONENT), gameWidth, gameHeight, goalY, startY, opponentPos.y, opponentBuffs);
		this.drawProgressImage(batch, TextureLoader.getInstance().getTextureForType(GameState.EntityType.PLAYER), gameWidth, gameHeight, goalY, startY, playerPos.y);
	}

	private void drawProgressImage(SpriteBatch batch, Texture texture, float gameWidth, float gameHeight, float goalY, float startY, float yPosition) {
		this.drawProgressImage(batch, texture, gameWidth, gameHeight, goalY, startY, yPosition, new String[0]);
	}

	private void drawProgressImage(SpriteBatch batch, Texture texture, float gameWidth, float gameHeight, float goalY, float startY, float yPosition, String[] opponentBuffs) {
		float progressHeight = gameHeight - 20;

		float imageX = gameWidth - progressPadding - progressWidth - progressImageSize / 2;
		float imageY = progressPadding + Math.min(yPosition / goalY, 1.0f) * progressHeight - progressImageSize / 2;
		batch.draw(texture, imageX, imageY, progressImageSize, progressImageSize);

		batch.end();
		shapeRenderer.begin(ShapeType.Filled);
		for (int i = 0; i < opponentBuffs.length; i++) {
			String buff = opponentBuffs[i];
			Color buffColor = Color.BLACK;
			switch (buff) {
				case "faster":
					buffColor = Color.RED;
					break;
				case "slower":
					buffColor = Color.GREEN;
					break;
				case "freeze":
					buffColor = Color.BLUE;
					break;
				case "invert":
					buffColor = Color.YELLOW;
					break;
			}
			shapeRenderer.setColor(buffColor);
			shapeRenderer.rect(imageX - 10, imageY + 5 * i, 10, 5);
		}
		shapeRenderer.end();
		batch.begin();
	}
	
	private void drawRoundedRect(float x, float y, float w, float h) {
		float circleRadius = h / 2;
		shapeRenderer.rect(x, y, w, h);
		shapeRenderer.circle(x, y + circleRadius, circleRadius);
		shapeRenderer.circle(x + w, y + circleRadius, circleRadius);
	}
}
