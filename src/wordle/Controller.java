package wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Controller {
    double BUTTON_PADDING = 10;
    private int guess = 0;
    private int numLetters;
    ArrayList<List<TextField>> gridOfTextFieldInputs = new ArrayList<>();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M");
    Wordle game = null;
    GridPane letters_used;
    GridPane grid_input; // This has the submit button + grid of textfields FYI Has to do with one of the bugs were facing I believe
    int position;

    private HashMap<String, Integer> letters_used_grid_colors = new HashMap<>();


    /**
     * Main Grid of the application
     */
    @FXML
    AnchorPane MAIN_PANE;

    /**
     * Makes submit button to enter word
     */
    private Button submitButton;


    /**
     * Initialization function for window
     * Adding style sheets to specific elements
     * Creating various layouts
     * @author Kevin Paganini
     */
    @FXML
    public void initialize(){

        // Creating Wordle Game
        try {
            game = new Wordle(6, 5, new File("src/Resources/wordle-official.txt"));
        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
        }
        makeInitialHashMapForKeyBoardColors();

        MAIN_PANE.getStyleClass().add("pane");
        //Creating grid of inputs
        grid_input = createGridOfInputs(6, 5);

        // Create keyboard of used letters
        letters_used = createKeyBoardInputs();
        letters_used.setLayoutX(400);
        letters_used.setLayoutY(50);
        letters_used.getStyleClass().add("keyBoardGrid");

        // Adding all to main pane
        MAIN_PANE.getChildren().addAll(grid_input, letters_used);

    }

    /**
     * Too Lazy to type it all so this is nice and succinct
     * @author Kevin Paganini
     */
    private void makeInitialHashMapForKeyBoardColors() {

        for (String letter : textFieldValues){
            letters_used_grid_colors.put(letter, -1);
        }
    }

    /**
     * Creating keyboard replica to display to user what has been chosen
     * and what is right
     * @return grid of keyboard
     * @author Kevin Paganini
     */
    private GridPane createKeyBoardInputs() {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(BUTTON_PADDING));
        grid.setHgap(BUTTON_PADDING);
        grid.setVgap(BUTTON_PADDING);
        for(int i = 0; i < 26; i++) {
            Label label = new Label(textFieldValues.get(i));
            label.setMaxSize(50, 50);
            label.setMinSize(50, 50);
            label.setPrefSize(50, 50);
            if (i < 10) {
                grid.add(label, i % 10, 0); // First row of keyboard
            } else if (i < 19) {
                grid.add(label, (i - 10) % 9, 1); // Second Row of keyboard
            } else {
                grid.add(label, ((i - 19) % 7) + 1, 2); // Third Row of Keyboard
            }
        }
        return grid;
    }


    /**
     *
     * Create grid of inputFields for words
     * @param numGuesses (Number of guesses)
     * @param numLetters (NUmber of letters being used for words)
     * @return Gridpane of inputs + filled out arrayList called gridOfTextFieldInputs
     * @author Kevin Paganini
     */
    private GridPane createGridOfInputs(int numGuesses, int numLetters){
        this.numLetters = numLetters;
        if (gridOfTextFieldInputs.size() != 0){
            gridOfTextFieldInputs = new ArrayList();
        }
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(BUTTON_PADDING));
        grid.setHgap(BUTTON_PADDING);
        grid.setVgap(BUTTON_PADDING);

        for (int r = 0; r < numGuesses; r++) {
            ArrayList<TextField> row = new ArrayList();
            gridOfTextFieldInputs.add(row);
            for (int c = 0; c < this.numLetters; c++) {
                TextField tf = new TextField();
                tf.setOnKeyReleased(this:: getTextFieldValues);
                tf.setMaxSize(50, 50);
                grid.add(tf, c, r);
                row.add(tf);
                if (r != 0) {
                    tf.setDisable(true);
                }
            }
        }

        //Creating Submit Button
        makeSubmitButton();
        grid.add(submitButton, numGuesses, numLetters / 2);
        return grid;
    }

    /**
     * Makes submit button for grid
     * @author Kevin Paganini
     */
    private void makeSubmitButton(){
        submitButton = new Button("Submit");
        submitButton.setOnAction(this:: submitButtonAction);
        submitButton.setDisable(true);
    }

    /**
     * Action to be performed when submit is clicked
     * Functionality:
     * Getting input from guess fields
     * Disabling text fields that were enabled
     * Checking positions of guess aainst target
     *
     * @param actionEvent
     * @author //TODO
     */
    private void submitButtonAction(ActionEvent actionEvent) {
        // do verification stuff

        //Getting input from guess text fields
        String input = "";
        for(int i = 0; i < numLetters; i++){
            TextField tf = (TextField) gridOfTextFieldInputs.get(guess).get(i);
            tf.setDisable(true);
            input += tf.getText();
        }
        System.out.println(input);

        // Checking positions of guess against target
        int[] position = game.returnPositions(input.toLowerCase(Locale.ROOT));


        String letter;
        for(int i = 0; i < input.length(); ++i) {
            letter = String.valueOf(input.charAt(i));
            int isCorrectValue = position[i];

            if (letters_used_grid_colors.get(letter) < isCorrectValue){ // Checks if value stored is smaller than value achieved
                letters_used_grid_colors.replace(letter, isCorrectValue); // If guess has higher value replaces old value
            }
            colorAndStyleKeyboard(letter);
        }
        //Checking if the user guessed correct word

        for(int i : position){
            System.out.println(i);
            //We can play wordle now!!!!!!!!!!!!!!!!!!!

        }
        recolorTextFields(position);


        guess++;
        if(game.isWinner(input.toLowerCase(Locale.ROOT))){
            System.out.println("You Won!");
        }
        else {
            System.out.println("Try Again!");
            //enables text fields that are next
            for (int i = 0; i < numLetters; i++) {
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.setDisable(false);
            }
        }
        submitButton.setDisable(true);

    }

    /**
     * Which style class should a key in keyboard get put in to
     * Key from keyboard gets new CSS class
     * @param position
     */
    private void recolorTextFields(int[] position) {
        for(int i = 0; i < numLetters; i++){
            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            if (position[i] == 2){
                tf.getStyleClass().clear();
                tf.getStyleClass().add("correct-position-letter-tf");
            }
            if (position[i] == 1){
                tf.getStyleClass().clear();
                tf.getStyleClass().add("correct-letter-tf");
            }
            if (position[i] == 0){
                tf.getStyleClass().clear();
                tf.getStyleClass().add("wrong-letter-tf");
            }
        }

    }

    /**
     * Every time key is pressed
     * Getting text field values and making sure submit stays off
     * unless there is a valid word in the guess boxes
     * @param keyEvent
     * @author //TODO
     */
    private void getTextFieldValues(KeyEvent keyEvent){

        //TODO Make sure illegal character like numbers or punctuation don't get inputted


        submitButton.setDisable(false);
        String input = "";
        for(int i = 0; i < numLetters; i++){

            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            input += tf.getText();


        }

        //TODO We could make a validation class to simplify controller
        //The code below could go in there

        //Gets the value of the character being input
        TextField textField = (TextField) grid_input.getChildren().get(position);
        String letter = textField.getText().toUpperCase();
        List<String> numbers = Arrays.asList("0","1","2","3","4","5","6","7","8","9");
        //if letter is a number, removes it
        if (numbers.contains(letter)){
            textField.setText("");
            //If input is letter moves to next box
        } else if (textFieldValues.contains(letter)) {
            textField = (TextField) grid_input.getChildren().get(position);
            textField.setText(letter);
            position += 1;
            TextField textField2 = (TextField) grid_input.getChildren().get(position);
            textField2.requestFocus();
        }
        //If backspace is typed, does it
        if(letter.isEmpty()) {
            if(position - 1 >= 0) {
                position -= 1;
                textField = (TextField) grid_input.getChildren().get(position);
                textField.setText("");
                textField.requestFocus();
            }

        }
        //Disabling submit button if guess text fields are not a word in dictionary
        if(!game.isValidWord(input.toLowerCase(Locale.ROOT))){
            submitButton.setDisable(true);
        }
        //Disabling submit button if guess text fields are not a word in dictionary
        if(!game.isValidWord(input.toLowerCase(Locale.ROOT))){
            submitButton.setDisable(true);
        }
    }

    //monkaS

    /**
     * Recolors and styles the keyboard
     *
     * @param letter which letter in keyboard to style
     */
    private void colorAndStyleKeyboard(String letter) {
        Label box;
        switch (letter){
            case "Q":
                box = (Label) letters_used.getChildren().get(0);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "W":
                box = (Label) letters_used.getChildren().get(1);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "E":
                box = (Label) letters_used.getChildren().get(2);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "R":
                box = (Label) letters_used.getChildren().get(3);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "T":
                box = (Label) letters_used.getChildren().get(4);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "Y":
                box = (Label) letters_used.getChildren().get(5);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "U":
                box = (Label) letters_used.getChildren().get(6);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "I":
                box = (Label) letters_used.getChildren().get(7);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "O":
                box = (Label) letters_used.getChildren().get(8);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "P":
                box = (Label) letters_used.getChildren().get(9);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "A":
                box = (Label) letters_used.getChildren().get(10);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "S":
                box = (Label) letters_used.getChildren().get(11);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "D":
                box = (Label) letters_used.getChildren().get(12);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "F":
                box = (Label) letters_used.getChildren().get(13);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "G":
                box = (Label) letters_used.getChildren().get(14);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "H":
                box = (Label) letters_used.getChildren().get(15);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "J":
                box = (Label) letters_used.getChildren().get(16);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "K":
                box = (Label) letters_used.getChildren().get(17);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "L":
                box = (Label) letters_used.getChildren().get(18);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "Z":
                box = (Label) letters_used.getChildren().get(19);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "X":
                box = (Label) letters_used.getChildren().get(20);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "C":
                box = (Label) letters_used.getChildren().get(21);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "V":
                box = (Label) letters_used.getChildren().get(22);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "B":
                box = (Label) letters_used.getChildren().get(23);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "N":
                box = (Label) letters_used.getChildren().get(24);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "M":
                box = (Label) letters_used.getChildren().get(25);
                box.setDisable(true);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
        }
    }

    private String recolorLabel(int isCorrectLetter){
        if (isCorrectLetter < 0 || isCorrectLetter > 3){
            throw new IndexOutOfBoundsException();
        }
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
