package wordle;

import Model.Suggestions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.*;

public abstract class Utils {


    private static final List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "DEL");

    int BUTTON_PADDING = 10;
    /**
     * Which style class should a key in keyboard get put in to
     * Key from keyboard gets new CSS class
     * @param position
     * @author Kevin Paganini
     */

    public static void recolorTextFields(int[] position, int numLetters, ArrayList<List<TextField>> gridOfTextFieldInputs, int guess, boolean CONTRAST) {
        if(CONTRAST) {
            for(int i = 0; i < numLetters; i++){
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.getStyleClass().clear();
                if (position[i] == 2) tf.getStyleClass().add("correct-position-letter-tf-contrast");
                if (position[i] == 1) tf.getStyleClass().add("correct-letter-tf-contrast");
                if (position[i] == 0) tf.getStyleClass().add("wrong-letter-tf-contrast");
            }
        } else {
            for(int i = 0; i < numLetters; i++){
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.getStyleClass().clear();
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

    public static String recolorLabel(int isCorrectLetter,boolean contrast){
        if (isCorrectLetter < 0 || isCorrectLetter > 3){
            throw new IndexOutOfBoundsException();
        }
        if(contrast) {
            if (isCorrectLetter == -1){
                return "label";
            }
            if (isCorrectLetter == 0){
                return "wrong-letter-label-contrast";
            }
            else if (isCorrectLetter == 1){
                return "correct-letter-label-contrast";
            }
            else if (isCorrectLetter == 2){
                return "correct-position-letter-label-contrast";
            }
            else {
                return null;
            }
        } else {
            if (isCorrectLetter == -1){
                return "label";
            }
            if (isCorrectLetter == 0){
                return "wrong-letter-label";
            }
            else if (isCorrectLetter == 1){
                return "correct-letter-label";
            }
            else if (isCorrectLetter == 2){
                return "correct-position-letter-label";
            }
            else {
                return null;
            }
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

        x.setCategories(FXCollections.<String>observableArrayList(freqWords));


        x.setLabel("Letter");
        NumberAxis y = new NumberAxis();
        y.setLabel("Frequency");
        BarChart<String, Number> bc = new BarChart(x, y);
        XYChart.Series<String, Number> series = new XYChart.Series();
        for(int i = 0; i < freqWords.length; i++){

            series.getData().add(new XYChart.Data<>(freqWords[i], dict.get(freqWords[i])));

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
        grid.add(suggestions, 0, 0);
        Set<String> noWrongLetters = s.returnPossibleWords();
        ArrayList<String> x = new ArrayList<>();
        x.addAll(noWrongLetters);
        int loopTo = Math.min(5, x.size());
        for(int i = 0; i < loopTo; i++){
            Label word = new Label(String.valueOf(x.get(i)));

            grid.add(word, i+1, 2);
        }
        return grid;
    }
}
