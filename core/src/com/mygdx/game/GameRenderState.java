package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

    TextButton.TextButtonStyle textButtonStyle; //dialog button
    TextButton exitButton;
    BitmapFont font;


    static float GAME_WIDTH;
    static float GAME_HEIGHT;

    @Override
    public void init() {
        gameState = GameState.getInstance();
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        hud = new Hud();
        backgroundImage = new Sprite(new Texture(Gdx.files.internal("bg_castle.png")));
        GAME_WIDTH = Gdx.graphics.getWidth();
        GAME_HEIGHT = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(30, 30 * (GAME_HEIGHT / GAME_WIDTH));

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.zoom = 1.0f;
        cam.update();

        textButtonStyle = new TextButton.TextButtonStyle();
        font = new BitmapFont();
        font.getData().setScale(2);
        textButtonStyle.font = font;

        //Create a touchpad skin
        touchpadSkin = new Skin();
        //Set background image
        touchpadSkin.add("touchBackground", new Texture(Gdx.files.internal("Touchpad/bigball.png")));
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

        conn = ClientConnection.getInstance();

        resetState();
    }

    public void resetState() {
        entities = new Array<>();

        Vector2 playerPos = new Vector2(9 * GameState.BLOCK_SIZE, 2 * GameState.BLOCK_SIZE);
        Vector2 playerSize = new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE);
        player = new Player(playerPos, playerSize, TextureLoader.getInstance().getTextureForType(GameState.EntityType.PLAYER));
        opponent = new Player(playerPos, playerSize, TextureLoader.getInstance().getTextureForType(GameState.EntityType.KEY));

        Door door = new Door(new Vector2(9 * GameState.BLOCK_SIZE, 21 * GameState.BLOCK_SIZE), new Vector2(GameState.BLOCK_SIZE, GameState.BLOCK_SIZE));
        Key key = new Key(new Vector2(6 * GameState.BLOCK_SIZE, 18 * GameState.BLOCK_SIZE), new Vector2(2, 2), TextureLoader.getInstance().getTextureForType(GameState.EntityType.KEY));
        gameState.setTile(door, 9, 20);
        key.registerKeyListener(door);
        entities.add(key);

        PowerupWalkFaster powerupWalkFaster = new PowerupWalkFaster(new Vector2(6 * GameState.BLOCK_SIZE, 3 * GameState.BLOCK_SIZE), new Vector2(3, 3));
        PowerupInvertTouchpad powerupInvertTouchpad = new PowerupInvertTouchpad(new Vector2(7 * GameState.BLOCK_SIZE, 3 * GameState.BLOCK_SIZE), new Vector2(3, 3));
        PowerupWalkSlower powerupWalkSlower = new PowerupWalkSlower(new Vector2(8 * GameState.BLOCK_SIZE, 3 * GameState.BLOCK_SIZE), new Vector2(3, 3));
        PowerupWalkFreeze powerupWalkFreeze = new PowerupWalkFreeze(new Vector2(5 * GameState.BLOCK_SIZE, 3 * GameState.BLOCK_SIZE), new Vector2(3, 3));

        entities.add(powerupWalkFaster);
        entities.add(powerupInvertTouchpad);
        entities.add(powerupWalkSlower);
        entities.add(powerupWalkFreeze);

        powerupWalkFaster.registerPowerupListener(player);
        powerupInvertTouchpad.registerPowerupListener(player);
        powerupWalkSlower.registerPowerupListener(player);
        powerupWalkFreeze.registerPowerupListener(player);

        //Create new TouchPad with the created style
        touchpad = new Touchpad(10, touchpadStyle);
        //setBounds(x,y,width,height)
        touchpad.setBounds(GAME_WIDTH - 350, 50, 250, 250);

        //Create a Stage and add TouchPad
        stage = new Stage(new FillViewport(GAME_WIDTH, GAME_HEIGHT), batch);
        stage.addActor(touchpad);

        GameState.getInstance().loadMap();
    }

    public void getsFocus() {
        conn.registerWinnerCallback(new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                try {
                    System.out.println("RegisterWinnercallbacked");
                    if (result.getString("gameid").equals(GameState.getInstance().gameId)) {
                        GameState.getInstance().setGameFinished(result.getString("winner"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        conn.registerPowerupCallback(new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                try {
                    String type = result.getString("type");
                    if (type.equals("invert"))
                        player.powerupInvertControls();
                    if (type.equals("slower"))
                        player.powerupWalkSlower();
                    if (type.equals("freeze"))
                        player.powerupWalkFreeze();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    int bombLimiter = 0;
    public void handleInput(Vector2 playerMovement) {
        int directionInverter = player.invertedControls ? -1 : 1;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            playerMovement.x -= player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime() * directionInverter;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            playerMovement.x += player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime() * directionInverter;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            playerMovement.y += player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime() * directionInverter;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            playerMovement.y -= player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime() * directionInverter;
        if (bombLimiter > 0) bombLimiter--;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isTouched() && !touchpad.isTouched()) {
            if (player.getNumberOfBombs() > 0 && bombLimiter <= 0) {
                bombLimiter = 15; //make sure you dont put all bombs down in an instant. This should be 1/4 second.
                Vector2 bombPos = new Vector2(player.getPos().x + GameState.BLOCK_SIZE / 4, player.getPos().y + GameState.BLOCK_SIZE / 4);
                Vector2 bombSize = new Vector2(GameState.BLOCK_SIZE / 2, GameState.BLOCK_SIZE / 2);
                entities.add(new Bomb(bombPos, bombSize));
                player.numberOfBombs--;
            }
        }
    }

    public void testPlayerMovement(Vector2 playerMovement) {
        Rectangle playerRectX = player.getCollisionRectangle();
        playerRectX.x += playerMovement.x;
        Rectangle playerRectY = player.getCollisionRectangle();
        playerRectY.y += playerMovement.y;
        boolean blockedX = false;
        boolean blockedY = false;
        for (int x = 0; x < GameState.WIDTH; x++) {
            for (int y = 0; y < GameState.HEIGHT; y++) {
                Tile t = gameState.getTile(x, y);

                if (t.getType() == GameState.BoxType.OPEN) {
                    continue;
                }

                Rectangle box = t.getCollisionRectangle();
                if (box.overlaps(playerRectX)) {
                    blockedX = true;
                }
                if (box.overlaps(playerRectY)) {
                    blockedY = true;
                }

                if (t.getType() == GameState.BoxType.FLAG && box.overlaps(playerRectY)) {
                    GameState state = GameState.getInstance();
                    String gameId = state.gameId;
                    String winner = state.name;
                    conn.sendWinner(gameId, winner);
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

    public void gameFinished(String winner) {
        this.exitButton = new TextButton("Exit to Menu", this.textButtonStyle); //Set the button up
        this.exitButton.setBounds(600, 250, 400, 150);

        Skin skin = new Skin(Gdx.files.internal("Skin/uiskin.json"));

        final Dialog dialog = new Dialog("Game Finished", skin) {

            @Override
            public float getPrefWidth() {
                // force dialog width
                // return Gdx.graphics.getWidth() / 2;
                return 700f;
            }

            @Override
            public float getPrefHeight() {
                // force dialog height
                // return Gdx.graphics.getWidth() / 2;
                return 400f;
            }
        };

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        this.exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameState state = GameState.getInstance();
                state.gameFinished = false;
                state.gameId = null;
                state.gameWinner = null;
                resetState();
                state.setRenderState(GameState.RenderState.MENU);
                dialog.hide();
            }
        });

        float btnSize = 80f;
        Table t = new Table();
        Label label = new Label(String.format("The winner is: %s", winner), skin);
        label.setFontScale(2.0f);
        t.add(label);
        t.row();
        t.add(exitButton).width(btnSize).height(btnSize);
        dialog.getButtonTable().add(t).center().padBottom(btnSize);
        dialog.show(stage).setPosition(
                (GAME_WIDTH / 2) - (720 / 2),
                (GAME_HEIGHT) - (GAME_HEIGHT - 40));

        dialog.setName("quitDialog");
        stage.addActor(dialog);
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
        float playerY = player.getPos().y + GameState.BLOCK_SIZE;
        cam.position.x = MathUtils.clamp(cam.position.x, playerX - GameState.BLOCK_SIZE * BLOCK_PADDING, playerX + GameState.BLOCK_SIZE * BLOCK_PADDING);
        cam.position.y = MathUtils.clamp(cam.position.y, playerY - GameState.BLOCK_SIZE * BLOCK_PADDING, playerY + GameState.BLOCK_SIZE * BLOCK_PADDING);
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
                    int bx = (int) (bombPos.x / GameState.BLOCK_SIZE);
                    int by = (int) (bombPos.y / GameState.BLOCK_SIZE);
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
    }

    int limiter = 0;
    int limiterMax = 15;
    @Override
    public void update() {
        if (gameState.gameFinished) {
            this.gameFinished(gameState.gameWinner);
            return;
        }
        cam.update();
        Vector2 playerMovement = new Vector2(0, 0);
        handleInput(playerMovement);
        int directionInverter = player.invertedControls ? -1 : 1;
        playerMovement = new Vector2(touchpad.getKnobPercentX() * player.getVelocity().x * 5 * Gdx.graphics.getDeltaTime() * directionInverter,
                touchpad.getKnobPercentY() * player.getVelocity().y * 5 * Gdx.graphics.getDeltaTime() * directionInverter);
        testPlayerMovement(playerMovement);
        testEntityCollision();

        clampCamera();

        player.update(Gdx.graphics.getDeltaTime());
        limiter++;
        if (limiter == limiterMax) {
            limiter = 0;
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

                        float xpos = (float) posObj.getDouble("xpos");
                        float ypos = (float) posObj.getDouble("ypos");

                        opponent.setPos(new Vector2(xpos, ypos));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void render() {
        Gdx.input.setInputProcessor(stage);

        batch.setProjectionMatrix(cam.combined);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 0.2f);
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
        hud.drawHud(hudBatch, player.powerups, player.getPos(), opponent.getPos(), 31 * GameState.BLOCK_SIZE, GameState.BLOCK_SIZE, GAME_WIDTH, GAME_HEIGHT);
        hudBatch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
