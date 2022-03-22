package wordle;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static javafx.scene.input.KeyCode.*;

public class Controller {
    public static final boolean DEBUG = false;
    double BUTTON_PADDING = 10;
    private int guess = 0;
    private int numLetters;
    ArrayList<List<TextField>> gridOfTextFieldInputs = new ArrayList<>();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "DEL");
    Wordle game = null;
    GridPane letters_used;
    GridPane grid_input; // This has the submit button + grid of textfields FYI
    int position;
    int win_streak;
    int wins;
    int losses;
    double win_percentage;
    int NUM_GUESSES = 6;
    int NUM_LETTERS = 5;

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
        guess = 0;
        MAIN_PANE.getChildren().clear();
        gridOfTextFieldInputs.clear();
        // Creating Wordle Game
        try {
            game = new Wordle(NUM_GUESSES, NUM_LETTERS, new File("src/Resources/wordle-official.txt"));
        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
        }

        if(DEBUG){
            System.out.println(game.getTarget());
        }
        makeInitialHashMapForKeyBoardColors();

        MAIN_PANE.getStyleClass().add("pane");
        //Creating grid of inputs
        grid_input = createGridOfInputs(NUM_GUESSES, NUM_LETTERS);

        // Create keyboard of used letters
        letters_used = createKeyBoardInputs();
        letters_used.setLayoutX(400);
        letters_used.setLayoutY(50);
        letters_used.getStyleClass().add("keyBoardGrid");

        // Create Statistics button
        Button statButton = createStatisticsButton();


        // Adding all to main pane
        MAIN_PANE.getChildren().addAll(grid_input, letters_used, statButton);

    }

    /**
     * Statistics Button for later
     * @return Button for statistics
     * @author Kevin Paganini
     * //TODO Maybe pick a better icon
     */
    private Button createStatisticsButton() {

        Image image = new Image("file:src/Resources/stat.jpg", 30, 30, false, false);
        ImageView view = new ImageView(image);

        Button button = new Button();
        button.setPrefSize(30, 30);
        button.setGraphic(view);
        button.setOnAction(this::showStatistics);
        button.setLayoutX(410);
        button.setLayoutY(310);
        return button;
    }

    /**
     * Show statistics of user
     * @param actionEvent
     */
    private void showStatistics(ActionEvent actionEvent) {
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
            label.setOnMouseClicked(this:: mouseClick);
            if (i < 10) {
                grid.add(label, i % 10, 0); // First row of keyboard
            } else if (i < 19) {
                grid.add(label, (i - 10) % 9, 1); // Second Row of keyboard
            } else {
                grid.add(label, ((i - 19) % 7) + 1, 2); // Third Row of Keyboard

            }


        }

        Label del = new Label(textFieldValues.get(26));
        del.setMaxSize(100, 50);
        del.setMinSize(100, 50);
        del.setPrefSize(100, 50);
        del.setOnMouseClicked(this:: mouseClick);
        grid.add(del, 9, 2);
        return grid;
    }

    /**
     * Functionality for software Keyboard
     * @param mouseEvent When letter is clicked
     */
    private void mouseClick(MouseEvent mouseEvent) {
        String letter = ((Label) mouseEvent.getSource()).getText().toUpperCase();
        for(int i = 0; i < numLetters; i++){
            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            if(tf.isFocused()){
                tf.setText(letter);
                i = numLetters;
            }
        }

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
                int finalC = c;
                tf.textProperty().addListener((observable, oldValue, newValue) -> listener(observable, oldValue, newValue, finalC));
                tf.setOnKeyPressed(this:: keyPressed);
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
        submitButton.setOnAction(this:: submitButton);
        submitButton.setDisable(true);
    }


    /**
     * Called when submitButton is clicked
     * Calls submitButtonAction because enter key performs the same action
     * @param actionEvent submit button being pressed
     */
    private void submitButton(ActionEvent actionEvent){
        submitButtonAction();

    }

    /**
     * Action to be performed when submit is clicked
     * Functionality:
     * Getting input from guess fields
     * Disabling text fields that were enabled
     * Checking positions of guess aainst target
     *
     * @author David Kane & Carson Meredith & Kevin Paganini
     */
    private void submitButtonAction() {
        // do verification stuff

        //Getting input from guess text fields
        String input = "";
        for(int i = 0; i < numLetters; i++){
            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            tf.setDisable(true);
            input += tf.getText();
        }

        if (DEBUG) System.out.println(input);

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

        if (DEBUG) {
            for(int i : position) {
                System.out.println(i);
                //We can play wordle now!!!!!!!!!!!!!!!!!!!
            }
        }
        recolorTextFields(position);


        guess++;
        //If there is a guess, and it is right
        if(game.isWinner(input.toLowerCase(Locale.ROOT))){
            if (DEBUG) System.out.println("You Won!");
            win_streak++;
            wins++;
            win_percentage = ((double)wins/(losses+wins)) * 100;
            if(win_percentage > 100) {
                win_percentage = 100;
            }
            showWinAlert();
        }
        //If there is a guess and it is wrong
        else if (guess != NUM_GUESSES){
            if (DEBUG) System.out.println("Try Again!");
            if (DEBUG) System.out.println(game.getTarget());
            //enables text fields that are next
            for (int i = 0; i < numLetters; i++) {
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                tf.setDisable(false);
            }
            gridOfTextFieldInputs.get(guess).get(0).requestFocus();
        //If there is a guess and user is out of guesses
        } else {
            win_streak = 0;
            losses++;
            win_percentage = ((double)wins/(losses+wins)) * 100;
            if(win_percentage > 100) {
                win_percentage = 100;
            }
            showWinAlert();
        }
        submitButton.setDisable(true);


    }

    /**
     * Creates alert when user either wins or loses their game of wordle
     * Shows information on win streak, guesses made, and win percentage
     * @author Carson Merediith
     */
    private void showWinAlert() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Play Again");
        DialogPane d;
        d = a.getDialogPane();
        d.getStylesheets().add("Styling//stylesheet.css");
        //Did player win or lose
        if(win_streak == 0) {
            d.getStyleClass().add("loser-dialog");
        } else {
            d.getStyleClass().add("winner-dialog");
        }
        d.setHeaderText("Played = " + (wins + losses) + "\nWIN% = " + win_percentage + "%" + "\nGUESSES THIS GAME = " + guess + "\nWINSTREAK = " + win_streak);
        d.setContentText("PLAY AGAIN?");

        Optional<ButtonType> result = a.showAndWait();
        if (!result.isPresent()) {
            // alert is exited, no button has been pressed.
            Platform.exit();
        } else if (result.get() == ButtonType.OK) {
            //oke button is pressed
            initialize();
        } else if (result.get() == ButtonType.CANCEL){
            // cancel button is pressed
            Platform.exit();
        }
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
        //Disabling submit button if guess text fields are not a word in dictionary
        if(!game.isValidWord(input.toLowerCase(Locale.ROOT))){
            submitButton.setDisable(true);
        }
    }

    //monkaS

    /**
     * Recolors and styles the keyboard
     * @author Carson Meredith & Kevin Paganini
     * @param letter which letter in keyboard to style
     */
    private void colorAndStyleKeyboard(String letter) {
        Label box;
        switch (letter){
            case "Q":
                box = (Label) letters_used.getChildren().get(0);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "W":
                box = (Label) letters_used.getChildren().get(1);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "E":
                box = (Label) letters_used.getChildren().get(2);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "R":
                box = (Label) letters_used.getChildren().get(3);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "T":
                box = (Label) letters_used.getChildren().get(4);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "Y":
                box = (Label) letters_used.getChildren().get(5);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "U":
                box = (Label) letters_used.getChildren().get(6);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "I":
                box = (Label) letters_used.getChildren().get(7);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "O":
                box = (Label) letters_used.getChildren().get(8);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "P":
                box = (Label) letters_used.getChildren().get(9);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "A":
                box = (Label) letters_used.getChildren().get(10);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "S":
                box = (Label) letters_used.getChildren().get(11);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "D":
                box = (Label) letters_used.getChildren().get(12);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "F":
                box = (Label) letters_used.getChildren().get(13);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "G":
                box = (Label) letters_used.getChildren().get(14);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "H":
                box = (Label) letters_used.getChildren().get(15);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "J":
                box = (Label) letters_used.getChildren().get(16);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "K":
                box = (Label) letters_used.getChildren().get(17);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "L":
                box = (Label) letters_used.getChildren().get(18);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "Z":
                box = (Label) letters_used.getChildren().get(19);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "X":
                box = (Label) letters_used.getChildren().get(20);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "C":
                box = (Label) letters_used.getChildren().get(21);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "V":
                box = (Label) letters_used.getChildren().get(22);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "B":
                box = (Label) letters_used.getChildren().get(23);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "N":
                box = (Label) letters_used.getChildren().get(24);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
            case "M":
                box = (Label) letters_used.getChildren().get(25);
                box.getStyleClass().clear();
                box.getStyleClass().add(recolorLabel(letters_used_grid_colors.get(letter)));
                break;
        }
    }
    /**
     * @author David Kane
     * Allows User to Navigate Boxes (and use Enter Key)
     * @param e
     * @return void
     */
    private void keyPressed(KeyEvent e){
        TextField textField = (TextField) e.getSource();
        KeyCode keyCode = e.getCode();
        if(keyCode == ENTER){
            int pos = 0;
            for(int i = 0; i < gridOfTextFieldInputs.get(guess).size(); i++){
                TextField validator = gridOfTextFieldInputs.get(guess).get(i);
                if(validator == textField){
                    pos = i;
                }
            }
            if(pos+1 == gridOfTextFieldInputs.get(guess).size()){
                if(!submitButton.isDisabled())
                submitButtonAction();
            }
        }
        else if(keyCode == LEFT){
            int pos = 0;
            for(int i = 0; i < gridOfTextFieldInputs.get(guess).size(); i++){
                TextField validator = gridOfTextFieldInputs.get(guess).get(i);
                if(validator == textField){
                    pos = i;
                }
            }
            if (pos > 0){
                gridOfTextFieldInputs.get(guess).get(pos-1).requestFocus();
            }
            else{
                gridOfTextFieldInputs.get(guess).get(gridOfTextFieldInputs.get(guess).size() -1).requestFocus();
            }
        }
        else if (keyCode == RIGHT) {
            int pos = 0;
            for(int i = 0; i < gridOfTextFieldInputs.get(guess).size(); i++){
                TextField validator = gridOfTextFieldInputs.get(guess).get(i);
                if(validator == textField){
                    pos = i;
                }
            }
            if (pos + 1 < gridOfTextFieldInputs.get(guess).size()) {
                gridOfTextFieldInputs.get(guess).get(pos + 1).requestFocus();
            }
            else{
                gridOfTextFieldInputs.get(guess).get(0).requestFocus();
            }
        }
        else if (keyCode == BACK_SPACE){
            int pos = 0;
            for(int i = 0; i < gridOfTextFieldInputs.get(guess).size(); i++){
                TextField validator = gridOfTextFieldInputs.get(guess).get(i);
                if(validator == textField){
                    pos = i;
                }
            }
            if (textField.getText().equals("") && pos > 0){
                gridOfTextFieldInputs.get(guess).get(pos-1).setText("");
                gridOfTextFieldInputs.get(guess).get(pos-1).requestFocus();
            }
        }
    }

    /**
     * Which CSS class should the label have
     * @param isCorrectLetter
     * @return String (Correct CSS class name)
     * @author Kevin Paganini
     */
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

    /**
     * @author: David Kane
     * Ensure only letters can be entered, and moves the boxes accordingly
     * @param: Observable e
     * @param: String oldValue
     * @param: String newValue
     * @param: int index
     * @return: void
     */
    private void listener(Observable e, String oldValue, String newValue, int index) {
        TextField tf = gridOfTextFieldInputs.get(guess).get(index);
        if(!tf.getText().equals("")){
            tf.setText(String.valueOf(tf.getText().charAt(0)));
            if (!Character.isLetter(tf.getText().charAt(0))) {
                tf.setText("");
            } else {
                tf.setText(tf.getText().toUpperCase());
                if (index + 1 < gridOfTextFieldInputs.get(guess).size()) {
                    gridOfTextFieldInputs.get(guess).get(index + 1).requestFocus();
                }
            }
        }
        }
    }

