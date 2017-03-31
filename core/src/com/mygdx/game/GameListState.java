package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kristianflatheimjensen on 24/03/2017.
 */

public class GameListState extends RenderUpdateState {
    String[] players = {"Kristian", "Synne", "Marianne"};
    TextButton.TextButtonStyle textButtonStyle;
    Stage stage;
    ClientConnection conn;

    @Override
    public void init() {
        conn = ClientConnection.getInstance();
        textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(4);
        textButtonStyle.font = font;
        stage = new Stage(new ScreenViewport()); //Set up a stage for the ui
    }

    public void getsFocus() {
        conn.getAvailablePlayers(new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                try {
                    JSONArray arr = result.getJSONArray("players");
                    String[] s = new String[arr.length()];
                    for (int i = 0; i < arr.length(); i++)
                        s[i] = arr.getString(i);
                    players = s;
                    addButtons();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void render() {
        Gdx.input.setInputProcessor(stage); //Start taking input from the ui

        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
        stage.draw(); //Draw the ui
    }

    @Override
    public void update() {

    }

    @Override
    public void dispose() {

    }

    private void addButtons() {
        for (int i = 0; i < players.length; i++) {
            String p = players[i];
            if (p.equals(GameState.getInstance().name))
                continue;

            TextButton button = new TextButton(p, textButtonStyle); //Set the button up
            button.setBounds(0, 0 + 100 * (i+1), 200, 75);
            stage.addActor(button); //Add the button to the stage to perform rendering and take input.

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ClientConnection conn = ClientConnection.getInstance();
                    conn.createGameWith("Kristian", p, new GameHTTPResponse() {
                        @Override
                        public void result(JSONObject result) {
                            try {
                                GameState.getInstance().gameId = result.getString("gameid");
                                GameState.getInstance().setRenderState(GameState.RenderState.GAME);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println("gameid");
                        }
                    });
                    System.out.println("creategamE" + p);
                }
            });
        }
    }
}
