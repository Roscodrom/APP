package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Map;

import sun.jvm.hotspot.ui.tree.SimpleTreeNode;

public class IndividualGame extends ScreenAdapter {
    private final int GAME_WIDTH = 480;
    private final int GAME_HEIGHT = 800;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    Game game = new Game();
    private Sound sound;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        ImageButton backButton = new ImageButton(skin);
        backButton.setPosition(5,  5);
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

        Label usedWordsLabel = new Label(game.getLastWords(), skin, "big");
        usedWordsLabel.setSize(GAME_WIDTH-40, usedWordsLabel.getHeight());
        usedWordsLabel.setPosition(GAME_WIDTH / 2f - 220, 490);
        usedWordsLabel.setFontScale(0.9f);
        usedWordsLabel.setWrap(true);
        stage.addActor(usedWordsLabel);


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

        Image sendButton = new Image(new Texture(Gdx.files.internal("images/send.png")));
        sendButton.setSize(60,60);
        sendButton.setPosition(GAME_WIDTH / 2f + 160, 430);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Validating: " + game.word);
                boolean isCorrect = game.checkUserWord(game.word, game.wordList);
                System.out.println(isCorrect);
                if (isCorrect) {
                    sound = Gdx.audio.newSound(Gdx.files.internal("sounds/correct.mp3"));
                    game.usedWords.add(game.word);
                    usedWordsLabel.setText(game.getLastWords());
                    userPoints.setText(String.valueOf(Integer.valueOf(String.valueOf(userPoints.getText()))+(game.calculateWordPoints(game.word))));
                } else {
                    sound = Gdx.audio.newSound(Gdx.files.internal("sounds/wrong.mp3"));
                }
                sound.play();
                game.word = "";
                game.wordLabel.setText("");
            }
        });
        stage.addActor(sendButton);


        Array<Actor> rosco = game.generateRosco();
        for (Actor actor : rosco) {
            stage.addActor(actor);
        }

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
}
