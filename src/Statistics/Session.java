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
        for(int i = 0; i < sessionGames.size(); i++){
           // total += sessionGames.get(i).getGuesses();
        }
        int total = 0;
        //sessionGames.stream().reduce(x -> );
        return 0;
    }

    public int getAvgGuesses(){
        return 0;
    }

    public int getWinStreak(){
        return 0;
    }
}
