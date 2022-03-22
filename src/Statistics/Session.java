package Statistics;

import wordle.Wordle;

import java.util.ArrayList;

public class Session {
    private ArrayList<Wordle> sessionGames;

    public Session (){
        sessionGames  = new ArrayList<>();

    }

    public void addGame(Wordle wordle){
        sessionGames.add(wordle);
    }

    public int averageGuesses(){
        int total = 0;
        //sessionGames.stream().reduce(x -> );
        return 0;
    }

    public int getAvgGuesses(){
        return 0;
    }
}
