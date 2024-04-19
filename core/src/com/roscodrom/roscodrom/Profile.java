package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Profile extends ScreenAdapter {
    private final int GAME_WIDTH = 480;
    private final int GAME_HEIGHT = 800;
    private float posX = GAME_WIDTH / 2f - 210;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;

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
                ((RoscodromGame) Gdx.app.getApplicationListener()).showMainMenuScreen();
            }
        });

        Label nicknameLabel = new Label("Nickname:",skin, "big");
        nicknameLabel.setPosition(posX, 600);
        nicknameLabel.setFontScale(0.65f);
        stage.addActor(nicknameLabel);

        TextField nicknameTF = new TextField("", skin);
        nicknameTF.setSize(400, nicknameTF.getHeight()*1.5f);
        nicknameTF.setPosition(posX, 550);
        nicknameTF.setScale(0.5f);
        stage.addActor(nicknameTF);

        Label emailLabel = new Label("Email:",skin, "big");
        emailLabel.setPosition(posX, 470);
        emailLabel.setFontScale(0.65f);
        stage.addActor(emailLabel);

        TextField emailTF = new TextField("", skin);
        emailTF.setSize(400, emailTF.getHeight()*1.5f);
        emailTF.setPosition(posX, 420);
        emailTF.setScale(0.5f);
        stage.addActor(emailTF);

        Label telephoneLabel = new Label("Numero de telefon:",skin, "big");
        telephoneLabel.setPosition(posX, 340);
        telephoneLabel.setFontScale(0.65f);
        stage.addActor(telephoneLabel);

        TextField telephoneTF = new TextField("", skin);
        telephoneTF.setSize(400, telephoneTF.getHeight()*1.5f);
        telephoneTF.setPosition(posX, 290);
        telephoneTF.setScale(0.5f);
        stage.addActor(telephoneTF);

        // IMAGEN DE PERFIL
        Label profileLabel = new Label("Imatge de perfil:",skin, "big");
        profileLabel.setPosition(posX, 210);
        profileLabel.setFontScale(0.65f);
        stage.addActor(profileLabel);

        Array<String> items = new Array<>();
        items.add("Image 1");
        items.add("Image 2");
        items.add("Image 3");

        SelectBox<String> profileImages = new SelectBox<>(skin);
        profileImages.setSize(150,profileImages.getHeight());
        profileImages.setItems(items);
        profileImages.setPosition(posX, 180);
        stage.addActor(profileImages);

        Button doneButton = new TextButton("DONE", skin);
        doneButton.setSize(285, GAME_WIDTH / 4f);
        doneButton.setPosition(posX*4.5f, 40);
        doneButton.setTransform(true);
        doneButton.setScale(0.7f);
        stage.addActor(doneButton);
        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("DONE");
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
