package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Sprite backgroundImage;
	GameState gameState;
	Player player;
	Array<Entity> entities = new Array<Entity>();
	
	Texture bombImg, flashImg;
	
	OrthographicCamera cam;
	
	Tiles tiles;

	static float GAME_WIDTH;
	static float GAME_HEIGHT;
	
	@Override
	public void create () {
		gameState = new GameState();
		batch = new SpriteBatch();
		Vector2 playerPos = new Vector2(4 * GameState.BLOCK_SIZE, 4 * GameState.BLOCK_SIZE);
		Vector2 playerSize = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
		player = new Player(playerPos, playerSize, Tiles.getInstance().getTextureForType(GameState.EntityType.PLAYER));
		backgroundImage = new Sprite(new Texture(Gdx.files.internal("bg_castle.png")));
		float GAME_WIDTH = Gdx.graphics.getWidth();
		float GAME_HEIGHT = Gdx.graphics.getHeight();
		cam = new OrthographicCamera(30, 30 * (GAME_HEIGHT / GAME_WIDTH));

		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		cam.zoom = 1.0f;
		cam.update();
		
		Door door = new Door(new Vector2(5 * GameState.BLOCK_SIZE, 9 * GameState.BLOCK_SIZE), new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE));
		Key key = new Key(new Vector2(20, 20), new Vector2(2, 2), Tiles.getInstance().getTextureForType(GameState.EntityType.KEY));
		gameState.setTile(door, 5, 9);
		key.registerKeyListener(door);
		entities.add(key);
		
		PowerupWalkFaster powerup = new PowerupWalkFaster(new Vector2(24, 28), new Vector2(2, 2));
		entities.add(powerup);
		powerup.registerPowerupListener(player);
	}
	
	public void handleInput(Vector2 playerMovement) {
		if(Gdx.input.isKeyPressed(Keys.LEFT)) playerMovement.x -= player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) playerMovement.x += player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.UP)) playerMovement.y += player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.DOWN)) playerMovement.y -= player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Keys.A)) {
			Vector2 bombPos = new Vector2(player.getPos().x + GameState.BLOCK_SIZE / 4, player.getPos().y + GameState.BLOCK_SIZE / 4);
			Vector2 bombSize = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
			entities.add(new Bomb(bombPos, bombSize));
		}
	}
	
	public void testPlayerMovement(Vector2 playerMovement) {
		Rectangle playerRectX = player.getCollisionRectangle(); playerRectX.x += playerMovement.x;
		Rectangle playerRectY = player.getCollisionRectangle(); playerRectY.y += playerMovement.y;
		boolean blockedX = false;
		boolean blockedY = false;
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				Tile t = gameState.getTile(x, y);
				if (t.getType() == GameState.BoxType.OPEN) continue;
				
				Rectangle box = t.getCollisionRectangle();
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
	}
	
	public void testEntityCollision() {
		Rectangle playerRect = player.getCollisionRectangle();
		for (Entity e : entities) {
			if (playerRect.overlaps(e.getCollisionRectangle())) {
				e.collide();
			}
		}
	}

	public void clampCamera() {
		float BLOCK_PADDING = 1;
		float playerX = player.getPos().x + GameState.BLOCK_SIZE / 2;
		float playerY = player.getPos().y + GameState.BLOCK_SIZE / 2;
		cam.position.x = MathUtils.clamp(cam.position.x, playerX - GameState.BLOCK_SIZE * BLOCK_PADDING, playerX + GameState.BLOCK_SIZE * BLOCK_PADDING);
		cam.position.y = MathUtils.clamp(cam.position.y, playerY- GameState.BLOCK_SIZE * BLOCK_PADDING, playerY + GameState.BLOCK_SIZE * BLOCK_PADDING);
	}
	
	public void batchDrawPosSize(SpriteBatch b, Texture t, Vector2 p, Vector2 s) {
		b.draw(t, p.x, p.y, s.x, s.y);
	}
	
	public void renderTiles() {
		for (int x = 0; x < GameState.WIDTH; x++) {
			for (int y = 0; y < GameState.HEIGHT; y++) {
				Tile t = gameState.getTile(x, y);
				if (t.getType() != GameState.BoxType.OPEN) {
					if (t.shouldRemove())
						gameState.setTile(new Tile(new Vector2(x, y), new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE), GameState.BoxType.OPEN), x, y);
					this.batchDrawPosSize(batch, t.getTexture(), t.getPos(), t.getSize());
				}
			}
		}
	}
	
	public void renderEntities() {
		for (int i = 0; i < entities.size; i++) {
			Entity e = entities.get(i);
			e.update(Gdx.graphics.getDeltaTime());
			if (!e.shouldRemove()) {
				this.batchDrawPosSize(batch, e.getTexture(), e.getPos(), e.getSize());
			} else {
				if (e instanceof Bomb) {
					Vector2 bombPos = e.getPos();
					int bx = (int)(bombPos.x / GameState.BLOCK_SIZE);
					int by = (int)(bombPos.y / GameState.BLOCK_SIZE);
					for (int x = bx - 1; x <= bx + 1; x++) {
						for (int y = by - 1; y <= by + 1; y++) {
							Tile t = gameState.getTile(x, y);
							if (t != null && t.getType() == GameState.BoxType.BOX) {
								gameState.setTile(new Tile(new Vector2(x, y), new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE), GameState.BoxType.OPEN), x, y);
							}
						}
					}
				}
				entities.removeIndex(i);
				i--;
			}
		}
	}
	
	public void renderPlayer() {
		this.batchDrawPosSize(batch, player.getTexture(), player.getPos(), player.getSize());
	}
	
	@Override
	public void render () {
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Vector2 playerMovement = new Vector2(0, 0);
		handleInput(playerMovement);
		testPlayerMovement(playerMovement);
		testEntityCollision();
		
		clampCamera();

		batch.begin();
		//batch.draw(backgroundImage, -20, -20);
		renderTiles();
		renderEntities();
		renderPlayer();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
