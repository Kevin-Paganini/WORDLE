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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Controller {
    double BUTTON_PADDING = 10;
    private int guess = 0;
    private int numLetters;
    ArrayList<List<TextField>> gridOfTextFieldInputs = new ArrayList();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M");
    Wordle game = null;

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

        // Creating Wordle Game
        try {
            game = new Wordle(6, 5, new File("src/Resources/wordle-official.txt"));
        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
        }


        MAIN_PANE.getStyleClass().add("pane");
        //Creating grid of inputs
        GridPane grid_input = createGridOfInputs(6, 5);

        // Create keyboard of used letters
        GridPane letters_used = createKeyBoardInputs();
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

            // Check if each box only has valid characters
            // Bad Way of doing it but good enough for now
            if(tf.getText().length() > 1){
                tf.setText(tf.getText(0,1));
            }
            // Check if each box is only one letter

            //
            if(tf.getText().equals("") || tf.getText().equals(" ") || tf.getText() == null){
              submitButton.setDisable(true);
            }

        }
        //Disabling submit button if guess text fields are not a word in dictionary
        if(!game.isValidWord(input.toLowerCase(Locale.ROOT))){
            submitButton.setDisable(true);
        }
    }

}
