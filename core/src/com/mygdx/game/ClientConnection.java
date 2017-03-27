package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.Dictionary;

/**
 * Created by kristianflatheimjensen on 22/03/2017.
 */

interface GameHTTPResponse {
    void result(String result);
}

public class ClientConnection {
    private static final ClientConnection instance = new ClientConnection();
    private Array<Net.HttpRequest> requests = new Array<Net.HttpRequest>();

    public static ClientConnection getInstance(){
        return instance;
    }

    private String base_uri;
    private ClientConnection() {
        String base_url = "10.0.2.2";
        String base_port = "5005";
        String base_api_path = "api/";
        this.base_uri = String.format("http://%s:%s/%s", base_url, base_port, base_api_path);
    }

    public void getAvailablePlayers(GameHTTPResponse response) {
        String URL = this.base_uri + "player";
        this.sendHTTPRequest("", URL, response);
    }

    public void registerLookingForGame(String name) {
        String content = String.format("name=%s", name);
        String URL = this.base_uri + "register";
        this.sendHTTPRequest(content, URL, new GameHTTPResponse() {
            @Override
            public void result(String result) {
                idleWaitForOpponent(name);
            }
        });
    }

    public void createGameWith(String name, String opponent) {
        String content = String.format("player1=%s&player2=%s", name, opponent);
        String URL = this.base_uri + "creategame";
        this.sendHTTPRequest(content, URL, new GameHTTPResponse() {
            @Override
            public void result(String result) {
                JsonReader json = new JsonReader();
                JsonValue base = json.parse(result);

                GameState gameState = GameState.getInstance();
                gameState.gameId = base.getString("gameid");
                gameState.setRenderState(GameState.RenderState.GAME);
            }
        });
    }

    public void getGame(String id) {
        String URL = this.base_uri + "game/" + id;
        this.sendHTTPRequest("", URL, new GameHTTPResponse() {
            @Override
            public void result(String result) {
                JsonReader json = new JsonReader();
                JsonValue base = json.parse(result);

                JsonValue player1 = base.get("1");
                float xpos = player1.getFloat("xpos");
                float ypos = player1.getFloat("ypos");
                GameState.getInstance().opponentPos = new Vector2(xpos, ypos);
            }
        });
    }

    public void sendPos(float xpos, float ypos, String gameId, String name) {
        String content = String.format("xpos=%f&ypos=%f", xpos, ypos);
        String URL = String.format("%s%s/%s/%s", this.base_uri, "game/", gameId, name);
        this.sendHTTPRequest(content, URL, new GameHTTPResponse() {
            @Override
            public void result(String result) {
                Json json = new Json();
                json.prettyPrint(result);
            }
        });
    }

    private void idleWaitForOpponent(String name) {
        String content = String.format("name=", name);
        String URL = String.format("%s%s", this.base_uri, "opponent");
        this.sendHTTPRequest(content, URL, new GameHTTPResponse() {
            @Override
            public void result(String result) {
                JsonReader json = new JsonReader();
                JsonValue base = json.parse(result);
                boolean found = base.get("found").asBoolean();
                if (!found)
                    idleWaitForOpponent(name);
                else {
                    String gameid = base.get("gameid").asString();

                    GameState gameState = GameState.getInstance();
                    gameState.gameId = base.getString("gameid");
                    gameState.setRenderState(GameState.RenderState.GAME);
                }
            }
        });
    }

    private void sendHTTPRequest(String content, String URL, GameHTTPResponse response) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(URL)
                .content(content)
                .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void cancelled() {}
            public void failed(java.lang.Throwable t) {}
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                response.result(httpResponse.getResultAsString());
            }
        });
    }
}

class GameResponse {
    public OrderedMap<String, PlayerResponse> players;
}

class PlayerResponse {
    public int x_pos;
    public int y_pos;
}
