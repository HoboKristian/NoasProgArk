package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kristianflatheimjensen on 23/03/2017.
 */

public class GameMenuState extends RenderUpdateState {
    Stage stage;
    TextButton findPlayerButton;
    TextButton helpButton;
    Image logo;

    String opponent = "ERROR";

    @Override
    public void init() {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(5);
        textButtonStyle.font = font;
        this.stage = new Stage(new ScreenViewport()); //Set up a stage for the ui

        this.logo = new Image(new Texture(Gdx.files.internal("mazerace.png")));
        this.logo.setBounds(850, 300, 500, 500);
        this.stage.addActor(this.logo);

        this.helpButton = new TextButton("Help", textButtonStyle); //Set the button up
        this.helpButton.setBounds(200, 000, 400, 75);
        this.stage.addActor(this.helpButton); //Add the button to the stage to perform rendering and take input.

        this.findPlayerButton = new TextButton("Find", textButtonStyle); //Set the button up
        this.findPlayerButton.setBounds(200, 100, 400, 75);
        this.stage.addActor(this.findPlayerButton); //Add the button to the stage to perform rendering and take input.

        this.helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("HELP");
            }
        });
        this.findPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameState.getInstance().setRenderState(GameState.RenderState.LIST);
            }
        });
    }

    public void getsFocus() {
        ClientConnection.getInstance().registerInvitedCallback(new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                try {
                    opponent = result.getString("opponent");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GameDialog.getInstance().invitedByOpponent = opponent;
                GameState.getInstance().setDialogToShow(GameState.DialogType.INVITED_BY_PLAYER);
            }
        });

        ClientConnection.getInstance().registerCancelInviteCallback(new GameHTTPResponse() {
            @Override
            public void result(JSONObject result) {
                if (GameState.getInstance().currentShowingDialog != null) {
                    GameState.getInstance().currentShowingDialog.hide();
                }
            }
        });
    }

    @Override
    public void render() {
        Gdx.input.setInputProcessor(stage); //Start taking input from the ui

        Gdx.gl.glClearColor(0.7f, 0.8f, 0.7f, 1);
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
}
