package Model;


import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Wordle {
    //Enables DEBUG mode
    public static final boolean DEBUG = false;
    private String currentGuess;
    private int[] posArray = null;
    private int numLetters;
    private final int guessesPossible;
    private int guessesLeft;
    private String target;
    private final TreeSet<String> dictionary;
    private final Suggestions suggestions;

    private final ArrayList<Guess> guessList = new ArrayList<>();

    public Wordle(int numGuesses, File dictionary, Session session) throws IOException {
        currentGuess = "";
        this.dictionary = new TreeSet<>();
        guessesPossible = numGuesses;
        this.guessesLeft = numGuesses;
        loadDictionary(dictionary);
        this.target = randomTarget();
        session.addGame(this);
        suggestions = new Suggestions();
        suggestions.addGame(this);
    }

    /**
     * Reads dictionary file and interprets all words in file. If it can be properly parsed,
     * the dictionary will be loaded into dictionary interally. Throws IOException if it can't
     * interpret the file properly. An error will fail the object's construction.
     *
     * @param file to read dictionary from
     * @throws IOException file contains invalid entries (wrong length or non-letter characters)
     * @author Kevin Paganini, Atreyu Schilling
     */
    private void loadDictionary(File file) throws IOException{
            if (DEBUG) System.out.println("\n\n\n");

            Scanner sc = new Scanner(file);
            //Line tracker for debug purposes
            int line = 1;
            String firstWord = sc.nextLine().trim().toLowerCase(Locale.ROOT);
            if (!firstWord.matches("^[A-Za-z]+$"))
                throw new IOException("Line " + line + " contains a string with invalid characters");
            else {
                numLetters = firstWord.length();
                dictionary.add(firstWord);
            }
            while (sc.hasNextLine()) {
                String cookie = sc.nextLine().trim().toLowerCase(Locale.ROOT);


                if (cookie.length() != numLetters)
                    throw new IOException("Line " + line + " contains a string of invalid length");
                // Minor fix to regex here
                if (!cookie.matches("^[A-Za-z]+$"))
                    throw new IOException("Line " + line + " contains a string with invalid characters");
                line++;
                dictionary.add(cookie);
                if (DEBUG) System.out.println(cookie);
            }
    }

    /**
     * Forces a provided string to become the target word
     * If the target word is not in the provided dictionary, an IllegalArgumentException is thrown
     *
     * This should primarily be used for debug purposes
     *
     * @param target string to force the target to be
     * @throws IllegalArgumentException a word not contained in the provided dictionary is passed
     * @author Kevin Paganini, Atreyu Schilling
     */
    public void forceTarget(String target) {
        if(!dictionary.contains(target)){
            throw new IllegalArgumentException(target + " not in provided dictionary");
        }
        this.target = target;
    }

    /**
     * Random Target Method from start
     * THIS IS THE METHOD USED TO MAKE A TARGET
     *
     * @author paganinik, Atreyu Schilling
     */
    public String randomTarget() {
        String target = (String) dictionary.toArray()[(int) (Math.random() * dictionary.size())];
        if (DEBUG) System.out.println("Target: " + target);
        //target = "thick";
        return target;
    }

    /**
     * Checks if the dictionary contains the word
     * @param word word to be checked against the dictionary
     * @return true if word is in dictionary, false otherwise
     */
    public boolean isValidWord(String word) {
        return dictionary.contains(word.toLowerCase());
    }

    /**
     * Returns an array of integers corresponding to the correctness of the
     * guess in relation to the target. The main logic of Wordle.
     *
     * If the letter does not exist at all in the word,
     * the number in the position of that character will be a 0
     *
     * If the letter exists in the word, but not in that position,
     * the number in the position of that character will be a 1
     *
     * If the letter exists in the word and in that position,
     * the number in the position of that character will be a 2
     *
     * Throws a NullPointerException if the target hasn't been loaded
     * returns a null array if the guess is not a valid word
     *
     * @param guess word to be checked against the target
     * @return array of ints with the same length as the string, or null
     * if the guess is invalid
     * @author Atreyu Schilling
     */

    public int[] makeGuess(String guess) {
        currentGuess = guess;
        if (DEBUG) System.out.println("Target: " + target);
        if (!isValidWord(guess)) {
            return null;
        }
        guessesLeft--;
        guess = guess.toUpperCase(Locale.ROOT);
        if (DEBUG) System.out.println("Guess: " + guess);
        if (isWinner(guess)) {
            guessList.add(new Guess(guess, false, true));
            guessesLeft=guessesPossible;
        } else if (guessesLeft == 0){
            guessList.add(new Guess(guess, true, false));
            guessesLeft=guessesPossible;
        } else {
            guessList.add(new Guess(guess, false, false));
            guessesLeft--;
        }
        return returnPositionsOnly(guess);
    }

    public void updateGuesses(String guess){
        guessList.add(new Guess(guess, true, false));
    }

    public void clearGuesses(){
        guessList.clear();
    }

    /**
     * The part of makeGuess WITHOUT SIDE EFFECTS. This should be called when the positions array
     * is needed but the user hasn't made a guess. See makeGuess for the array return type.
     *
     * NOTE: Does not check that the word guessed is in the dictionary. This must either be done separately or assumed.
     *
     * @param guess word to be checked against the target
     * @return array of ints with the same length as the string, or null
     * if the guess is invalid.
     */
    public int[] returnPositionsOnly(String guess) {

        char[] targetChars = target.toLowerCase(Locale.ROOT).toCharArray();
        char[] guessChars = guess.toLowerCase(Locale.ROOT).toCharArray();
        int[] resultantArray = new int[numLetters];

        //All this logic is just to protect against double-dipping in the target array, but it's important.
        //First, run through for all perfect matches
        for (int i = 0; i < numLetters; i++) {
            if (guessChars[i] == targetChars[i]) {
                //If it's correct, shouldn't double-dip on the target array.
                guessChars[i] = 0;
                targetChars[i] = 0;
                resultantArray[i] = 2;
            }
        }
        //Now that perfect matches are removed, deal with imperfect matches
        for (int i = 0; i < numLetters; i++) {
            if (guessChars[i] == 0) continue;
            for (int j = 0; j < numLetters; j++) {
                // If character is found, it's always a 1, since perfect matches were already dealt with
                // Again zero out the target to not double-dip
                if (targetChars[j] == guessChars[i]) {
                    resultantArray[i] = 1;
                    targetChars[j] = 0;
                    break;
                }
            }
        }
        posArray = resultantArray;
        //anything not dealt with is still 0
        return resultantArray;
    }

    /**
     * Returns a pair containing an Integer keyed to a String representing the index
     * of a letter that is correct in the target given the following criteria:
     *  If the user has never guessed a letter in the target, it will return the first instance of this as index-character
     *  If the user has guessed all letters in the target, but not all correctly positioned, it will find the first
     *      incorrectly positioned character and return it as index-character
     *  If the user has guessed all letters, and has guessed all positions, returns the last letter of the target by default
     *
     * This does not track how many times it was called.
     *
     * @return Integer Character pair
     * The Integer indicates the index of the letter to be changed
     * The Character indicates the letter the hint should display
     * @author Atreyu Schilling, Kevin Paganini
     */

    public Pair<Integer, Character> getHint() {
        Set<Character> usedLetters = new TreeSet<>();
        for (Guess guess : guessList) {
            for (char c : guess.getGuess().toLowerCase(Locale.ROOT).toCharArray()){
                usedLetters.add(c);
            }
        }
        //If the letter hasn't ever been guessed, return the first thing it can immediately
        for (int i = 0; i < target.length(); i++) {
            if (!usedLetters.contains(target.charAt(i))) {
                //Same as below, make sure to mess with suggestions accordingly.
                suggestions.removeGreen(i, target.charAt(i));
                return new Pair<>(i, target.charAt(i));
            }
        }
        //Else, go through the target and eliminate letters that are already greened
        char[] targetArr = target.toCharArray();
        for (Guess guess : guessList) {
            //Is there a reason the guess string is uppercase? Everything else here is lowercase.
            String strGuess = guess.getGuess().toLowerCase();
            for (int i = 0; i < strGuess.length(); i++) {
                if (strGuess.charAt(i) == targetArr[i]) {
                    targetArr[i] = 0;
                }
            }
        }
        Pair<Integer, Character> resultPair = new Pair<>(target.length() - 1, target.charAt(target.length() - 1));
        for(int i = 0; i < targetArr.length; i++) {
            if (targetArr[i] != 0) {
                resultPair = new Pair<>(i, targetArr[i]);
                break;
            }
        }
        suggestions.removeGreen(resultPair.getKey(), resultPair.getValue());
        return resultPair;
    }


    /**
     * Returns true if a provided guess matches the target and false if not.
     * @param guess guess to be checked against the target
     * @return true if target matches guess ignoring case, false otherwise
     * @author Kevin Paganini, Atreyu Schilling
     */
    public boolean isWinner(String guess) {
        return guess.equalsIgnoreCase(target);
    }


    public String getTarget() {
        return target;
    }


    public List<Guess> getGuesses(){
        return guessList;
    }

    public TreeSet<String> getDictionary(){
        return dictionary;
    }


    public int[] getPositionsArray() {
        return posArray;
    }

    public String getCurrentGuess() {
        return currentGuess;

    }


    public int getNumLetters() {
        return numLetters;
    }

    public Suggestions getSuggestions() {
        return suggestions;
    }
}
