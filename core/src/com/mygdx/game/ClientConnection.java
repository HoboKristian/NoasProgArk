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
            //socket = IO.socket("http://localhost:5005");
            //socket = IO.socket("http://192.168.1.218:5005");
            //socket = IO.socket("http://10.0.2.2:5005");
            socket = IO.socket("http://185.14.185.30:5005");

            socket.connect();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        String base_url = "192.168.1.218";
        //String base_url = "10.0.2.2";
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

    public void registerName(String name, GameHTTPResponse reponse) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            socket.emit("register", obj);
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

    public void invitePlayer(String name, String opponent, GameHTTPResponse response) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", name);
            obj.put("player2", opponent);
            socket.emit("inviteplayer", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.on("answerinvite", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }

    public void cancelInvite(String name, String opponent) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", name);
            obj.put("player2", opponent);
            socket.emit("cancelinvite", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void acceptInvite(String name, String opponent) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", name);
            obj.put("player2", opponent);
            socket.emit("acceptinvite", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void declineInvite(String name, String opponent) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", name);
            obj.put("player2", opponent);
            socket.emit("declineinvite", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createGameWith(String name, String opponent, GameHTTPResponse response) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("player1", name);
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
    }

    public void sendBuff(String gameId, String target, String buff, float duration) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", target);
            obj.put("gameid", gameId);
            obj.put("buff", buff);
            obj.put("duration", duration);
            socket.emit("buff", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendWinner(String gameId, String name) {
        System.out.println(String.format("%s %s %s", gameId, name, GameState.getInstance().name));
        JSONObject obj = new JSONObject();
        try {
            obj.put("winner", name);
            obj.put("gameid", gameId);
            socket.emit("finishedgame", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendPowerup(String gameId, String target, String type) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", target);
            obj.put("type", type);
            socket.emit("sendpowerup", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.sendBuff(gameId, target, type, 5000);
    }

    public void registerWinnerCallback(GameHTTPResponse response) {
        socket.on("finishedgame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }

    public void registerPowerupCallback(GameHTTPResponse response) {
        socket.on("powerup", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }

    public void registerInvitedCallback(GameHTTPResponse response) {
        socket.on("invited", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }

    public void registerCancelInviteCallback(GameHTTPResponse response) {
        socket.on("cancelinvite", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                response.result(obj);
            }
        });
    }
}