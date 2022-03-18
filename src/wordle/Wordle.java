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
     * Reads dictionary file and interprets all words in file. If it can be properly parsed,
     * the dictionary will be loaded into dictionary interally. Throws IOException if it can't
     * interpret the file properly.
     *
     * Also clears out the dictionary before it loads, in case something breaks
     * Does mean that invalid files unload dictionary, unfortunately
     *
     * @param file to read dictionary from
     * @throws IOException file contains invalid entries (wrong length or non-letter characters)
     * @author Kevin Paganini, Atreyu Schilling
     */
    private void loadDictionary(File file) throws IOException{
        try {
            dictionary = new TreeSet<>();
            Scanner sc = new Scanner(file);
            //Line tracker for debug purposes
            int line = 1;
            while (sc.hasNextLine()) {
                String cookie = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                if (cookie.length() != numLetters)
                    throw new IOException("Line " + line + " contains a string of invalid length");
                // Minor fix to regex here
                if (!cookie.matches("^[A-Za-z]+$"))
                    throw new IOException("Line " + line + " contains a string with invalid characters");
                line++;
                dictionary.add(cookie);
            }
        } catch (IOException e) {
            dictionary.clear();
            throw e;
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
     * TODO: Could add difficulty rankings
     * @author paganinik, Atreyu Schilling
     */
    public String randomTarget() {
        //return (String) dictionary.toArray()[(int) (Math.random() * dictionary.size())];
        return "debug";
    }

    /**
     * Checks if the dictionary contains the word
     * @param word word to be checked against the dictionary
     * @return true if word is in dictionary, false otherwise
     */
    public boolean isValidWord(String word) {
        return dictionary.contains(word.toLowerCase());
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
     * returns a null array if the guess is not a valid word
     *
     * @param guess word to be checked against the target
     * @return array of ints with the same length as the string, or null
     * if the guess is invalid
     * @author Atreyu Schilling, TODO
     */
    public int[] returnPositions(String guess) {
        System.out.println(target);
        if (!hasTarget()) {
            throw new NullPointerException("Target is null or empty");
        }
        if (!isValidWord(guess)) {
            return null;
        }
        char[] targetChars = target.toLowerCase(Locale.ROOT).toCharArray();
        char[] guessChars = guess.toLowerCase(Locale.ROOT).toCharArray();
        int[] resultantArray = new int[numLetters];
        //Because array's initialize to all 0s, no need to fill it.

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
            if (guessChars[i] == 0) break;
            for (int j = 0; j < numLetters; j++) {
                // If character is found, it's always a 1, since perfect matches were already dealt with
                // Again kick out the target to not double-dip
                if (targetChars[j] == guessChars[i]) {
                    resultantArray[i] = 1;
                    break;
                }
            }
        }
        //Since anything not dealt with is still 0s from initializing the array, just return
        return resultantArray;
    }


    /**
     * Returns true if a provided guess matches the target and false if not.
     * @param guess guess to be checked against the target
     * @return true if target matches guess ignoring case, false otherwise
     * @author Kevin Paganini / Someone else
     */

    public boolean isWinner(String guess) {
        return guess.equalsIgnoreCase(target);
    }

}
