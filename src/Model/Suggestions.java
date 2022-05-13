package Model;

import wordle.Utils;

import java.util.*;

//TO parse down dictionary and give suggestions
public class Suggestions {
    private Wordle game;
    private final Set<String> validWords;
    private final HashMap<String, Integer> validLetterHash;
    ArrayList<String> seen;

    /**
     * Constructor
     * @author: Kevin Paganini
     */
    public Suggestions(){
        this.game = null;
        this.validWords = new HashSet<>();
        this.validLetterHash = Utils.makeInitialHashMapForKeyBoardColors();
        this.seen = new ArrayList<>();
    }


    public Set<String> getValidWords(){
        return validWords;
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
        //System.out.println(game.getCurrentGuess());
        //System.out.println(game.getTarget());
        validWords.remove(game.getCurrentGuess().toLowerCase(Locale.ROOT));
        for(int i = 0; i < positions.length; i++){
            int isCorrectValue = positions[i];
            String letter = String.valueOf(currentGuess[i]).toUpperCase(Locale.ROOT);
            if (validLetterHash.get(letter) < isCorrectValue){ // Checks if value stored is smaller than value achieved
                validLetterHash.replace(letter, isCorrectValue); // If guess has higher value replaces old value
            }


        }
        boolean doubleLetter = false;
        char doubleLetterValue = ' ';
        for(int j = 0; j < currentGuess.length; j++) {
            for (int i = 0; i < currentGuess.length; i++) {
                if (i != j) {
                    if (currentGuess[i] == currentGuess[j]) {
                        doubleLetter = true;
                        doubleLetterValue = currentGuess[j];
                    }
                }
            }
        }

        for(int j = 0; j < currentGuess.length; j++){
            String letter = String.valueOf(currentGuess[j]).toUpperCase(Locale.ROOT);
            int value = validLetterHash.get(letter);
            if((value == 0 && !seen.contains(letter)) || doubleLetter) { // if the letter is not wrong or there is a double letter
                if(!doubleLetter || (!String.valueOf(doubleLetterValue).toUpperCase(Locale.ROOT).equals(letter)) || positions[j] == 0){
                    //if there is no double letter or the double letter isnt the one that is in correct position
                    if(value == 0){
                        removeWrongLetterWords(letter);
                    }
                    else if (positions[j] == 0){
                        removeCorrectLetterWrongPos(letter, j);
                    }
                } else if(positions[j] == 0){
                    removeCorrectLetterWrongPos(letter, j);
                }

            }
            if(value == 1 && !seen.contains(letter)){
                removeCorrectLetterWrongPos(letter, j);
            }
            if (value == 2 && positions[j] == 2){
                removeWordWithoutCorrectLetter(letter, j);
                seen.add(letter);
            }



        }
        ArrayList<String> sortedList = sortValidWords();
        validWords.clear();
        validWords.addAll(sortedList);

        return validWords;
    }

    private ArrayList<String> sortValidWords() {
        ArrayList<String> validWordsSorted = new ArrayList<>();
        //Create letter frequency going through all valid words left
        //Sort by highest frequency
        // Suggest words with letters with highest frequency
        ArrayList<String> valid = new ArrayList<>(validWords);
        HashMap<String, Integer> letterFreq = Utils.intializeLetterFrequency();
        for (String word : valid) {
            char[] guessLetters = word.toUpperCase(Locale.ROOT).toCharArray();
            for (char letter : guessLetters) {
                String letterStr = String.valueOf(letter);
                //Initializes the value in the frequency map to 1 if it doesn't exist, doesn't initialize it if not
                letterFreq.merge(letterStr, 1, Integer::sum);
            }


        }
        HashMap<String, Integer> sortedHashLetterFreq = Utils.sortHashMapByValue(letterFreq);
        String [] freqLetters = sortedHashLetterFreq.keySet().toArray(new String[0]);

        for (String letter : freqLetters) {
            for (String s : valid) {
                if (s.toUpperCase(Locale.ROOT).contains(letter)) {
                    if (!validWordsSorted.contains(s)){
                        validWordsSorted.add(s);
                    }

                }
            }

        }
        return validWordsSorted;
    }

    /**
     * Removes words from list that have the wrong letter
     * @param letter
     * @author Kevin Paganini
     */
    public void removeWrongLetterWords(String letter){

        ArrayList<String> badWords = new ArrayList<>();
        ArrayList<String> valid = new ArrayList<>(validWords);
        for (String s : valid) {
            if (s.contains(letter.toLowerCase(Locale.ROOT))) {
                badWords.add(s);
            }
        }
        badWords.forEach(validWords::remove);


    }

    /**
     * Removes words that don't contain passed in letter
     * @param letter
     * @author Kevin Paganini
     */
    public void removeCorrectLetterWrongPos(String letter, int index){
        ArrayList<String> badWords = new ArrayList<>();
        ArrayList<String> valid = new ArrayList<>(validWords);
        for (String s : valid) {
            if (!s.contains(letter.toLowerCase(Locale.ROOT)) ||
                    String.valueOf(s.toCharArray()[index]).toUpperCase(Locale.ROOT).equals(letter)) {
                badWords.add(s);
            }
        }
        badWords.forEach(validWords::remove);

    }

    /**
     * Removing words without passed in letter at specific spot
     * @param letter
     * @param index
     * @author Kevin Paganini
     */
    public void removeWordWithoutCorrectLetter(String letter, int index){
        ArrayList<String> badWords = new ArrayList<>();
        ArrayList<String> valid = new ArrayList<>(validWords);
        for (String s : valid) {
            //String validLetterCheck = String.valueOf(s.toCharArray()[index]).toUpperCase(Locale.ROOT);
            if (!String.valueOf(s.charAt(index)).toUpperCase(Locale.ROOT).equals(letter)) {
                badWords.add(s);
            }
        }
        badWords.forEach(validWords::remove);
    }




}
