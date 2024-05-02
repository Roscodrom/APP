package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import java.io.FileWriter;

public class Profile extends ScreenAdapter {
    private final int GAME_WIDTH = 480;
    private final int GAME_HEIGHT = 800;
    private float posX = GAME_WIDTH / 2f - 210;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Sprite profileSprite;
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
                ((Roscodrom) Gdx.app.getApplicationListener()).showMainMenuScreen();
            }
        });

        Label title = new Label("PERFIL",skin, "big");
        title.setPosition(GAME_WIDTH / 2f - title.getWidth()/2+40, 720);
        title.setFontScale(0.9f);
        stage.addActor(title);

        Label nicknameLabel = new Label("Nickname:",skin, "big");
        nicknameLabel.setPosition(posX, 650);
        nicknameLabel.setFontScale(0.65f);
        stage.addActor(nicknameLabel);

        TextField nicknameTF = new TextField("", skin);
        nicknameTF.setSize(400, nicknameTF.getHeight()*1.5f);
        nicknameTF.setPosition(posX, 600);
        stage.addActor(nicknameTF);

        Label emailLabel = new Label("Email:",skin, "big");
        emailLabel.setPosition(posX, 520);
        emailLabel.setFontScale(0.65f);
        stage.addActor(emailLabel);

        TextField emailTF = new TextField("", skin);
        emailTF.setSize(400, emailTF.getHeight()*1.5f);
        emailTF.setPosition(posX, 470);
        stage.addActor(emailTF);

        Label telephoneLabel = new Label("Numero de telefon:",skin, "big");
        telephoneLabel.setPosition(posX, 390);
        telephoneLabel.setFontScale(0.65f);
        stage.addActor(telephoneLabel);

        TextField telephoneTF = new TextField("", skin);
        telephoneTF.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return  c >= '0' && c <= '9';
            }
        });
        telephoneTF.setSize(400, telephoneTF.getHeight()*1.5f);
        telephoneTF.setPosition(posX, 340);
        stage.addActor(telephoneTF);

        // IMAGEN DE PERFIL
        Label profileLabel = new Label("Imatge de perfil:",skin, "big");
        profileLabel.setPosition(posX, 260);
        profileLabel.setFontScale(0.65f);
        stage.addActor(profileLabel);

        Array<String> items = new Array<>();
        items.add("Image 1");
        items.add("Image 2");
        items.add("Image 3");
        items.add("Image 4");

        SelectBox<String> profileImages = new SelectBox<>(skin);
        profileImages.setSize(150,profileImages.getHeight());
        profileImages.setItems(items);
        profileImages.setPosition(posX, 200);
        stage.addActor(profileImages);

        profileSprite = new Sprite(new Texture(Gdx.files.internal("images/profile/profile1.jpg")));
        profileSprite.setPosition(posX + 200, 170);
        profileSprite.setSize(100, 100);
        profileImages.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                int selectedItem = profileImages.getSelectedIndex()+1;
                String profilePath = getProfilePath(selectedItem);

                // Change the profile sprite based on the selected item
                profileSprite.setTexture(new Texture(Gdx.files.internal(profilePath)));

            }
        });

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
                String nickname = nicknameTF.getText();
                String email = emailTF.getText();
                String telephone = telephoneTF.getText();
                String imagePath = getProfilePath(profileImages.getSelectedIndex()+1);
                String b64Image = imageToBase64(imagePath);

                if (nickname.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
                    System.out.println("NO");
                } else {
                    System.out.println("Sending");

                    sendProfile(nickname, email, telephone, b64Image);
                }

            }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        profileSprite.draw(batch);
        batch.end();

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

    public String getProfilePath(int index) {
        return "images/profile/profile" + index + ".jpg";
    }

    @SuppressWarnings("NewApi")
    public String imageToBase64(String path) {
        File file = new File("./assets/"+path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            // Read the image file into a byte array
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);

            // Encode the byte array to Base64
            return Base64.getEncoder().encodeToString(imageData);
        } catch (IOException e) {
            System.err.println("Error reading the image file: " + e.getMessage());
            return null;
        }
    }

    public void sendProfile(String nickname, String email, String telephone, String image) {

        String ipServer = "https://roscodrom2.ieti.site/api/user/register";
        String json =
            "{\"name\":\"" + nickname + "\"," +
            "\"email\":\"" + email + "\"," +
            "\"phone_number\":\"" + telephone + "\"," +
            "\"avatar\":\"" + image + "\"}";

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(ipServer)
                .header("Content-Type", "application/json")
                .content(json)
                .timeout(10000)
                .build();
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String responseString = httpResponse.getResultAsString();
                System.out.println("OK: " + responseString);
                if (httpResponse.getStatus().getStatusCode()==201) {
                    saveToFile(json, "profile.txt");

                    if (responseString!= null &&!responseString.isEmpty()) {
                        Json jsonParser = new Json();
                        JsonValue response = jsonParser.fromJson(null, responseString);
                        if (response!= null) {
                            String apiKey = response.get("data").getString("api_key");
                            saveToFile(apiKey, "api_token.txt");
                            ((Roscodrom) Gdx.app.getApplicationListener()).showMainMenuScreen();
                        } else {
                            System.out.println("Error parsing JSON response");
                        }
                    } else {
                        System.out.println("Empty response from server");
                    }
                } else {
                    System.out.println("Error: " + httpResponse.getStatus().getStatusCode());
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("ERROR: " + t.toString());
            }

            @Override
            public void cancelled() {
                System.out.println("Canceled");
            }
        };

        System.out.println("Sending");
        Gdx.net.sendHttpRequest(httpRequest, listener);
    }

    public void saveToFile(String content, String filePath) {
        FileHandle file = Gdx.files.local(filePath);
        try {
            file.writeString(content, false);
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
