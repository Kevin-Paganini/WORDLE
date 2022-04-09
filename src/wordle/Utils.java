package wordle;

import Model.Suggestions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class Utils {


    private static final List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "DEL");

    //int BUTTON_PADDING = 10;

    /**
     * Which style class should a key in keyboard get put in to
     * Key from keyboard gets new CSS class
     * @param position
     * @author Kevin Paganini
     */

    public static void recolorTextFields(int[] position, int numLetters, ArrayList<List<TextField>> gridOfTextFieldInputs, int guess, boolean CONTRAST, boolean HARD) {
        if(CONTRAST) {
            for(int i = 0; i < numLetters; i++){
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.getStyleClass().clear();
                if(HARD) tf.setText("");
                if (position[i] == 2) tf.getStyleClass().add("correct-position-letter-tf-contrast");
                if (position[i] == 1) tf.getStyleClass().add("correct-letter-tf-contrast");
                if (position[i] == 0) tf.getStyleClass().add("wrong-letter-tf-contrast");
            }
        } else {
            for(int i = 0; i < numLetters; i++){
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.getStyleClass().clear();
                if(HARD) tf.setText("");
                if (position[i] == 2) tf.getStyleClass().add("correct-position-letter-tf");
                if (position[i] == 1) tf.getStyleClass().add("correct-letter-tf");
                if (position[i] == 0) tf.getStyleClass().add("wrong-letter-tf");
            }
        }
    }



    /**
     * Which CSS class should the label have
     * @param isCorrectLetter
     * @return String (Correct CSS class name)
     * @author Kevin Paganini
     */

    public static String recolorLabel(int isCorrectLetter,boolean contrast, boolean DARK,boolean HARD){
        if (isCorrectLetter < 0 || isCorrectLetter > 3){
            throw new IndexOutOfBoundsException();
        }
        if(!HARD) {
            if (contrast) {
                if (isCorrectLetter == 0) {
                    return "wrong-letter-label-contrast";
                } else if (isCorrectLetter == 1) {
                    return "correct-letter-label-contrast";
                } else if (isCorrectLetter == 2) {
                    return "correct-position-letter-label-contrast";
                } else {
                    return null;
                }
            } else {
                if (isCorrectLetter == 0) {
                    return "wrong-letter-label";
                } else if (isCorrectLetter == 1) {
                    return "correct-letter-label";
                } else if (isCorrectLetter == 2) {
                    return "correct-position-letter-label";
                } else {
                    return null;
                }
            }
        } else if(DARK) {
            return "label-dark";
        } else {
            return "label";
        }
    }


    /**
     * Too Lazy to type it all so this is nice and succinct
     * @author Kevin Paganini
     */
    public static HashMap<String, Integer> makeInitialHashMapForKeyBoardColors() {
        HashMap<String, Integer> letters_used_grid_colors = new HashMap<>();
        for (String letter : textFieldValues){
            letters_used_grid_colors.put(letter, -1);
        }
        return letters_used_grid_colors;
    }

    /**
     * Didn't want to type it out either
     * Used in Session statistics
     * @return HashMap with frequencies initialized
     * @author Kevin Paganini
     */
    public static HashMap<String, Integer> intializeLetterFrequency() {
        HashMap<String, Integer> letters_used_grid_colors = new HashMap<>();
        for (String letter : textFieldValues){
            letters_used_grid_colors.put(letter, 0);
        }
        return letters_used_grid_colors;
    }


    /**
     * Sorting of a hashmap by value
     * @param wordFreq
     * @return sorted hashmap just do (for (String key: hashmap.keyset)
     * @author Kevin Paganini
     */
    public static HashMap<String, Integer> sortHashMapByValue(HashMap<String, Integer> wordFreq){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(wordFreq.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        //I'm skeptical this needs a deep copy
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for(Map.Entry <String, Integer> entry: list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Nice debug function / good for testing
     * @param dict
     * @author Kevin Paganini
     */
    public static void printHashMap(HashMap<String, Integer> dict){
        for(String key : dict.keySet()){
            System.out.println("key: " + key + ", Value: " + dict.get(key));
        }
    }

    /**
     * Make 5 bar chart with whatever hashmap passed into it as long as value is a number
     * @param dict
     * @return barchart
     * @author Kevin Paganini
     */
    public static BarChart<String, Number> make5BarChartFromHashMap(HashMap<String, Integer> dict){
        CategoryAxis x = new CategoryAxis();
        String [] freqWords = dict.keySet().toArray(new String[0]);
        if (freqWords.length > 5){
            freqWords = Arrays.copyOfRange(freqWords, 0, 5);

        }

        x.setCategories(FXCollections.<String>observableArrayList(freqWords));


        x.setLabel("Word");

        NumberAxis y = new NumberAxis();
        y.setLabel("Frequency");
        BarChart<String, Number> bc = new BarChart(x, y);
        XYChart.Series<String, Number> series = new XYChart.Series();
        for(int i = 0; i < freqWords.length; i++){

            series.getData().add(new XYChart.Data<>(freqWords[i], dict.get(freqWords[i])));
        }

        bc.getData().add(series);
        return bc;
    }

    /**
     * Make the letter bar chart
     * @param dict
     * @return Barchart letter freq
     * @author Kevin Paganini
     */
    public static BarChart<String, Number> makeLetterBarChart(HashMap<String, Integer> dict){
        CategoryAxis x = new CategoryAxis();
        String [] freqWords = dict.keySet().toArray(new String[0]);

        x.setCategories(FXCollections.observableArrayList(freqWords));


        x.setLabel("Letter");
        NumberAxis y = new NumberAxis();
        y.setLabel("Frequency");
        BarChart<String, Number> bc = new BarChart<>(x, y);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (String freqWord : freqWords) {
            series.getData().add(new XYChart.Data<>(freqWord, dict.get(freqWord)));
        }
        bc.getData().add(series);
        bc.setBarGap(10);

        return bc;
    }

    /**
     * Makes suggestions gridpane
     * @param s
     * @return gridpane for suggestions
     * @author: Kevin Paganini
     */

    public static GridPane makeSuggestionsGrid(Suggestions s) {

        GridPane grid = new GridPane();
        Label suggestions = new Label("Suggestions:");
        grid.add(suggestions, 0, 2);
        grid.setHgap(10);
        ArrayList<String> x = new ArrayList<>(s.pruneDictionary());
        int loopTo = Math.min(5, x.size());
        for(int i = 0; i < loopTo; i++){
            Label word = new Label(String.valueOf(x.get(i)));

            grid.add(word, i+2, 2);
        }
        return grid;
    }

    public static ArrayList<String> readScoreboard(){
        ArrayList<String> score = new ArrayList<>();
        String text = "";
        try {
            File scores = new File("src/Resources/UserData/Scoreboard");
            BufferedReader br = new BufferedReader(new FileReader(scores));
            String line = br.readLine();
            while (line != null){
                score.add(line);
                line = br.readLine();
            }
        }catch (IOException e) {
            System.out.println("Scoreboard could not be read");
        }
        return score;
    }

    public static void saveScoreboard(ArrayList<String> scores, VBox Scoreboard, int numLetters, boolean HARD){
        Collections.sort(scores, Utils::sortScoreboard);
        String text = "";
        for(String line : scores){
            text += line + "\n";
        }
        try {
            Files.write(Paths.get("src/Resources/UserData/Scoreboard"),text.getBytes());
        } catch(IOException e) {
            System.out.println("aloha");
        }
        updateScoreboard(scores,Scoreboard, numLetters, HARD);
    }

    public static int sortScoreboard(String s1, String s2) {
        double first = Double.parseDouble(s1.substring(s1.lastIndexOf(",") + 1, s1.lastIndexOf(";")));
        double second = Double.parseDouble(s2.substring(s2.lastIndexOf(",") + 1, s2.lastIndexOf(";")));
        if(first < second){
            return -1;
        } else if (first == second){
            double guess1 = Double.parseDouble(s1.substring(s1.lastIndexOf(";") + 1,s1.lastIndexOf(":")));
            double guess2 = Double.parseDouble(s2.substring(s2.lastIndexOf(";") + 1,s2.lastIndexOf(":")));
            return Double.compare(guess1, guess2);
        } else {
            return 1;
        }
    }

    public static VBox updateScoreboard(ArrayList<String> scores, VBox Scoreboard, int numLetters, boolean HARD) {
        int total = 0;
        int dif1 = 0;
        if(HARD) {
            dif1 = 1;
        }
        for(int i =0; i < scores.size(); ++i){
            String score = scores.get(i);
            int letters = Integer.parseInt(score.substring(score.lastIndexOf(":") + 1, score.lastIndexOf("/")));
            int dif2 = Integer.parseInt(score.substring(score.lastIndexOf("/") + 1));
            if(total < 10 && letters == numLetters && dif1 == dif2) {
                Label temp = (Label)Scoreboard.getChildren().get(total + 1);
                double time = Double.parseDouble(score.substring(score.lastIndexOf(",") + 1, score.lastIndexOf(";")));
                int guesses = Integer.parseInt(score.substring(score.lastIndexOf(";") + 1,score.lastIndexOf(":")));
                temp.setText(score.substring(0, score.indexOf(",")) + ": " + time + "/" + guesses);
                total++;
            }
        }
        return Scoreboard;
    }
}
