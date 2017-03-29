package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by kristianflatheimjensen on 23/03/2017.
 */

public class GameMenuState extends RenderUpdateState {
    TextButton.TextButtonStyle textButtonStyle;
    Stage stage;
    TextButton findPlayerButton;
    TextButton helpButton;
    TextButton lookingForPlayerButton;
    Image logo;


    @Override
    public void init() {

        textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(5);
        textButtonStyle.font = font;
        this.stage = new Stage(new ScreenViewport()); //Set up a stage for the ui

        this.logo = new Image(new Texture(Gdx.files.internal("mazerace.png")));
        this.logo.setBounds(850, 300, 500, 500);
        this.stage.addActor(this.logo);


        this.helpButton = new TextButton("Help", this.textButtonStyle); //Set the button up
        this.helpButton.setBounds(200, 200, 400, 150);
        this.stage.addActor(this.helpButton); //Add the button to the stage to perform rendering and take input.

        this.findPlayerButton = new TextButton("Find", this.textButtonStyle); //Set the button up
        this.findPlayerButton.setBounds(200, 500, 400, 150);
        this.stage.addActor(this.findPlayerButton); //Add the button to the stage to perform rendering and take input.

        this.lookingForPlayerButton = new TextButton("Looking", this.textButtonStyle); //Set the button up
        this.lookingForPlayerButton.setBounds(200, 800, 400, 150);
        this.stage.addActor(this.lookingForPlayerButton); //Add the button to the stage to perform rendering and take input.

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
        this.lookingForPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientConnection.getInstance().registerLookingForGame(GameState.getInstance().name);
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

    }

    @Override
    public void dispose() {

    }
}
