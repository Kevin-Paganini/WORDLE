package Model;

import wordle.Utils;

import java.util.*;

//TO parse down dictionary and give suggestions
public class Suggestions {
    private Wordle game;

    //THIS IS A DEEP COPY. Make SURE this is a deep copy or the game will break.
    private Set<String> validWords;

    private final HashMap<String, Integer> validLetterHash;
    List<String> seen;

    //The only thing that should be creating one of these is the Session. That's it. Don't mess with this
    protected Suggestions(){
        this.validLetterHash = Utils.makeInitialHashMapForKeyBoardColors();
    }

    /**
     * Attaches the provided Wordle object to this object. Suggestions will have
     * no functionality until a game is added. Once a game is added, all data regarding
     * the previous game is wiped.
     *
     * @param wordle wordle to attach suggestions to
     * @author Kevin Paganini, Atreyu Schilling
     */
    public void addGame(Wordle wordle) {
        this.game = wordle;
        this.validWords = new TreeSet<>(wordle.getDictionary());
    }

    public List<Guess> getGuesses(){
        return game.getGuesses();
    }

    /**
     * Prunes the dictionary
     * @return Set with possible words
     */
    public Set<String> pruneDictionary(){
        List<Guess> currentGuesses = game.getCurrentGuesses();
        int[] positions = game.returnPositionsOnly(currentGuesses.get(1).getGuess());
        for (Guess guess : currentGuesses) {
            validWords.remove(guess.getGuess().toLowerCase(Locale.ROOT));
        }
        for(int position : positions){
            String letter = String.valueOf(position).toUpperCase(Locale.ROOT);
            if (validLetterHash.get(letter) < position){ // Checks if value stored is smaller than value achieved
                validLetterHash.replace(letter, position); // If guess has higher value replaces old value
            }
        }
        boolean doubleLetter = false;
        for (Guess guess : currentGuesses) {
            char[] currentGuess = guess.getGuess().toCharArray();
            for(int j = 0; j < currentGuess.length; j++){
                for(int i = 0; i < currentGuess.length; i++){
                    if (i != j && currentGuess[i] == currentGuess[j]) {
                        doubleLetter = true;
                        break;
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