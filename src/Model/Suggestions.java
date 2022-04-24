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
        validWords.removeIf(s -> !shouldKeep(s, game.getCurrentGuess()));
        return sortValidWords(validWords);
    }

    private Set<String> sortValidWords(Set<String> validWords) {
        Set<String> validWordsSorted = new TreeSet<>();
        //Create letter frequency going through all valid words left
        //Sort by highest frequency
        // Suggest words with letters with highest frequency
        HashMap<String, Integer> letterFreq = Utils.intializeLetterFrequency();
        for (String word : validWords) {
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
            for (String s : validWords) {
                if (s.toUpperCase(Locale.ROOT).contains(letter)) {
                    validWordsSorted.add(s);
                }
            }

        }
        return validWordsSorted;
    }

    private boolean shouldKeep(String dictWord, String guess) {
        char[] guessArr = guess.toUpperCase(Locale.ROOT).toCharArray();
        char[] dictArr = dictWord.toUpperCase(Locale.ROOT).toCharArray();
        int[] positions = game.returnPositionsOnly(guess);
        //Green checks
        for (int i = 0; i < guessArr.length; i++) {
            if (positions[i] != 2) continue;
            if (guessArr[i] != dictArr[i]) return false;
            guessArr[i] = 0;
            dictArr[i] = 0;
        }
        //Yellow checks
        for (int i = 0; i < guessArr.length; i++) {
            if (positions[i] != 1 || guessArr[i] == 0) continue;
            for (int j = 0; j < dictArr.length; j++) {
                if(guessArr[i] == dictArr[j]) {
                    if (i == j) return false;
                    guessArr[i] = 0;
                    dictArr[j] = 0;
                    break;
                }
                //Letter not found
                if (j == dictArr.length - 1) return false;
            }
        }
        for (int i = 0; i < guessArr.length; i++) {
            if (positions[i] != 0 || guessArr[i] == 0) continue;
            for (char c : dictArr) {
                if (c == guessArr[i]) return false;
            }
        }
        return true;
    }

    public void removeGreen(int index, char letter) {
        validWords.removeIf(s -> s.charAt(index) != letter);
    }
}
