package Model;

import sun.reflect.generics.tree.Tree;
import wordle.Utils;

import java.lang.reflect.Array;
import java.util.*;

//TO parse down dictionary and givwe suggestions
public class Suggestions {
    private Wordle game;
    private Set<String> validWords;
    private ArrayList<String> wrongLetters;
    private ArrayList<String> correctLetterWrongPos;
    private ArrayList<String> correctLetter;
    private ArrayList<String> guesses;
    private HashMap<String, Integer> validLetterHash;
    ArrayList seen;

    /**
     * Constructor
     * @author: Kevin Paganini
     */
    public Suggestions(){
        this.game = null;
        this.guesses = new ArrayList<>();
        this.validWords = new HashSet<>();
        this.validLetterHash = Utils.makeInitialHashMapForKeyBoardColors();
        this.seen = new ArrayList();
    }

    /**
     * Add wordle game to suggestions so it can process and suggest
     * @param wordle
     */
    public void addGame(Wordle wordle) {
        this.game = wordle;
        Set<String> temp = wordle.getDictionary();

        this.validWords.addAll(temp);
    }



    /**
     * Prunes the dictionary
     * @return Set with possible words
     * @author: Kevin Paganini
     */
    public Set<String> pruneDictionary(){
        int[] positions = game.getPositionsArray();
        char[] currentGuess = game.getCurrentGuess().toCharArray();
        System.out.println(game.getCurrentGuess());
        System.out.println(game.getTarget());
        validWords.remove(game.getCurrentGuess().toLowerCase(Locale.ROOT));
        for(int i = 0; i < positions.length; i++){
            int isCorrectValue = positions[i];
            String letter = String.valueOf(currentGuess[i]).toUpperCase(Locale.ROOT);
            if (validLetterHash.get(letter) < isCorrectValue){ // Checks if value stored is smaller than value achieved
                validLetterHash.replace(letter, isCorrectValue); // If guess has higher value replaces old value
            }


        }
        boolean doubleLetter = false;
        for(int j = 0; j < currentGuess.length; j++){
            for(int i = 0; i < currentGuess.length; i++){
                if(i!=j){
                    if (currentGuess[i] == currentGuess[j]){
                        doubleLetter = true;
                    }
                }
            }
            String letter = String.valueOf(currentGuess[j]).toUpperCase(Locale.ROOT);
            int value = validLetterHash.get(letter);
            if(value == 0 && !seen.contains(letter) && !doubleLetter) {
                removeWrongLetterWords(letter);
            }
            if(value == 1 && !seen.contains(letter)){
                removeCorrectLetterWrongPos(letter, j);
            }
            if (value == 2 && !seen.contains(letter) && positions[j] == 2){
                if(letter.equals("E")){
                    System.out.println();
                }
                removeWordWithoutCorrectLetter(letter, j);
                seen.add(letter);
            }


        }
        return validWords;
    }

    /**
     * Removes words from list that have the wrong letter
     * @param letter
     * @author Kevin Paganini
     */
    public void removeWrongLetterWords(String letter){

        ArrayList<String> badWords = new ArrayList<>();
        ArrayList<String> valid = new ArrayList<>();
        valid.addAll(validWords);
        for(int i = 0; i < valid.size(); i++){
            if(valid.get(i).equals("rogue")){
                System.out.println("Hi");
            }
            if(valid.get(i).contains(letter.toLowerCase(Locale.ROOT))){
                badWords.add(valid.get(i));
            }
        }
        validWords.removeAll(badWords);


    }

    /**
     * Removes words that don't contain passed in letter
     * @param letter
     * @author Kevin Paganini
     */
    public void removeCorrectLetterWrongPos(String letter, int index){
        ArrayList<String> badWords = new ArrayList<>();
        ArrayList<String> valid = new ArrayList<>();
        valid.addAll(validWords);
        for(int i = 0; i < valid.size(); i++){
            if(valid.get(i).equals("rogue")){
                System.out.println("Hi");
            }
            if(!valid.get(i).contains(letter.toLowerCase(Locale.ROOT)) || String.valueOf(valid.get(i).toCharArray()[index]).toUpperCase(Locale.ROOT).equals(letter)){
                badWords.add(valid.get(i));
            }
        }
        validWords.removeAll(badWords);

    }

    /**
     * Removing words without passed in letter at specific spot
     * @param letter
     * @param index
     * @author Kevin Paganini
     */
    public void removeWordWithoutCorrectLetter(String letter, int index){
        ArrayList badWords = new ArrayList();
        ArrayList<String> valid = new ArrayList<>();
        valid.addAll(validWords);
        for(int i = 0; i < valid.size(); i++){
            if(valid.get(i).equals("rogue")){
                System.out.println("Hi");

            }
            String validLetterCheck = String.valueOf(valid.get(i).toCharArray()[index]).toUpperCase(Locale.ROOT);
            if(!String.valueOf(valid.get(i).toCharArray()[index]).toUpperCase(Locale.ROOT).equals(letter)){
                badWords.add(valid.get(i));
            }
        }
        validWords.removeAll(badWords);
    }


}
