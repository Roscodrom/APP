package com.roscodrom.roscodrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Game {
    private String[] rosco_letters = { "", "", "", "", "", "", "", "" };
    private String[] vocales = {"A", "E", "I", "O", "U"};
    private String[] consonantes = {"B", "C", "Ç", "D", "F", "G", "H", "J", "K", "L", "L·L", "M",
                                    "N", "NY", "P", "Q", "R", "S", "T", "V", "W", "X", "Z"};
    List<String> wordList;

    public Game(){
        wordList = new ArrayList<>();
        FileHandle fileHandle = Gdx.files.external("dicts/catala_dict.txt");
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
    }

    private void generateRoscoLetters() {
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
            rosco_letters[i] = getRandomLetter(isVocal, random);
        }

    }

    private int calculateWordPoints(String player_word) {
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

    private boolean checkUserWord(String player_word, List<String> wordList) {
        int compareNum = 0;
        boolean wordNotInDict = false;
        Collator collator = Collator.getInstance(new Locale("ca"));

        if (wordList.size() > 1) {
            compareNum = collator.compare(player_word, (String) (wordList.get(wordList.size() / 2)));
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

    private String getRandomLetter(boolean isVocal, Random random) {
        String newLetter = "";
        boolean letterInRosco = true;

        while (letterInRosco) {
            newLetter = isVocal ? vocales[random.nextInt(vocales.length)] : consonantes[random.nextInt(consonantes.length)];
            letterInRosco = checkLettersInRosco(newLetter);
        }

        return newLetter;
    }

    private boolean checkLettersInRosco(String newLetter) {
        for (String letter : rosco_letters) {
            if (letter.equals(newLetter)) {
                return true;
            }
        }
        return false;
    }
}
