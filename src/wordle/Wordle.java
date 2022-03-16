package wordle;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Wordle {
    //The naming scheme for private final is the same as private non-final. Only public final has SCREAMING_CAMEL_CASE
    private final int numLetters;
    // The wordle might not even need to know how many guesses it's had.
    // I'm leaving this here, but it may not be necessary. Weird
    private final int numGuesses;
    private String target;
    TreeSet<String> dictionary = null;

    public Wordle(int numGuesses, int numLetters, File dictionary) throws IOException {
        this.numGuesses = numGuesses;
        this.numLetters = numLetters;
        loadDictionary(dictionary);
        this.target = randomTarget();
    }

    /**
     * Loads the dictionary file into the program
     * @param file to read dictionary from
     * @throws IOException file contains invalid entries (wrong length or non-letter characters)
     * @author Kevin Paganini, Atreyu Schilling
     */
    private void loadDictionary(File file) throws IOException{
        dictionary = new TreeSet<>();
        Scanner sc = new Scanner(file);
        //Line tracker for debug purposes
        int line = 1;
        while(sc.hasNextLine()) {
            String cookie = sc.nextLine().trim().toLowerCase(Locale.ROOT);
            if (cookie.length() != numLetters)
                throw new IOException("Line " + line + " contains a string of invalid length");
            // Minor fix to regex here
            if (!cookie.matches("^[A-Za-z]+$"))
                throw new IOException("Line " + line + " contains a string with invalid characters");
            line++;
            dictionary.add(cookie);
        }
    }

    /**
     * Forces a provided string to become the target word
     * If the target word is not in the provided dictionary, the target does not change
     *
     * This should primarily be used for debug purposes
     *
     * @param target string to force the target to be
     * @return False if the target is not in the dictionary, true otherwise
     * @author Kevin Paganini, Atreyu Schilling
     */
    public boolean forceTarget(String target) {
        if(dictionary.contains(target)){
            this.target = target;
            return true;
        }
        return false;
    }

    /**
     * Random Target Method from start
     * THIS IS THE METHOD USED TO MAKE A TARGET
     * //TODO: Could add difficulty rankings
     * @author paganinik, Atreyu Schilling
     */
    public String randomTarget() {
        return (String) dictionary.toArray()[(int) (Math.random() * dictionary.size())];
    }

    /**
     * Checks if the dictionary contains the word
     * @param word word to be checked against the dictionary
     * @return true if word is in dictionary, false otherwise
     */
    public boolean isValidWord(String word) {

        return dictionary.contains(word);
    }

    public boolean hasTarget() {
        return target != null && !target.equals("");
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
     *
     * @param guess word to be checked against the target
     * @return array of ints with the same length as the string
     * @author Atreyu Schilling, TODO
     */
    public int[] returnPositions(String guess) {
        if (!hasTarget()) {
            throw new NullPointerException("Target is null or empty");
        }
        char[] targetChars = target.toLowerCase(Locale.ROOT).toCharArray();
        char[] guessChars = guess.toLowerCase(Locale.ROOT).toCharArray();
        int[] resultantArray = new int[numLetters];
        //Because array's initialize to all 0s, we don't need to fill it.

        //All this logic is just to protect against double-dipping in the target array, but it's important.
        for (int i = 0; i < numLetters; i++) {
            if (guessChars[i] == targetChars[i]) {
                //If it's correct, we shouldn't double-dip on the target array.
                guessChars[i] = 0;
                targetChars[i] = 0;
                resultantArray[i] = 2;
            }
        }
        //Now that we removed perfect matches, let's deal with imperfect matches
        for (int i = 0; i < numLetters; i++) {
            if (guessChars[i] == 0) break;
            for (int j = 0; j < numLetters; j++) {
                // If we find the character we need, because we checked for same position, it's always a 1
                // Again kick out the target to not double-dip
                if (targetChars[j] == guessChars[i]) {
                    resultantArray[i] = 1;
                    break;
                }
            }
        }
        //Since anything we didn't deal with is still 0s from initializing the array, we're done here
        return resultantArray;

        /*
        char[] targetArray = target.toUpperCase(Locale.ROOT).toCharArray();
        char[] guessArray = guess.toUpperCase(Locale.ROOT).toCharArray();
        int[] correctPositionArray = new int[this.numLetters];
        for(int i = 0; i < targetArray.length; i++){
            //check if first letter is in target
            //check if first letter is in correct position
            if(!target.contains("" + guess.charAt(i))){
                correctPositionArray[i] = 0;
            }
            if(target.contains("" + guess.charAt(i))){
                correctPositionArray[i] = 1;
            }
            if(target.charAt(i) == guess.charAt(i)){
                correctPositionArray[i] = 2;
            }
        }
        return correctPositionArray;
         */
    }


    /**
     * Returns true if a provided guess matches the target and false if not.
     * @param guess guess to be checked against the target
     * @return true if target matches guess ignoring case, false otherwise
     */

    public boolean isWinner(String guess) {
        return guess.equalsIgnoreCase(target);
        /*
        int[] correctPositions = returnPositions(guess);
        for (int x : correctPositions){
            if (x != 2){
                return false;
            }
        }
        return true
         */
    }

}
