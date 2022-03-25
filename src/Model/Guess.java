package Model;

public class Guess {
    private final String guess;
    private final boolean isLoss;
    private final boolean isWin;

    public Guess(String guess, boolean isLoss, boolean isWin) {
        this.guess = guess;
        this.isLoss = isLoss;
        this.isWin = isWin;
    }

    public String getGuess() {
        return guess;
    }

    public boolean isLoss() {
        return isLoss;
    }

    public boolean isWin() {
        return isWin;
    }

}
