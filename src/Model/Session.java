package Model;

import java.io.File;
import java.util.ArrayList;

public class Session {
    public static final File STORAGE_FILE = new File("src/Resources/previousGuesses.txt");
    private final ArrayList<Wordle> sessionGames = new ArrayList<>();

    public Session (){

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
