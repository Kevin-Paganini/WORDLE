package wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.xml.soap.Text;
import java.io.File;
import java.util.*;

public class Controller {
    double BUTTON_PADDING = 10;
    private int guess = 0;
    private int numLetters;
    ArrayList<List<TextField>> gridOfTextFieldInputs = new ArrayList();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M");
    Wordle game = null;
    GridPane letters_used;
    GridPane grid_input;
    int position;

    /**
     * Main Grid of the application
     */
    @FXML
    AnchorPane MAIN_PANE;

    Button submitButton;


    /**
     * Initialization function for window
     * Adding style sheets to specific elements
     * Creating various layouts
     * @author: Kevin Paganini
     */
    @FXML
    public void initialize(){

        // Creeating Wordle Game
        game = new Wordle(6, 5, new File("src/Resources/wordle-official.txt"));



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
                grid.add(label, i % 10, 0);
            } else if (i < 19) {
                grid.add(label, (i - 10) % 9, 1);
            } else {
                grid.add(label, ((i - 19) % 7) + 1, 2);
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
                if (r != 0){
                    tf.setDisable(true);
                }
            }
        }

        //Creating Submit Button
        submitButton = new Button("Submit");
        submitButton.setOnAction(this:: submitButtonAction);
        grid.add(submitButton, numGuesses, numLetters / 2);
        submitButton.setDisable(true);
        return grid;
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
        guess++;

        // Disable text fields that were just enabled
        for(int i = 0; i < numLetters; i++){
            TextField tf = (TextField) gridOfTextFieldInputs.get(guess).get(i);
            tf.setDisable(false);
        }
        submitButton.setDisable(true);

        // Checking positions of guess against target
        int[] position = game.returnPositions(input.toLowerCase(Locale.ROOT));

        char letter;
        for(int i = 0; i < input.length(); ++i) {
            letter = input.charAt(i);
            checkLetter(letter);
        }
        //Checking if the user guessed correct word
        System.out.println("You are  " + game.isWinner(input.toLowerCase(Locale.ROOT)));
        for(int i : position){
            System.out.println(i);
            //We can play wordle now!!!!!!!!!!!!!!!!!!!
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

            TextField tf = (TextField) gridOfTextFieldInputs.get(guess).get(i);
            input += tf.getText();
            // I know this does something but I don't really know what, can someone explain please :) - Kevin
        }
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
    }


    private void checkLetter(char letter) {
        Label box;
        switch (letter){
            case 'Q':
                box = (Label) letters_used.getChildren().get(0);
                box.setDisable(true);
                break;
            case 'W':
                box = (Label) letters_used.getChildren().get(1);
                box.setDisable(true);
                break;
            case 'E':
                box = (Label) letters_used.getChildren().get(2);
                box.setDisable(true);
                break;
            case 'R':
                box = (Label) letters_used.getChildren().get(3);
                box.setDisable(true);
                break;
            case 'T':
                box = (Label) letters_used.getChildren().get(4);
                box.setDisable(true);
                break;
            case 'Y':
                box = (Label) letters_used.getChildren().get(5);
                box.setDisable(true);
                break;
            case 'U':
                box = (Label) letters_used.getChildren().get(6);
                box.setDisable(true);
                break;
            case 'I':
                box = (Label) letters_used.getChildren().get(7);
                box.setDisable(true);
                break;
            case 'O':
                box = (Label) letters_used.getChildren().get(8);
                box.setDisable(true);
                break;
            case 'P':
                box = (Label) letters_used.getChildren().get(9);
                box.setDisable(true);
                break;
            case 'A':
                box = (Label) letters_used.getChildren().get(10);
                box.setDisable(true);
                break;
            case 'S':
                box = (Label) letters_used.getChildren().get(11);
                box.setDisable(true);
                break;
            case 'D':
                box = (Label) letters_used.getChildren().get(12);
                box.setDisable(true);
                break;
            case 'F':
                box = (Label) letters_used.getChildren().get(13);
                box.setDisable(true);
                break;
            case 'G':
                box = (Label) letters_used.getChildren().get(14);
                box.setDisable(true);
                break;
            case 'H':
                box = (Label) letters_used.getChildren().get(15);
                box.setDisable(true);
                break;
            case 'J':
                box = (Label) letters_used.getChildren().get(16);
                box.setDisable(true);
                break;
            case 'K':
                box = (Label) letters_used.getChildren().get(17);
                box.setDisable(true);
                break;
            case 'L':
                box = (Label) letters_used.getChildren().get(18);
                box.setDisable(true);
                break;
            case 'Z':
                box = (Label) letters_used.getChildren().get(19);
                box.setDisable(true);
                break;
            case 'X':
                box = (Label) letters_used.getChildren().get(20);
                box.setDisable(true);
                break;
            case 'C':
                box = (Label) letters_used.getChildren().get(21);
                box.setDisable(true);
                break;
            case 'V':
                box = (Label) letters_used.getChildren().get(22);
                box.setDisable(true);
                break;
            case 'B':
                box = (Label) letters_used.getChildren().get(23);
                box.setDisable(true);
                break;
            case 'N':
                box = (Label) letters_used.getChildren().get(24);
                box.setDisable(true);
                break;
            case 'M':
                box = (Label) letters_used.getChildren().get(25);
                box.setDisable(true);
                break;
        }
    }

}
