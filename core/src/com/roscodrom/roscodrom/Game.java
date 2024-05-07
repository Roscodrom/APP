package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Game {
    final int GAME_WIDTH = 480;
    private final Skin skin= new Skin(Gdx.files.internal("skin/glassy-ui.json"));
    public String word = "";
    public Label wordLabel;
    private final String[] vocales = {"A", "E", "I", "O", "U"};
    private final String[] consonantes = {"B", "C", "Ç", "D", "F", "G", "H", "J", "K", "L", "L·L", "M",
                                    "N", "NY", "P", "Q", "R", "S", "T", "V", "W", "X", "Z"};
    public List<String> wordList;
    public List<String> usedWords = new ArrayList<>();

    public Game(){
        wordList = new ArrayList<>();
        FileHandle fileHandle = Gdx.files.internal("dicts/catala_dict.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(fileHandle.reader());
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                wordList.add(word);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        usedWords.add("");
        usedWords.add("");
        usedWords.add("");
        usedWords.add("");
        usedWords.add("");
        usedWords.add("");

    }

    public String[] generateRoscoLetters() {
        String[] rosco_letters = { "", "", "", "", "", "", "", "" };
        Random random = new Random();
        int countVocales = 0;

        for (int i = 0; i < 8; i++) {
            boolean isVocal = false;
            if (i < 2) { // Las dos primeras letras son vocales
                isVocal = true;
                countVocales++;
            } else if (i < 5) { // Aqui se calcula si hay vocales extra o consonantes extra
                if (countVocales == 2 && random.nextDouble() < 0.4) {
                    isVocal = true;
                    countVocales++;
                } else if (countVocales == 3 && random.nextDouble() < 0.2) {
                    isVocal = true;
                    countVocales++;
                } else if (countVocales == 4 && random.nextDouble() < 0.1) {
                    isVocal = true;
                    countVocales++;
                }
            }
            rosco_letters[i] = getRandomLetter(isVocal, random, rosco_letters);
        }
        return rosco_letters;

    }

    public int calculateWordPoints(String player_word) {
        if (player_word.equals("KIWI")) {
            return 99999999;
        }
        int wordPoints = 0;
        for (char letter : player_word.toCharArray()) {
            if ("EAIRSNOTLU".indexOf(letter) != -1) {
                wordPoints += 1;
            } else if ("CDM".indexOf(letter) != -1) {
                wordPoints += 2;
            } else if ("BGP".indexOf(letter) != -1) {
                wordPoints += 3;
            } else if ("FV".indexOf(letter) != -1) {
                wordPoints += 4;
            } else if ("HJQZ".indexOf(letter) != -1) {
                wordPoints += 8;
            } else {
                wordPoints += 10;
            }
        }

        return wordPoints * ((int) (player_word.length() / 2));
    }

    public boolean checkUserWord(String player_word, List<String> wordList) {
        int compareNum = 0;
        boolean wordNotInDict = false;
        Collator collator = Collator.getInstance(new Locale("ca"));

        if (wordList.size() > 1) {
            compareNum = collator.compare(player_word.replaceAll("·", ""),
                    (String) (wordList.get(wordList.size() / 2)).replaceAll("·", ""));
        } else {
            if (!(player_word.equals(wordList.get(0)))) {
                wordNotInDict = true;
            }
        }

        if (wordNotInDict) {
            return false;
        } else if (compareNum < 0) {
            return checkUserWord(player_word, wordList.subList(0, wordList.size() / 2));
        } else if (compareNum > 0) {
            return checkUserWord(player_word, wordList.subList(wordList.size() / 2, wordList.size()));
        } else {
            return true;
        }
    }

    private String getRandomLetter(boolean isVocal, Random random, String[] rosco_letters) {
        String newLetter = "";
        boolean letterInRosco = true;

        while (letterInRosco) {
            newLetter = isVocal ? vocales[random.nextInt(vocales.length)] : consonantes[random.nextInt(consonantes.length)];
            letterInRosco = checkLettersInRosco(newLetter, rosco_letters);
        }

        return newLetter;
    }

    private boolean checkLettersInRosco(String newLetter, String[] rosco_letters) {
        for (String letter : rosco_letters) {
            if (letter.equals(newLetter)) {
                return true;
            }
        }
        return false;
    }

    public Array<Actor> generateRosco(String[] rosco_letters) {
        Array<Actor> actors = new Array<Actor>();
        int numberLetters = rosco_letters.length;
        int radius = 140;

        for (int i=0; i < numberLetters; i++) {
            float rad = (float) (2 * Math.PI * i / numberLetters);

            String letter = rosco_letters[i];
            Image letterButton = new Image(new Texture(Gdx.files.internal("images/button.png")));
            letterButton.setSize(75,75);
            letterButton.setPosition( (float) (radius * Math.cos(rad)) + (GAME_WIDTH/2f-letterButton.getWidth()/2), (float) (radius * Math.sin(rad)) + 150);
            letterButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (word.length()<=15){
                        word = word + letter.toUpperCase();
                        wordLabel.setText(word);
                    }
                }
            });
            actors.add(letterButton);

            Label letterLabel = new Label(letter, skin, "big");
            letterLabel.setPosition( (float) (radius * Math.cos(rad)) + (GAME_WIDTH/2f-letterLabel.getWidth()/2), (float) (radius * Math.sin(rad)) + 150 );
            letterLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (word.length()<=14){
                        word = word + letter.toUpperCase();
                        wordLabel.setText(word);
                    }
                }
            });
            actors.add(letterLabel);
        }

        return actors;
    }

}
