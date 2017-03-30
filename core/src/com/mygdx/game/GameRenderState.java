package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kristianflatheimjensen on 23/03/2017.
 */

public class GameRenderState extends RenderUpdateState {
    SpriteBatch batch, hudBatch;
    Sprite backgroundImage;
    GameState gameState;
    Player player, opponent;
    Array<Entity> entities = new Array<Entity>();

    Hud hud;

    Texture bombImg, flashImg;

    OrthographicCamera cam;

    TextureLoader tiles;

    Touchpad touchpad;
    Touchpad.TouchpadStyle touchpadStyle;
    Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;
    Stage stage;
    ClientConnection conn;

    static float GAME_WIDTH;
    static float GAME_HEIGHT;

    @Override
    public void init() {
        gameState = GameState.getInstance();
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        hud = new Hud();
        Vector2 playerPos = new Vector2(4 * GameState.BLOCK_SIZE, 4 * GameState.BLOCK_SIZE);
        Vector2 playerSize = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
        player = new Player(playerPos, playerSize, TextureLoader.getInstance().getTextureForType(GameState.EntityType.PLAYER));
        opponent = new Player(playerPos, playerSize, TextureLoader.getInstance().getTextureForType(GameState.EntityType.KEY));
        backgroundImage = new Sprite(new Texture(Gdx.files.internal("bg_castle.png")));
        GAME_WIDTH = Gdx.graphics.getWidth();
        GAME_HEIGHT = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(30, 30 * (GAME_HEIGHT / GAME_WIDTH));

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.zoom = 1.0f;
        cam.update();

        Door door = new Door(new Vector2(5 * GameState.BLOCK_SIZE, 9 * GameState.BLOCK_SIZE), new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE));
        Key key = new Key(new Vector2(20, 20), new Vector2(2, 2), TextureLoader.getInstance().getTextureForType(GameState.EntityType.KEY));
        gameState.setTile(door, 5, 9);
        key.registerKeyListener(door);
        entities.add(key);

        PowerupWalkFaster powerup = new PowerupWalkFaster(new Vector2(24, 28), new Vector2(2, 2));
        entities.add(powerup);
        powerup.registerPowerupListener(player);


        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture(Gdx.files.internal("Touchpad/touchBackground.png")));
        //Set knob image
        touchpadSkin.add("touchKnob", new Texture(Gdx.files.internal("Touchpad/touchKnob.png")));
        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();
        //Create Drawable's from TouchPad skin
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;
        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(15, 15, 200, 200);
        //Create a Stage and add TouchPad
        stage = new Stage(new FillViewport(GAME_WIDTH, GAME_HEIGHT), batch);
        stage.addActor(touchpad);

        conn = ClientConnection.getInstance();
    }

    public void handleInput(Vector2 playerMovement) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) playerMovement.x -= player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) playerMovement.x += player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) playerMovement.y += player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) playerMovement.y -= player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
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

    public void renderPlayers() {
        this.batchDrawPosSize(batch, player.getTexture(), player.getPos(), player.getSize());
        this.batchDrawPosSize(batch, opponent.getTexture(), opponent.getPos(), opponent.getSize());
    }

    @Override
    public void update() {
        cam.update();
        Vector2 playerMovement = new Vector2(0, 0);
        handleInput(playerMovement);
        playerMovement = new Vector2(touchpad.getKnobPercentX() * player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime(),
                touchpad.getKnobPercentY() * player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime());
        testPlayerMovement(playerMovement);
        testEntityCollision();

        clampCamera();

        player.update(Gdx.graphics.getDeltaTime());
        conn.sendPos(player.pos.x, player.pos.y, GameState.getInstance().gameId, GameState.getInstance().name);
        conn.getGame(GameState.getInstance().gameId, new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                try {
                    String player1 = result.getString("player1");
                    String player2 = result.getString("player2");
                    JSONObject posObj;


                    if (GameState.getInstance().name.matches(player1)) {
                        posObj = result.getJSONObject("2");
                    } else {
                        posObj = result.getJSONObject("1");
                    }

//                    System.out.println(player1 + " " + player2 + " " + GameState.getInstance().name);
//                    System.out.println(posObj);

                    float xpos = (float)posObj.getDouble("xpos");
                    float ypos = (float)posObj.getDouble("ypos");

                    opponent.setPos(new Vector2(xpos, ypos));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void render() {
        Gdx.input.setInputProcessor(stage);

        batch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        //batch.draw(backgroundImage, -20, -20);
        renderTiles();
        renderEntities();
        renderPlayers();
        batch.end();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());

        hudBatch.begin();
        Color powerupCol = Color.BLACK;
        if (player.getPowerupClass() == PowerupWalkFaster.class)
            powerupCol = Color.BLUE;
        hud.setPowerColor(powerupCol);
        hud.setPowerSize(((int)(player.getPowerupDuration() * 30)));
        hud.drawHud(hudBatch, GAME_WIDTH, GAME_HEIGHT);
        hudBatch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}
