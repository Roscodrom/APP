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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

public class MultiplayerGame extends ScreenAdapter {
    private final int GAME_WIDTH = 480;
    private final int GAME_HEIGHT = 800;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
    private Stage stage;
    Label timerLabel;
    int gameCountdown = 60;
    Timer gameTimer = new Timer();
    Game game = new Game();
    private Sound sound;
    ScrollPane scrollPane;
    private VerticalGroup verticalGroup;
    WebSocket socket;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        timerLabel = new Label("00''", skin, "big");

        //String address = "roscodrom2.ieti.site";
        //int port = 443;
        String address = "192.168.18.148";
        int port = 80;

        waitingRoom();

        socket = WebSockets.newSocket(WebSockets.toWebSocketUrl(address, port));
        //socket = WebSockets.newSocket(WebSockets.toSecureWebSocketUrl(address, port));
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
        ImageButton backButton = new ImageButton(skin);
        backButton.setPosition(5, GAME_HEIGHT - (backButton.getHeight()-30));
        backButton.setSize(backButton.getWidth()/2.1f,backButton.getHeight());
        backButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/back_icon.png"))));
        backButton.setTransform(true);
        backButton.setScale(0.65f);
        stage.addActor(backButton);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showMainMenuScreen();
            }
        });

        Label title = new Label("WAITING ROOM",skin, "big");
        title.setPosition(GAME_WIDTH / 2f - title.getWidth()/2+40, 720);
        title.setFontScale(0.9f);
        stage.addActor(title);

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


        Label userPoints = new Label("0", skin, "big");
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
                    boolean isCorrect = game.checkUserWord(game.word, game.wordList);
                    System.out.println(isCorrect);
                    if (isCorrect) {
                        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/correct.mp3"));
                        game.usedWords.add(game.word);
                        verticalGroup.addActor(new Label(game.word + " x" + game.calculateWordPoints(game.word), skin));
                        userPoints.setText(String.valueOf(Integer.parseInt(String.valueOf(userPoints.getText())) + (game.calculateWordPoints(game.word))));
                    } else {
                        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/wrong.mp3"));
                    }
                    sound.play();
                }
                game.word = "";
                game.wordLabel.setText("");
            }
        });
        stage.addActor(sendButton);


        Array<Actor> rosco = game.generateRosco();
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
                JSONObject data = new JSONObject(packet);

                switch (data.getString("type")) {
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
                        JSONObject tiempo = data.getJSONObject("data");
                        int timeLeft = tiempo.getInt("tempsRestant");
                        boolean inGame = tiempo.getBoolean("enPartida");
                        int timeInSeconds = timeLeft / 1000 + (inGame ? 60 : 0);
                        Gdx.app.postRunnable(() -> {
                            timerLabel.setText(timeInSeconds + "''");  // Ensure UI updates are made on the main thread
                        });
                        break;
                    case "GAME_START":
                        stage.clear();
                        multiplayerGame();
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
                return reader.readLine();  // Reads the first line
            } catch (IOException e) {
                // Handle exception
                System.err.println("Error reading file: " + filepath);
                e.printStackTrace();
                return null;  // Return null if an error occurs
            }
        }
    }
}
