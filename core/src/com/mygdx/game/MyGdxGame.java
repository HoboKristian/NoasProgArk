package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	GameState gameState;
	Player player;
	Array<Bomb> bombs = new Array<Bomb>();
	
	Texture bombImg, flashImg;
	
	OrthographicCamera cam;
	
	Tiles tiles;

	static float GAME_WIDTH;
	static float GAME_HEIGHT;
	
	@Override
	public void create () {
		gameState = new GameState();
		batch = new SpriteBatch();
		player = new Player(GameState.BLOCK_SIZE * 2, GameState.BLOCK_SIZE * 2);
		bombImg = new Texture(Gdx.files.internal("Items/bomb.png"));
		flashImg = new Texture(Gdx.files.internal("Items/bombFlash.png"));
		tiles = new Tiles();
		
		float GAME_WIDTH = Gdx.graphics.getWidth();
        float GAME_HEIGHT = Gdx.graphics.getHeight();
		cam = new OrthographicCamera(30, 30 * (GAME_HEIGHT / GAME_WIDTH));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.zoom = 1.0f;
		cam.update();
	}

	@Override
	public void render () {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector2 playerMovement = new Vector2(0, 0);
		if(Gdx.input.isKeyPressed(Keys.LEFT)) playerMovement.x -= GameState.BLOCK_SIZE * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) playerMovement.x += GameState.BLOCK_SIZE * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.UP)) playerMovement.y += GameState.BLOCK_SIZE * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) playerMovement.y -= GameState.BLOCK_SIZE * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.A)) {
			bombs.add(new Bomb(player.getPos().x + GameState.BLOCK_SIZE / 4, player.getPos().y + GameState.BLOCK_SIZE / 4, bombImg, flashImg));
		}

		Rectangle playerRectX = player.getHitbox(); playerRectX.x += playerMovement.x;
		Rectangle playerRectY = player.getHitbox(); playerRectY.y += playerMovement.y;
		boolean blockedX = false;
		boolean blockedY = false;
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				if (gameState.getState(x, y) == GameState.BoxType.OPEN) continue;
				
				Rectangle box = new Rectangle(x * GameState.BLOCK_SIZE, y * GameState.BLOCK_SIZE, GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
				if (box.overlaps(playerRectX)) {
					blockedX = true;
				} if (box.overlaps(playerRectY)) {
					blockedY = true;
				}
			}
			if (blockedX && blockedY)
				break;
		}

		if (!blockedX)
			player.move(playerMovement.x, 0);
		if (!blockedY)
			player.move(0, playerMovement.y);
		
		float BLOCK_PADDING = 1;
		/*float cameraX = cam.position.x - cam.viewportWidth / 2 + GameState.BLOCK_SIZE * BLOCK_PADDING;
		float cameraY = cam.position.y - cam.viewportHeight / 2 + GameState.BLOCK_SIZE * BLOCK_PADDING;
		Rectangle cameraRect = new Rectangle(cameraX, cameraY,
											cam.viewportWidth - GameState.BLOCK_SIZE * BLOCK_PADDING * 2,
											cam.viewportHeight - GameState.BLOCK_SIZE * BLOCK_PADDING * 2);
		*/
		float playerX = player.getPos().x + GameState.BLOCK_SIZE / 2;
		float playerY = player.getPos().y + GameState.BLOCK_SIZE / 2;
		cam.position.x = MathUtils.clamp(cam.position.x, playerX - GameState.BLOCK_SIZE * BLOCK_PADDING, playerX + GameState.BLOCK_SIZE * BLOCK_PADDING);
		cam.position.y = MathUtils.clamp(cam.position.y, playerY- GameState.BLOCK_SIZE * BLOCK_PADDING, playerY + GameState.BLOCK_SIZE * BLOCK_PADDING);
		/*if (!cameraRect.contains(player.getPos().cpy().add(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2))) {
		//if ( .dst2(new Vector2(cam.position.x, cam.position.y)) <= GameState.BLOCK_SIZE * 3) {
			cam.position.add(playerMovement.x, playerMovement.y, 0);
		}*/
		
		batch.begin();
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				if (gameState.getState(x, y) != GameState.BoxType.OPEN)
					batch.draw(tiles.getTextureForBox(gameState.getState(x, y)), x * GameState.BLOCK_SIZE, y * GameState.BLOCK_SIZE, GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
			}
		}
		for (Bomb b : bombs) {
			boolean keep = b.update(Gdx.graphics.getDeltaTime());
			Vector2 bombPos = b.getPos();
			if (keep) {
				batch.draw(b.img(), (int)bombPos.x, (int)bombPos.y, GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
			} else {
				int bx = (int)(bombPos.x / GameState.BLOCK_SIZE);
				int by = (int)(bombPos.y / GameState.BLOCK_SIZE);
				for (int x = bx - 1; x <= bx + 1; x++) {
					for (int y = by - 1; y <= by + 1; y++) {
						if (gameState.getState(x, y) == GameState.BoxType.BOX) {
							gameState.setState(GameState.BoxType.OPEN, x, y);
						}
					}
				}
				bombs.removeIndex(0);
				// remove bomb
			}
		}
		Vector2 playerPos = player.getPos();
		batch.draw(player.img(), (float)playerPos.x, (float)playerPos.y, GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
