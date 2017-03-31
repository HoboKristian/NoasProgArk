package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyGdxGame extends ApplicationAdapter implements GameStateListener {
	GameListState gameListState;
	GameMenuState gameMenuState;
	GameRenderState gameRenderState;

	RenderUpdateState currentRenderState;

	@Override
	public void create() {
		this.gameListState = new GameListState();
		this.gameMenuState = new GameMenuState();
		this.gameRenderState = new GameRenderState();
		this.gameListState.init();
		this.gameMenuState.init();
		this.gameRenderState.init();

		GameState.getInstance().registerGameStateListener(this);
		GameState.getInstance().setRenderState(GameState.RenderState.GAME);


		NameInputField listener = new NameInputField(new NameInputFieldListener() {
			@Override
			public void result(String text, boolean cancelled) {
				System.out.println("sdfsdf");
				if (!cancelled && !text.matches(""))
					GameState.getInstance().name = text;
				ClientConnection.getInstance().registerName(GameState.getInstance().name, new GameHTTPResponse() {
					@Override
					public void result(JSONObject result) {
						System.out.println("cyka");
						try {
							GameState.getInstance().gameId = result.getString("gameid");
							GameState.getInstance().setRenderState(GameState.RenderState.GAME);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		Gdx.input.getTextInput(listener, "Set name", "", "Your name");
	}

	@Override
	public void render() {
		this.currentRenderState.update();
		this.currentRenderState.render();
	}

	@Override
	public void dispose() {
		this.currentRenderState.dispose();
	}

	@Override
	public void gameStateChanged(GameState.RenderState renderState) {
		switch (renderState) {
			case GAME:
				this.currentRenderState = gameRenderState;
				break;
			case LIST:
				this.currentRenderState = gameListState;
				break;
			case MENU:
				this.currentRenderState = gameMenuState;
				break;
		}
		this.currentRenderState.getsFocus();
	}
}
