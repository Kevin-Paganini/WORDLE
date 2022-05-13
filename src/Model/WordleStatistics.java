package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordleStatistics {
    /**
     * Returns the average number of guesses per winning guess for all guesses in the storage file
     * Does not count nor store guesses in the current buffer - this must be done elsewhere
     * @return double representing the average number of user guesses per winning guess.
     *      totalGuesses/numWins
     *      Returns -1 if the storage file does not exist
     *      Returns -2 if the storage file exists but does not contain any guess data
     *      Returns 0 if the user has no wins but does have guesses
     * @throws IOException IO error occurs
     */
    public double averageGuessesPerWin() throws IOException {
        if (!Session.STORAGE_FILE.exists()) return -1;

        String line;
        double totalGuesses = 0;
        double numWins = 0;
        BufferedReader br = new BufferedReader(new FileReader(Session.STORAGE_FILE));
        while ((line = br.readLine()) != null) {
            totalGuesses++;
            if (line.charAt(line.length() - 1) == '1') {
                numWins++;
            }
        }
        br.close();
        if (totalGuesses == 0 ) return -2;
        if (numWins == 0) return 0;

        return totalGuesses / numWins;
    }

    /**
     * Returns the number of wins over the total number of games
     * Does not count nor store guesses in the current buffer - this must be done elsewhere
     * @return double representing the ratio of winning guesses to winning & losing guesses
     *      numWins / (numWins + numLosses)
     *      Returns -1 if the storage file does not exist
     *      Returns -2 if there are no wins or losses
     * @throws IOException IO error occurs
     */
    public double winLossPercent() throws IOException {
        if (!Session.STORAGE_FILE.exists()) return -1;

        double numWins = 0, numLosses = 0;
        BufferedReader br = new BufferedReader(new FileReader(Session.STORAGE_FILE));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.charAt(line.length() - 1) == '1') numWins++;
            else if (line.charAt(line.length() - 1) == '0') numLosses++;
        }
        if (numLosses + numWins == 0) return -2;
        return numWins / (numLosses + numWins);
    }
    /**
     * Returns the current and highest win-streak as a double array.
     * Does not count nor store guesses in the current buffer - this must be done elsewhere
     * @return double array where the value at index 0 represents the current win-streak and
     *      the value at index 1 represents the highest win-streak
     *      Returns null if the storage file does not exist
     * @throws IOException IO error occurs
     */
    public int[] winStreak() throws IOException {
        if (!Session.STORAGE_FILE.exists()) return null;

        BufferedReader br = new BufferedReader(new FileReader(Session.STORAGE_FILE));
        int currentStreak = 0;
        int highestStreak = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.charAt(line.length() - 1) == '1') currentStreak++;
            else if (line.charAt(line.length() - 1) == '0') currentStreak = 0;
            highestStreak = Math.max(currentStreak, highestStreak);
        }
        return new int[]{currentStreak, highestStreak};
    }

}
