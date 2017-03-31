package com.mygdx.game;

import com.badlogic.gdx.Input;

import org.json.JSONObject;

/**
 * Created by kristianflatheimjensen on 31/03/2017.
 */

interface NameInputFieldListener {
    void result(String text, boolean cancelled);
}

public class NameInputField  implements Input.TextInputListener {
    private NameInputFieldListener listener;
    public NameInputField(NameInputFieldListener listener) {
        this.listener = listener;
    }

    @Override
    public void input (String text) {
        this.listener.result(text, false);
    }

    @Override
    public void canceled () {
        this.listener.result("", true);
    }
}