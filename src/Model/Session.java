package Model;

import wordle.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Session {
    public static final File STORAGE_FILE = new File("src/Resources/previousGuesses.txt");
    private final ArrayList<Wordle> sessionGames = new ArrayList<>();
    double averageGuesses;
    HashMap<String, Integer> letterFrequency;

    public Session (){
        averageGuesses = 0;
        letterFrequency = Utils.makeInitialHashMapForLetterFrequency();
    }

    public void addGame(Wordle wordle){
        sessionGames.add(wordle);
    }

    public double getAverageGuesses(){
        int total = 0;
        for(int i = 0; i < sessionGames.size(); i++){
           total += sessionGames.get(i).getGuessList().size();
        }
        averageGuesses = total / sessionGames.size();

        return averageGuesses;
    }


    public HashMap<String, Integer> getLetterGuessFrequency(){
        for(int i = 0; i < sessionGames.size(); i++){
            for(int j = 0; j < sessionGames.get(i).getGuessList().size(); j++){
                String guess = sessionGames.get(i).getGuessList().get(j);
                for(int k = 0; k < guess.length(); k++){
                    String current_letter = String.valueOf(guess.toUpperCase(Locale.ROOT).toCharArray()[k]);
                    int value = letterFrequency.get(current_letter);
                    letterFrequency.put(current_letter, ++value);
                }
            }
        }
        return letterFrequency;
    }


    public int getWins(){
        int totalWins = 0;
        for(int i = 0; i < sessionGames.size(); i++){
            if (sessionGames.get(i).isWin()){
                totalWins++;
            }
        }
        return totalWins;
    }

    public int getLosses(){
        int totalLosses = 0;
        for(int i = 0; i < sessionGames.size(); i++){
            if (!sessionGames.get(i).isWin()){
                totalLosses++;
            }
        }
        return totalLosses;
    }


    public int getWinStreak(){
        int longestWinStreak = 0;
        int currentWinStreak = 0;
        for(int i = 0; i < sessionGames.size(); i++)
            if(sessionGames.get(i).isWin()){
                currentWinStreak++;
            } else {
                if(currentWinStreak > longestWinStreak){
                    longestWinStreak = currentWinStreak;
                    currentWinStreak = 0;
                }
            }
        if(currentWinStreak > longestWinStreak){
            longestWinStreak = currentWinStreak;
        }

        return longestWinStreak;
    }



    public String prettyString(){
        System.out.println("Hello:");
        System.out.println("Average guess number: " + getAverageGuesses());
        System.out.println("Letter Frequency:");
        HashMap<String, Integer> printLetter = getLetterGuessFrequency();
        for(String key : printLetter.keySet()){
            System.out.println("Letter: " + key + ": " + printLetter.get(key));
        }
        System.out.println("Number of Wins: " + getWins());
        System.out.println("Number of Losses: " + getLosses());
        System.out.println("Longest Win Streak: " + getWinStreak());
        return "";
    }
}
