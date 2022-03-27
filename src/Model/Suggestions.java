package Model;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to give the user suggestions based on all possible words in their loaded dictionary that
 * can be guessed based on the previous guesses in this match.
 *
 * ONLY ONE SUGGESTION SHOULD BE CREATED, AND ONLY IN THE SESSION CLASS.
 *
 * @author ??? Atreyu Schilling
 */
public class Suggestions {
    private Wordle game;

    //Because Strings are immutable this is safe to mess with
    private Set<String> validWords;


    //The only thing that should be creating one of these is the Session.
    protected Suggestions(){}
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

    /**
     * Removes any elements of the dictionary that cannot be guessed due to previous guesses and returns
     * a list of all possible guesses left.
     * Uses the wordle's CURRENT GUESSES to determine guesses of the match.
     * @return Set with ALL possible words to guess for
     * @author ??? Atreyu Schilling
     */
    public Set<String> returnPossibleWords() {
        Stream<String> validStream = validWords.stream();
        List<Guess> guesses = game.getCurrentGuesses();
        for (Guess guess : guesses) {
            //Looking at every guess
            char[] letterArr = guess.getGuess().toCharArray();
            int[] positions = game.returnPositionsOnly(guess.getGuess());

            validStream = validStream.filter((word) -> {

                //Here's the cute little test I left in
                if (word.equals("rogue"))
                    System.out.println("Hi");

                //First we sort through all the letters in the letter array
                for (int i = 0; i < letterArr.length; i++) {

                    boolean letterExistsHere = word.charAt(i) == letterArr[i];
                    boolean letterExists = word.indexOf(letterArr[i]) == -1;

                    if ((positions[i] == 2 && !letterExistsHere) ||
                            //If the letter should exist here but doesn't
                            (positions[i] == 1 && (letterExistsHere || !letterExists)) ||
                            //Or if the letter should exist somewhere that isn't here & exists here or doesn't exist at all
                            (positions[i] == 0 && letterExists)) {
                            //Or if the letter shouldn't exist at all and does exist
                        return false;
                        //Kick it
                    }

                }
                //If it passed all the tests for each letter, this ONE WORD works for this ONE GUESS
                return true;
            });
        }
        return validStream.collect(Collectors.toSet());
    }
}
