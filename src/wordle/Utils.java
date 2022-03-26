package wordle;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.*;

public abstract class Utils {


    private static final List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "DEL");

    int BUTTON_PADDING = 10;
    /**
     * Which style class should a key in keyboard get put in to
     * Key from keyboard gets new CSS class
     * @param position
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

    public static HashMap<String, Integer> makeInitialHashMapForLetterFrequency() {
        HashMap<String, Integer> letters_used_grid_colors = new HashMap<>();
        for (String letter : textFieldValues){
            letters_used_grid_colors.put(letter, 0);
        }
        return letters_used_grid_colors;
    }



    public static HashMap<String, Integer> sortHashMapByValue(HashMap<String, Integer> wordFreq){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(wordFreq.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Map.Entry <String, Integer> entry: list){
            sortedMap.put(entry.getKey(), entry.getValue());

        }
        return sortedMap;
    }

    public static void printHashMap(HashMap<String, Integer> dict){
        for(String key : dict.keySet()){
            System.out.println("key: " + key + ", Value: " + dict.get(key));
        }
    }



}
