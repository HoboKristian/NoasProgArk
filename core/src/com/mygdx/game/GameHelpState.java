package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kristianflatheimjensen on 24/03/2017.
 */

public class GameHelpState extends RenderUpdateState {
    private Stage stage;
    private List<Texture> helpImages = new ArrayList<>();
    private int counter = 0;
    private boolean down = false;

    private SpriteBatch batch;

    @Override
    public void init() {
        stage = new Stage(new ScreenViewport()); //Set up a stage for the ui
        batch = new SpriteBatch();

        helpImages.add(new Texture(Gdx.files.internal("Player/help1.png")));
        helpImages.add(new Texture(Gdx.files.internal("Player/help2.png")));
        helpImages.add(new Texture(Gdx.files.internal("Player/help3.png")));
    }

    public void getsFocus() {
        counter = 0;
    }

    @Override
    public void render() {
        Gdx.input.setInputProcessor(stage);

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 0.2f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(helpImages.get(counter), 0, 0, this.stage.getViewport().getScreenWidth(), this.stage.getViewport().getScreenHeight());
        batch.end();
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void update() {
        Dialog gameStateDialog = GameState.getInstance().getDialogToShow();
        if (gameStateDialog != null) {
            stage.addActor(gameStateDialog);
            gameStateDialog.show(stage).setBounds(10, 10, this.stage.getViewport().getScreenWidth() - 20, this.stage.getViewport().getScreenHeight() - 20);
        }

        if (Gdx.input.isTouched()) {
            if (!down) {
                counter++;
                if (counter >= helpImages.size())
                    GameState.getInstance().setRenderState(GameState.RenderState.MENU);
            }
            down = true;
        } else {
            down = false;
        }
    }

    @Override
    public void dispose() {

    }
}
