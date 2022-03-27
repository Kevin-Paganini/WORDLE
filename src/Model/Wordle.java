package Model;


import java.io.*;
import java.util.*;

public class Wordle {
    //Enables DEBUG mode
    public static final boolean DEBUG = false;
    //File that guesses are written to - if it doesn't exist, create it


    private final int numLetters;
    private final int guessesPossible;
    private int guessesLeft;
    private String target;
    private TreeSet<String> dictionary = null;

    private int[] positions = null;
    private final List<String> currentGuesses;

    private final ArrayList<Guess> guessList = new ArrayList<>();

    public Wordle(int numGuesses, int numLetters, File dictionary, Session session) throws IOException {
        guessesPossible = numGuesses;
        this.guessesLeft = numGuesses;
        this.numLetters = numLetters;
        loadDictionary(dictionary);
        this.target = randomTarget();
        session.addGame(this);
        currentGuesses = new ArrayList<>();
    }

    /**
     * Reads dictionary file and interprets all words in file. If it can be properly parsed,
     * the dictionary will be loaded into dictionary interally. Throws IOException if it can't
     * interpret the file properly.
     *
     * Does not clear the existing dictionary until the file is interpreted completely
     *
     * @param file to read dictionary from
     * @throws IOException file contains invalid entries (wrong length or non-letter characters)
     * @author Kevin Paganini, Atreyu Schilling
     */
    private void loadDictionary(File file) throws IOException{
            if (DEBUG) {
                System.out.println("\n\n\n");
            }
            TreeSet<String> tempDict = new TreeSet<>();
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
                tempDict.add(cookie);
                if (DEBUG) {

                    System.out.println(cookie);
                }
            }
            dictionary = tempDict;
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
        String target = (String) dictionary.toArray()[(int) (Math.random() * dictionary.size())];
        if (DEBUG) System.out.println(target);

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
    public int[] returnPositions(String guess) {
        if (DEBUG) System.out.println(target);
        guessesLeft--;
        if (!isValidWord(guess)) {
            return null;
        }
        if (DEBUG) System.out.println(guess);
        if (isWinner(guess)) {
            guessList.add(new Guess(guess, false, true));
            guessesLeft=guessesPossible;
            currentGuesses.clear();
        } else if (guessesLeft == 0){
            guessList.add(new Guess(guess, true, false));
            guessesLeft=guessesPossible;
            currentGuesses.clear();
        } else {
            guessList.add(new Guess(guess, false, false));
            guessesLeft--;
            currentGuesses.add(guess);
        }


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
        positions = resultantArray;
        //Since anything not dealt with is still 0s from initializing the array, just return
        return resultantArray;
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

    public int[] getPositionsArray(){
        return positions;
    }

    public List<String> getCurrentGuesses(){
        return currentGuesses;
    }


}
