package Model;

import wordle.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * Session class to keep track of user statistics for one run through of program
 * @author Kevin Paganini
 */
public class Session {
    public static final File STORAGE_FILE = new File("src/Resources/previousGuesses.txt");
    private final ArrayList<Wordle> games = new ArrayList<>();

    public void addGame(Wordle wordle){
        games.add(wordle);
    }

    /**
     * Returns the average guesses per game as a double
     *
     * @return guesses / games, as a double
     *         Returns -1 if no games have been played.
     */
    public double getAverageGuesses(){
        double guesses = 0;
        double matches = 0;
        for (Wordle game : games) {
            guesses += game.getGuesses().size();
            //The Wordle doesn't represent a single game, but a single set of settings.
            for (Guess guess : game.getGuesses()) {
                if (guess.isWin() || guess.isLoss()) matches++;
            }
        }
        return guesses / matches;
    }


    /**
     * Returns the frequency of letters previously guessed as a key-value pair,
     * where the key represents the letter and the value represents the frequency of that letter
     *
     * @return HashMap where key is the letter and value is the frequency
     */
    public HashMap<String, Integer> getLetterGuessFrequency(){
        HashMap<String, Integer> letterFrequency = Utils.intializeLetterFrequency();
        for (Wordle game : games) {
            for (Guess guess : game.getGuesses()) {
                //For every guess of every game, split them into characters and cycle over them
                //Because the utils function returns all capitals,
                //the resultant letters are capitalized as well
                char[] guessLetters = guess.getGuess().toUpperCase(Locale.ROOT).toCharArray();
                for (char letter : guessLetters) {
                    String letterStr = String.valueOf(letter);
                    //Initializes the value in the frequency map to 1 if it doesn't exist, doesn't initialize it if not
                    letterFrequency.merge(letterStr, 1, Integer::sum);
                }
            }
        }
        return letterFrequency;
    }


    /**
     * Returns the frequency of words previously guessed as a key-value pair,
     * where the key represents the word and the value represents the frequency of that word
     *
     * @return HashMap where key is the word and value is the frequency
     */
    public HashMap<String, Integer> getWordGuessFrequency(){

        HashMap<String, Integer> wordFrequency = new HashMap<>();
        for (Wordle game : games) {
            for (Guess guess : game.getGuesses()) {
                //For every guess of every game,
                //assigns the index to 1 if there's no value there, and V+1 if there is
                wordFrequency.merge(guess.getGuess(), 1, Integer::sum);
            }
        }
        return Utils.sortHashMapByValue(wordFrequency);
    }

    public int getWins(){
        int totalWins = 0;
        for (Wordle game : games) {
            for (Guess guess : game.getGuesses()) {
                if(guess.isWin()) totalWins++;
            }
        }
        return totalWins;
    }

    public int getLosses(){
        int totalLosses = 0;
        for (Wordle game : games) {
            for (Guess guess : game.getGuesses()) {
                if(guess.isLoss()) totalLosses++;
            }
        }
        return totalLosses;
    }

    /**
     * Returns the longest win-streak so far
     * @return Longest win-streak as an integer
     */
    public int getWinStreak(){
        int longestWinStreak = 0;
        int currentWinStreak = 0;
        for (Wordle game : games) {
            for (Guess guess : game.getGuesses()) {
                if (guess.isWin()) {
                    currentWinStreak++;
                } else if(guess.isLoss()) {
                    longestWinStreak = Math.max(currentWinStreak, longestWinStreak);
                    currentWinStreak = 0;
                }
            }
        }
        return Math.max(currentWinStreak, longestWinStreak);
    }



    /**
     * USED FOR DEBUGGING - DO NOT USE OTHERWISE
     */
    public void prettyString() {
        System.out.println("Average guess number: " + getAverageGuesses());
        System.out.println("Letter Frequency:");
        HashMap<String, Integer> printLetter = getLetterGuessFrequency();
        Utils.printHashMap(printLetter);
        System.out.println("Number of Wins: " + getWins());
        System.out.println("Number of Losses: " + getLosses());
        System.out.println("Longest Win Streak: " + getWinStreak());
        System.out.println("Word freqeuncy: ");
        Utils.printHashMap(getWordGuessFrequency());
    }


}
