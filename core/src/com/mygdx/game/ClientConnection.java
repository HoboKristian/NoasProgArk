package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Dictionary;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by kristianflatheimjensen on 22/03/2017.
 */

interface GameHTTPResponse {
    void result(JSONObject result);
}

public class ClientConnection {
    private static final ClientConnection instance = new ClientConnection();
    private Array<Net.HttpRequest> requests = new Array<Net.HttpRequest>();

    public static ClientConnection getInstance(){
        return instance;
    }

    private String base_uri;
    Socket socket;

    private ClientConnection() {
        try {
            socket = IO.socket("http://localhost:5005");
            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        String base_url = "10.0.2.2";
        String base_port = "5005";
        String base_api_path = "api/";
        this.base_uri = String.format("http://%s:%s/%s", base_url, base_port, base_api_path);
    }

    public void getAvailablePlayers(GameHTTPResponse response) {
        System.out.println("connect");
        socket.emit("players", new JSONObject());
        socket.on("players", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                System.out.println("players");
                try {
                    response.result(obj);
                    System.out.println("playing against: " + obj.getJSONArray("players").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void registerLookingForGame(String name, GameHTTPResponse reponse) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", GameState.getInstance().name);
            socket.emit("lookingforplayer", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on("opponentfound", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                reponse.result(obj);
            }
        });
    }

    public void createGameWith(String name, String opponent, GameHTTPResponse response) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", GameState.getInstance().name);
            obj.put("player2", opponent);
            socket.emit("creategame", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on("creategame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {}
        });
    }

    public void getGame(String gameId, GameHTTPResponse response) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("gameid", gameId);
            socket.emit("getgame", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on("getgame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }

    public void sendPos(float xpos, float ypos, String gameId, String name) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("player", name);
            obj.put("gameid", gameId);
            obj.put("xpos", xpos);
            obj.put("ypos", ypos);
            socket.emit("setpos", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            }
        });
    }
}