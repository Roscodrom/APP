package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen extends ScreenAdapter {
    final int GAME_WIDTH = 480;
    final int GAME_HEIGHT = 800;
    final float posX = GAME_WIDTH / 2f - 210;
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

        Image title = new Image(new Texture(Gdx.files.internal("images/text/title.png")));
        title.setSize(420, 100);
        title.setPosition((GAME_WIDTH / 2f - title.getWidth() / 2f), GAME_HEIGHT - title.getHeight() - 40);
        stage.addActor(title);

        Button individualButton = new TextButton("Partida Individual", skin);
        individualButton.setSize(600, GAME_WIDTH / 4f);
        individualButton.setPosition(posX, 400);
        individualButton.setTransform(true);
        individualButton.setScale(0.7f);
        stage.addActor(individualButton);
        individualButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showIndividualGame();
            }
        });

        Button multiplayerButton = new TextButton("Partida Multijugador", skin);
        multiplayerButton.setSize(600, GAME_WIDTH / 4f);
        multiplayerButton.setPosition(posX, 300);
        multiplayerButton.setTransform(true);
        multiplayerButton.setScale(0.7f);
        stage.addActor(multiplayerButton);
        multiplayerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (doesProfileFileExist()) {
                    ((Roscodrom) Gdx.app.getApplicationListener()).showMultiplayerGame();
                } else {
                    Dialog dialog = new Dialog("PER JUGAR MULTIJUGADOR", skin){
                        protected void result(Object object)
                        {
                            System.out.println("Option: " + object);
                            if (object.equals(1)) {
                                System.out.println("DONE");
                                ((Roscodrom) Gdx.app.getApplicationListener()).showProfile();
                            } else {
                                System.out.println("No DONE");
                            }
                        };
                    };
                    Label label = new Label(" Has de tenir un perfil. ", skin, "big");
                    label.setColor(Color.BLACK);
                    dialog.text(label);
                    Button doneButton = new TextButton("D'ACORD", skin);
                    dialog.button(doneButton, 1);
                    dialog.setMovable(false);
                    dialog.setScale(0.79f);

                    dialog.show(stage);
                }
            }
        });

        Button coliseumButton = new TextButton("Colisseu", skin);
        coliseumButton.setSize(600, GAME_WIDTH / 4f);
        coliseumButton.setPosition(posX, 200);
        coliseumButton.setTransform(true);
        coliseumButton.setScale(0.7f);
        stage.addActor(coliseumButton);
        coliseumButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showColiseum();
            }
        });

        Button optionsButton = new TextButton("Opcions", skin);
        optionsButton.setSize(285, GAME_WIDTH / 4f);
        optionsButton.setPosition(posX, 100);
        optionsButton.setTransform(true);
        optionsButton.setScale(0.7f);
        stage.addActor(optionsButton);
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showOptions();
            }
        });

        Button profileButton = new TextButton("Profile", skin);
        profileButton.setSize(285, GAME_WIDTH / 4f);
        profileButton.setPosition(posX + 220, 100);
        profileButton.setTransform(true);
        profileButton.setScale(0.7f);
        stage.addActor(profileButton);
        profileButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Roscodrom) Gdx.app.getApplicationListener()).showProfile();
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

    boolean doesProfileFileExist() {
        FileHandle file = Gdx.files.local("profile.txt");
        return file.exists();
    }
}