package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Hud {
	private int topRightCount = 3;
	private int powerSize;
	private Color powerColor;
	private Texture topRightTex;
	private ShapeRenderer shapeRenderer;

	public Hud() {
		topRightTex = Tiles.getInstance().getTextureForType(GameState.EntityType.BOMB);
		shapeRenderer = new ShapeRenderer();
		powerSize = 100;
		powerColor = Color.RED;
	}
	
	public void setPowerSize(int size) {
		powerSize = size;
	}
	
	public void setPowerColor(Color col) {
		powerColor = col;
	}
	
	public void drawHud(SpriteBatch batch, float gameWidth, float gameHeight) {
		for (int i = 0; i < topRightCount; i++)
			batch.draw(topRightTex, 20 + i * 30, gameHeight - 40, 30, 30);

		if (powerSize > 0) {
			batch.end();
			shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
			shapeRenderer.begin(ShapeType.Filled);
			int powerupHeight = 16;

			shapeRenderer.setColor(Color.valueOf("0099e6"));
			this.drawRoundedRect(200, gameHeight - 41, this.powerSize + 2, powerupHeight + 2);
			
			/*shapeRenderer.setColor(Color.valueOf("444444"));
			this.drawRoundedRect(202, gameHeight - 42, this.powerSize, powerupHeight);

			shapeRenderer.setColor(Color.valueOf("555555"));
			this.drawRoundedRect(203, gameHeight - 43, this.powerSize, powerupHeight);
			*/
			shapeRenderer.setColor(powerColor);
			this.drawRoundedRect(200, gameHeight - 40, this.powerSize, powerupHeight);

			
			shapeRenderer.end();
			batch.begin();
		}
	}
	
	private void drawRoundedRect(float x, float y, float w, float h) {
		float circleRadius = h / 2;
		shapeRenderer.rect(x, y, this.powerSize, h);
		shapeRenderer.circle(x, y + circleRadius, circleRadius);
		shapeRenderer.circle(x + this.powerSize, y + circleRadius, circleRadius);
	}

}
