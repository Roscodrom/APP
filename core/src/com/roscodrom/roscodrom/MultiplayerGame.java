package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class MultiplayerGame extends ScreenAdapter {
    final int GAME_WIDTH = 480;
    final int GAME_HEIGHT = 800;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
    private Stage stage;
    Label timerLabel;
    Label userPoints;
    int gameCountdown = 60;
    Timer gameTimer = new Timer();
    Game game = new Game();
    Sound sound;
    ScrollPane scrollPane;
    private VerticalGroup verticalGroup;
    WebSocket socket;
    String[] rosco_letters = {};

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        timerLabel = new Label("00''", skin, "big");
        userPoints = new Label("0", skin, "big");

        String address = "roscodrom2.ieti.site";
        int port = 443;
        //String address = "192.168.16.82";
        //int port = 80;

        waitingRoom();

        //socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(address, port));
        socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl(address, port));
        socket.setSendGracefully(false);
        socket.addListener(new MyWSListener());
        socket.connect();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        skin.dispose();
        stage.dispose();

    }


    private void waitingRoom() {
        Label label1 = new Label("Estas a la\nsala d'espera!", skin, "big");
        label1.setWidth(GAME_WIDTH);
        label1.setAlignment(Align.center);
        label1.setPosition(0, 500);
        stage.addActor(label1);

        Label label2 = new Label("Temps fins la\nseguent partida:", skin, "big");
        label2.setWidth(GAME_WIDTH);
        label2.setAlignment(Align.center);
        label2.setPosition(0, 180);
        stage.addActor(label2);

        timerLabel.setPosition(GAME_WIDTH / 2f - timerLabel.getWidth() / 2, 100);
        stage.addActor(timerLabel);
    }

    private void multiplayerGame() {
        verticalGroup = new VerticalGroup();
        verticalGroup.align(Align.bottomLeft);

        scrollPane = new ScrollPane(verticalGroup);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setSize(450, 285);
        scrollPane.setPosition(GAME_WIDTH / 2f - 220, 495);

        stage.addActor(scrollPane);


        TextField rectangle = new TextField("",skin);
        rectangle.setDisabled(true);
        rectangle.setPosition(GAME_WIDTH / 2f - 220, 430);
        rectangle.setSize(rectangle.getWidth()+225, rectangle.getHeight()+20);
        stage.addActor(rectangle);

        game.wordLabel = new Label(game.word, skin, "big");
        game.wordLabel.setPosition(GAME_WIDTH / 2f - 210, 460);
        game.wordLabel.setColor(Color.BLACK);
        game.wordLabel.setFontScale(0.55f);
        stage.addActor(game.wordLabel);

        userPoints.setPosition(390, 340);
        userPoints.setAlignment(Align.right);
        stage.addActor(userPoints);

        Label timerLabel = new Label("", skin, "big");
        timerLabel.setPosition(GAME_WIDTH / 2f - 210, 340);
        stage.addActor(timerLabel);

        gameTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {

                if (gameCountdown > -1) {
                    timerLabel.setText(gameCountdown+"''");
                    if (gameCountdown % 10 == 0 && gameCountdown != 10) {
                        timerLabel.setColor(Color.ORANGE);
                    } else if (gameCountdown %2 != 0 && gameCountdown < 10) {
                        timerLabel.setColor(Color.RED);
                    } else {
                        timerLabel.setColor(Color.WHITE);
                    }
                } else {
                    gameTimer.stop();
                    ((Roscodrom) Gdx.app.getApplicationListener()).showGameOver(Integer.parseInt(String.valueOf(userPoints.getText())));
                }
                gameCountdown--;
            }
        }, 0, 1);

        Image sendButton = new Image(new Texture(Gdx.files.internal("images/send.png")));
        sendButton.setSize(60,60);
        sendButton.setPosition(GAME_WIDTH / 2f + 160, 430);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.word.length() >= 3) {
                    System.out.println("Validating: " + game.word);
                    socket.send("{\"type\":\"CHECK_WORD\",\"data\":{\"word\":\""+game.word+"\"}}");
                }
                game.word = "";
                game.wordLabel.setText("");
            }
        });
        stage.addActor(sendButton);


        Array<Actor> rosco = game.generateRosco(rosco_letters);
        for (Actor actor : rosco) {
            stage.addActor(actor);
        }

        Label delLabel = new Label(" < ", skin, "big");
        delLabel.setPosition(GAME_WIDTH/2f-delLabel.getWidth()/2f, 150);
        delLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (game.word.length() >= 1) {
                    game.word = game.word.substring(0, game.word.length() - 1);
                    game.wordLabel.setText(game.word);
                }
            }
        });
        stage.addActor(delLabel);
    }

    class MyWSListener implements WebSocketListener {

        @Override
        public boolean onOpen(WebSocket webSocket) {
            System.out.println("Opening...");
            return true;
        }

        @Override
        public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
            System.out.println("Closing...");
            return false;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, String packet) {
            System.out.println("Message:"+packet);
            try {
                JSONObject resp = new JSONObject(packet);

                switch (resp.getString("type")) {
                    case "HANDSHAKE":
                        String apikey = readFile("api_token.txt");
                        String nickname = "";
                        try {
                            JSONObject profile = new JSONObject(readFile("profile.txt"));
                            nickname = profile.getString("name");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        socket.send("{\"type\":\"HANDSHAKE\", \"data\":{\"id\":\"" + apikey + "\",\"nickname\":\"" + nickname + "\", \"client\":\"android\"}}");
                        break;
                    case "TIEMPO_PARA_INICIO":
                        JSONObject tiempo = resp.getJSONObject("data");
                        int timeLeft = tiempo.getInt("tempsRestant");
                        boolean inGame = tiempo.getBoolean("enPartida");
                        int timeInSeconds = timeLeft / 1000 + (inGame ? 60 : 0);
                        Gdx.app.postRunnable(() -> {
                            timerLabel.setText(timeInSeconds + "''");
                        });
                        break;
                    case "GAME_START":
                        stage.clear();
                        JSONObject roscoMessage = resp.getJSONObject("data");
                        JSONArray JSONArrayRosco = roscoMessage.getJSONArray("roscoLetters");
                        rosco_letters = jsonArrayToStringArray(JSONArrayRosco);
                        multiplayerGame();
                    case "WORD_POINTS":
                        JSONObject data = resp.getJSONObject("data");
                        String word = data.getString("word");
                        int points = data.getInt("points");

                        if (points > 0) {
                            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/correct.mp3"));
                            game.usedWords.add(game.word);
                            Label userLabel = new Label(word + " x" + points, skin);
                            userLabel.setColor(Color.BLUE);
                            verticalGroup.addActor(userLabel);
                            userPoints.setText(String.valueOf(Integer.parseInt(String.valueOf(userPoints.getText())) + points));
                        } else {
                            sound = Gdx.audio.newSound(Gdx.files.internal("sounds/wrong.mp3"));
                        }
                        sound.play();
                    case "NEW_WORD":
                        JSONObject dataRival = resp.getJSONObject("data");
                        String otherWord = dataRival.getString("word");
                        int otherPoints = dataRival.getInt("points");

                        if (otherPoints > 0) {
                            game.usedWords.add(game.word);
                            Label wordLabel = new Label(otherWord + " x" + otherPoints, skin);
                            wordLabel.setColor(Color.RED);
                            verticalGroup.addActor(wordLabel);
                        }

                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        @Override
        public boolean onMessage(WebSocket webSocket, byte[] packet) {
            System.out.println("Message:"+ Arrays.toString(packet));
            return true;
        }

        @Override
        public boolean onError(WebSocket webSocket, Throwable error) {
            System.out.println("ERROR:"+error.toString());
            return false;
        }

        public String readFile(String filepath) {
            FileHandle file = Gdx.files.local(filepath);

            try (BufferedReader reader = new BufferedReader(file.reader())) {
                return reader.readLine();
            } catch (IOException e) {
                System.err.println("Error reading file: " + filepath);
                e.printStackTrace();
                return null;
            }
        }

        public String[] jsonArrayToStringArray(JSONArray jsonArray) {
            int length = jsonArray.length();
            String[] stringArray = new String[length];

            for (int i = 0; i < length; i++) {
                stringArray[i] = jsonArray.getString(i);
            }

            return stringArray;
        }
    }
}
