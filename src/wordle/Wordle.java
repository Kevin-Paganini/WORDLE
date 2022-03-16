package wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Wordle {
    private final int NUM_GUESSES;
    private final int NUM_LETTERS;

    TreeSet<String> dictionary = null;
    String target = null;


    /**
     *
     *
     *
     *
     *
     * @param numGuesses
     * @param numLetters
     *
     */
    public Wordle(int numGuesses, int numLetters, File dictionary) {
        this.NUM_GUESSES = numGuesses;
        this.NUM_LETTERS = numLetters;
        loadDictionary(dictionary);
        this.target = randomTarget();

    }

    /**
     * Loads the dictionary file into the program
     * @param File to read dictionary from
     * @throws IOException file contains invalid entries (wrong length or non-letter characters)
     * @Author Kevin Paganini, Atreyu Schilling
     */
    public void loadDictionary(File file) {
        dictionary = new TreeSet<>();

        try {
            Scanner sc = new Scanner(file);
            //Line tracker for debug purposes
            int line = 1;
            while(sc.hasNextLine()) {
                String cookie = sc.nextLine().trim().toLowerCase(Locale.ROOT);
                //TODO
                if (cookie.length() != NUM_LETTERS)
                    throw new IOException("Line " + line + " contains a string of invalid length");

                if (!cookie.matches("^[A-Za-z]+$"))
                    throw new IOException("Line " + line + " contains a string with invalid characters");

                line++;
                dictionary.add(cookie);
            }

        } catch (IOException e){
            System.out.println("Bad File");
        }



    }

    /**
     * Forces a provided string to become the target word
     * If the target word is not in the provided dictionary, the target does not change
     * @param forcedTarget string to force the target to be
     * @return False if the target is not in the dictionary, true otherwise
     * @author Kevin Paganini, Atreyu Schilling
     */
    public boolean makeTarget(String forcedTarget) {
        if (!hasDictionary()) {
            throw new NullPointerException("Dictionary does not exist");
        }
        if(dictionary.contains(forcedTarget)){
            target = forcedTarget;
            return true;
        }
        return false;
    }

    /**
     * Random Target Method from start
     * THIS IS THE METHOD USED TO MAKE A TARGET
     * //TO-DO Could add difficulty rankings
     * @author paganinik
     */
    public String randomTarget(){
        double random = Math.random();
        int randomChoice = (int) (random * dictionary.size());

        String[] dictionaryArray = dictionary.toArray(new String[dictionary.size()]);
        return dictionaryArray[randomChoice];
    }

    /**
     * Selects a target at random from the provided dictionary
     * This should be the only one that a controller interacts with
     * @throws NullPointerException dictionary has not been loaded
     */
    public void makeTarget() {
        if (!hasDictionary()) {
            throw new NullPointerException("Dictionary does not exist");
        }

    }

    /**
     * VOLATILE SETTER - DO NOT LET THE USER INTERACT WITH THIS METHOD DIRECTLY
     * Sets the target WITHOUT checking if it is a valid word
     * Use makeTarget if you're not testing anything
     * @param target string to set the target to
     */
    private void setTarget(String target) {
        this.target = target;
    }

    //I feel like a comment is a little unnecessary here, it does what it says on the tin

    /**
     * THIS IS THE METHOD THAT CHECKS IF THE WORD IN THE FIELD IS IN DICTIONARY
     * @param word
     * @return true if word is in dictionary
     */
    public boolean isValidWord(String word) {
        System.out.println("Hello I made it");
        return dictionary.contains(word);
    }

    public boolean hasDictionary() {
        return dictionary != null && !dictionary.isEmpty();
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
     */
    public int[] returnPositions(String guess) {
        if (target == null) {
            throw new NullPointerException("Target word cannot be null");
        }
        char[] targetArray = target.toUpperCase(Locale.ROOT).toCharArray();
        char[] guessArray = guess.toUpperCase(Locale.ROOT).toCharArray();
        int[] correctPositionArray = new int[this.NUM_LETTERS];
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
    }

}
