package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kristianflatheimjensen on 23/04/2017.
 */

public class GameDialog {
    private static final GameDialog instance = new GameDialog();

    public static GameDialog getInstance() {
        return instance;
    }

    private GameDialog() {
    }

    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;

    public String invitedOpponent = "ERROR";
    public String invitedByOpponent = "ERROR";

    public void init() {
        textButtonStyle = new TextButton.TextButtonStyle();
        font = new BitmapFont();
        font.getData().setScale(5);
        textButtonStyle.font = font;
        skin = new Skin(Gdx.files.internal("Skin/uiskin.json"));

    }

    public Dialog getInvitedByPlayerDialog() {
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        TextButton acceptButton = new TextButton("Accept", textButtonStyle); //Set the button up
        TextButton declineButton = new TextButton("Decline", textButtonStyle); //Set the button up

        final Dialog dialog = new Dialog("Invited to game", skin);

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        acceptButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientConnection.getInstance().acceptInvite(GameState.getInstance().name, invitedByOpponent);
                GameState.getInstance().setDialogToShow(null);
                dialog.hide();
            }
        });

        declineButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientConnection.getInstance().declineInvite(GameState.getInstance().name, invitedByOpponent);
                GameState.getInstance().setDialogToShow(null);
                dialog.hide();
            }
        });

        float btnSize = 80f;
        Table t = new Table();
        Label label = new Label(String.format("You have been invited to play against: %s", invitedByOpponent), skin);
        label.setFontScale(2.0f);
        t.add(label);
        t.row();
        t.add(acceptButton).width(btnSize).height(btnSize);
        t.add(declineButton).width(btnSize).height(btnSize);

        dialog.getButtonTable().add(t).center().padBottom(btnSize);
        dialog.setName("Invite Dialog");

        return dialog;
    }

    public Dialog getInvitedPlayerDialog() {
        textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);
        textButtonStyle.font = font;

        TextButton cancelButton = new TextButton("Cancel", textButtonStyle); //Set the button up

        Skin skin = new Skin(Gdx.files.internal("Skin/uiskin.json"));

        final Dialog dialog = new Dialog("Invited to game", skin);

        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientConnection.getInstance().cancelInvite(GameState.getInstance().name, invitedOpponent);
                GameState.getInstance().setDialogToShow(null);
                dialog.hide();
            }
        });

        float btnSize = 80f;
        Table t = new Table();
        Label label = new Label(String.format("You have invited %s to play against you", invitedOpponent), skin);
        label.setFontScale(2.0f);
        t.add(label);
        t.row();
        t.add(cancelButton).width(btnSize).height(btnSize);

        dialog.getButtonTable().add(t).center().padBottom(btnSize);

        dialog.setName("Invite Dialog");

        return dialog;
    }
}