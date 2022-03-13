package wordle;

import java.nio.file.Path;
import java.util.*;

public class Wordle {
    TreeSet<String> dictionary = null;
    String target = null;




    /**
     * Loads the dictionary file into the program
     * @param path path to read dictionary from
     * @throws IllegalFormatException File cannot be parsed as a dictionary
     */
    public void loadDictionary(Path path) {
        //TODO
    }

    /**
     * Forces a provided string to become the target word
     * If the target word is not in the provided dictionary, the target does not change
     * @param forcedTarget string to force the target to be
     * @return False if the target is not in the dictionary, true otherwise
     */
    public boolean makeTarget(String forcedTarget) {
        //TODO
        return false;
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
        //TODO
    }

    private void setTarget(String target) {
        this.target = target;
    }

    //I feel like a comment is a little unnecessary here, it does what it says on the tin
    public boolean isValidWord(String word) {
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
        //TODO
        return null;
    }

}
