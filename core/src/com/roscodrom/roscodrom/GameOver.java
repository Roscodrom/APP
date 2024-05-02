package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOver extends ScreenAdapter {
    private final int GAME_WIDTH = 480;
    private final int GAME_HEIGHT = 800;
    private float posX = GAME_WIDTH / 2f - 210;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;
    private int points;

    public GameOver(int points) {
        this.points = points;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

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

        Label title = new Label("Game Over",skin, "big");
        title.setPosition(GAME_WIDTH / 2f - title.getWidth()/2+40, 720);
        title.setFontScale(0.9f);
        stage.addActor(title);

        Label points1 = new Label("HAS FET",skin, "big");
        points1.setAlignment(Align.center);
        points1.setWidth(GAME_WIDTH);
        points1.setPosition(0, GAME_HEIGHT / 2f - title.getHeight()/2+75);
        stage.addActor(points1);

        Label points2 = new Label(String.valueOf(points),skin, "big");
        points2.setAlignment(Align.center);
        points2.setWidth(GAME_WIDTH);
        points2.setPosition(0, GAME_HEIGHT / 2f - title.getHeight()/2);
        stage.addActor(points2);

        Label points3 = new Label("PUNTS",skin, "big");
        points3.setAlignment(Align.center);
        points3.setWidth(GAME_WIDTH);
        points3.setPosition(0, GAME_HEIGHT / 2f - title.getHeight()/2-75);
        stage.addActor(points3);

        Button homeButton = new TextButton("INICI", skin);
        //homeButton.setScale(0.7f);
        homeButton.setPosition(GAME_WIDTH / 2f - homeButton.getWidth() / 2f, 100);
        homeButton.setTransform(true);
        stage.addActor(homeButton);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showMainMenuScreen();
            }
        });

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
