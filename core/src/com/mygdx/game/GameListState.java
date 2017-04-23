package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
        Dialog gameStateDialog = GameState.getInstance().getDialogToShow();
        if (gameStateDialog != null) {
            stage.addActor(gameStateDialog);
            gameStateDialog.show(stage).setBounds(10, 10, this.stage.getViewport().getScreenWidth() - 20, this.stage.getViewport().getScreenHeight() - 20);
        }
    }

    @Override
    public void dispose() {

    }

    private void addButtons() {
        for (int i = 0; i < players.length; i++) {
            String opponent = players[i];
            if (opponent.equals(GameState.getInstance().name))
                continue;

            TextButton button = new TextButton(opponent, textButtonStyle); //Set the button up
            button.setBounds(0, 100 * (i+1), 200, 75);
            stage.addActor(button); //Add the button to the stage to perform rendering and take input.

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameDialog.getInstance().invitedOpponent = opponent;
                    GameState.getInstance().setDialogToShow(GameState.DialogType.INVITED_PLAYER);

                    ClientConnection conn = ClientConnection.getInstance();
                    conn.invitePlayer(GameState.getInstance().name, opponent, new GameHTTPResponse() {
                        @Override
                        public void result(JSONObject result) {
                            try {
                                boolean answer = result.getBoolean("answer");
                                if (answer) {
                                    System.out.println("cerategmame - " + opponent);
                                    conn.createGameWith(GameState.getInstance().name, opponent, new GameHTTPResponse() {
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
                                } else {
                                    if (GameState.getInstance().currentShowingDialog != null) {
                                        GameState.getInstance().currentShowingDialog.hide();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }
}
