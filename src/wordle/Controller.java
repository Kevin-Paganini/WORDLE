package wordle;

import Model.*;
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
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import static javafx.scene.input.KeyCode.*;

public class Controller {
    public static final boolean DEBUG = true;
    private static File dictionaryFile =  new File("src/Resources/wordle-official.txt");
    public static final double BUTTON_PADDING = 10;
    private int guess = 0;
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
    int numGuesses = 6;
    int numLetters = 5;
    boolean DARK = false;
    boolean CONTRAST = false;
    DialogPane win;

    private HashMap<String, Integer> letters_used_grid_colors;

    private Button statButton;

    private Session session;


    /**
     * Main Grid of the application
     */
    @FXML
    AnchorPane MAIN_PANE;

    @FXML
    Pane SETTINGS_PANE;

    @FXML
    Button importDictionaryButton;

    @FXML
    TextField numGuess;


    /**
     * Makes submit button to enter word
     */
    private Button submitButton;


    @FXML
    private Button dark_light;

    @FXML
    private Button contrast;

    @FXML
    private Button numChange;

    ArrayList<Button> buttons = new ArrayList<>();


    /**
     * Initialization function for window
     * Adding style sheets to specific elements
     * Creating various layouts
     * @author Kevin Paganini
     */
    @FXML
    public void initialize(){
      startNewGame();
      session = new Session();
    }

    /**
     * This used to be in initialize, this needs to happen everytime a new game is started
     *
     */
    public void startNewGame(){
        guess = 0;
        MAIN_PANE.getChildren().clear();
        gridOfTextFieldInputs.clear();
        // Creating Wordle Game

        try {
            //I have an idea solution for this - Atreyu
            BufferedReader brTest = new BufferedReader(new FileReader(dictionaryFile));
            String word = brTest.readLine();
            numLetters = word.length();
            game = new Wordle(numGuesses, numLetters, dictionaryFile);

        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
        } catch (NullPointerException e){
            //TODO: Catch if the opened dictionary file is blank
        }

        if(DEBUG) System.out.println(game.getTarget());

        letters_used_grid_colors = Utils.makeInitialHashMapForKeyBoardColors();


        //Creating grid of inputs
        grid_input = createGridOfInputs(numGuesses, numLetters);

        // Create keyboard of used letters
        letters_used = createKeyBoardInputs();
        letters_used.setLayoutX((numLetters * 60) + 100);
        letters_used.setLayoutY(50);
        letters_used.getStyleClass().add("keyBoardGrid");

        // Create Statistics button
        statButton = createStatisticsButton();
        submitButton = makeSubmitButton();


        // Adding all to main pane
        MAIN_PANE.getChildren().addAll(grid_input, letters_used, statButton, submitButton);
        buttons.add(submitButton);
        buttons.add(statButton);
        buttons.add(importDictionaryButton);
        buttons.add(dark_light);
        buttons.add(contrast);
        buttons.add(numChange);
        update_dark(DARK,CONTRAST);
        update_contrast(CONTRAST,DARK);
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
        button.setLayoutX((numLetters * 60) + 75);
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
     * @author David Kane
     */
    private void mouseClick(MouseEvent mouseEvent) {
        String letter = ((Label) mouseEvent.getSource()).getText().toUpperCase();
        if (letter == "DEL"){
          for (int i = numLetters-1; i >= 0; i--){
              TextField tf = gridOfTextFieldInputs.get(guess).get(i);
              if (!tf.getText().equals("")) {
                  tf.setText("");
                  tf.requestFocus();
                  i = 0;
              }
          }
        }

        else {
            for (int i = 0; i < numLetters; i++) {
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                if (tf.getText().equals("")) {
                    i = numLetters;
                        tf.setText(letter);
                    }
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
                tf.setMaxSize(50, 50);
                grid.add(tf, c, r);
                row.add(tf);
                if (r != 0){
                    tf.setDisable(true);
                }
            }
        }
        return grid;
    }

    /**
     * Makes submit button for grid
     * @author Kevin Paganini
     */
    private Button makeSubmitButton(){
        submitButton = new Button("Submit");
        submitButton.setOnAction(this:: submitButton);
        submitButton.setLayoutX((numLetters * 60) + 75);
        submitButton.setLayoutY(200);
        submitButton.setDisable(true);
        return submitButton;
    }


    /**
     * Called when submitButton is clicked
     * Calls submit because enter key performs the same action
     * @param actionEvent submit button being pressed
     */
    private void submitButton(ActionEvent actionEvent){
        submit();
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
    private void submit() {
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
        Utils.recolorTextFields(position, numLetters, gridOfTextFieldInputs, guess,CONTRAST);



        guess++;
        //If there is a guess, and it is right
        if(game.isWinner(input.toLowerCase(Locale.ROOT))){
            if (DEBUG) System.out.println("You Won!");
            win_streak++;
            wins++;
            win_percentage = Math.min(100, ((double)wins/(losses+wins)) * 100);
            showWinAlert();
        }
        //If there is a guess and it is wrong
        else if (guess != numGuesses){
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
        win = a.getDialogPane();
        win.getStylesheets().add("Styling//stylesheet.css");
        win.getStylesheets().add("Styling//dark_stylesheet.css");
        win.getStylesheets().add("Styling//contrast_stylesheet.css");
        //Did player win or lose
        win.getStyleClass().clear();
        if(win_streak == 0) {
            if(DARK && !CONTRAST) {
                win.getStyleClass().add("loser-dialog-dark");
            } else if(DARK && CONTRAST){
                win.getStyleClass().add("loser-dialog-dark-contrast");
            } else if(CONTRAST && !DARK) {
                win.getStyleClass().add("loser-dialog-contrast");
            } else {
                win.getStyleClass().add("loser-dialog");
            }
        } else {
            if(DARK && !CONTRAST) {
                win.getStyleClass().add("winner-dialog-dark");
            } else if(DARK && CONTRAST){
                win.getStyleClass().add("winner-dialog-dark-contrast");
            } else if(CONTRAST && !DARK) {
                win.getStyleClass().add("winner-dialog-contrast");
            } else {
                win.getStyleClass().add("winner-dialog");
            }
        }
        win.setHeaderText("Played = " + (wins + losses) + "\nWIN% = " + win_percentage + "%" + "\nGUESSES THIS GAME = " + guess + "\nWINSTREAK = " + win_streak);
        win.setContentText("PLAY AGAIN?");

        Optional<ButtonType> result = a.showAndWait();
        if (!result.isPresent()) {
            // alert is exited, no button has been pressed.
            Platform.exit();
        } else if (result.get() == ButtonType.OK) {
            //oke button is pressed
            startNewGame();
        } else if (result.get() == ButtonType.CANCEL){
            // cancel button is pressed
            Platform.exit();
        }
    }



    /**
     * Every time key is pressed
     * Getting text field values and making sure submit stays off
     * unless there is a valid word in the guess boxes
     * @author //TODO
     */
    private void getTextFieldValues(){


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
    /**
     * Recolors and styles the keyboard
     * @author Carson Meredith & Kevin Paganini
     * @param letter which letter in keyboard to style
     */
    private void colorAndStyleKeyboard(String letter) {
        Label box;
        int index = textFieldValues.indexOf(letter);
        box = (Label) letters_used.getChildren().get(index);
        box.getStyleClass().clear();
        box.getStyleClass().add(Utils.recolorLabel(letters_used_grid_colors.get(letter),CONTRAST));
    }
    /**
     * @author David Kane
     * Allows User to Navigate Boxes (and use Enter Key)
     * @param e keyEvent to trigger the function
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
                submit();
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
     * @author David Kane
     * Ensure only letters can be entered, and moves the boxes accordingly
     * @param: Observable e
     * @param: String oldValue
     * @param: String newValue
     * @param: int index
     * @return: void
     */
    private void listener(Observable e, String oldValue, String newValue, int index) {
        getTextFieldValues();
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

    /**
     * @author ?????? & David Kane
     * Will try and change the library for the user
     * @param actionEvent Button click (garbage value)
     */
    public void importDictionary(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File ("src/Resources/"));
        File temp;
        temp = fc.showOpenDialog(null);
        if (temp != null) {
            dictionaryFile = temp;
            startNewGame();
        }
    }

    /**
     * @author Carson Meredith
     * Changes from light to dark or dark to light
     * @param DARK DARK/LIGHT setting desired
     * @param CONTRAST CONTRAST/NORMAL setting desired
     */
    public void update_dark(boolean DARK, boolean CONTRAST) {
        if(DARK && CONTRAST) {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark-contrast");
            }
        } else if(CONTRAST && !DARK){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-contrast");
            }
        } else if(DARK && !CONTRAST){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark");
            }
        } else {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button");
            }
        }
        if(DARK){
            numGuess.getStyleClass().clear();
            numGuess.getStyleClass().add("text-field-dark");
            MAIN_PANE.getStyleClass().clear();
            MAIN_PANE.getStyleClass().add("pane-dark");
            SETTINGS_PANE.getStyleClass().clear();
            SETTINGS_PANE.getStyleClass().add("pane-dark");
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label)letters_used.getChildren().get(i);
                if(temp.getStyleClass().toString().equals("label")){
                    temp.getStyleClass().clear();
                    temp.getStyleClass().add("label-dark");
                }
            }
            for(int i = 0; i < grid_input.getChildren().size();++i){
                TextField tf = (TextField)grid_input.getChildren().get(i);
                if(tf.getStyleClass().toString().equals("text-input text-field") || tf.getStyleClass().toString().equals("text-field")) {
                    tf.getStyleClass().clear();
                    tf.getStyleClass().add("text-field-dark");
                }
            }
        } else if (!DARK){
            MAIN_PANE.getStyleClass().clear();
            MAIN_PANE.getStyleClass().add("pane");
            numGuess.getStyleClass().clear();
            numGuess.getStyleClass().add("text-field");
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label) letters_used.getChildren().get(i);
                if(temp.getStyleClass().toString().equals("label-dark")){
                    temp.getStyleClass().clear();
                    temp.getStyleClass().add("label");
                }
            }
            for(int i = 0; i < grid_input.getChildren().size();++i){
                TextField tf = (TextField)grid_input.getChildren().get(i);
                if(tf.getStyleClass().toString().equals("text-field-dark")) {
                    tf.getStyleClass().clear();
                    tf.getStyleClass().add("text-field");
                }
            }
            SETTINGS_PANE.getStyleClass().clear();
            SETTINGS_PANE.getStyleClass().add("pane");
        }
    }

    /**
     * @author Carson Meredith
     * Changes dark mode if dark button is pressed
     * @param actionEvent Button click
     */
    public void dark_light_mode_switch(ActionEvent actionEvent) {
        String text = dark_light.getText();
        DARK = text.equals("DARK-MODE");
        update_dark(DARK,CONTRAST);
        if(DARK){
            dark_light.setText("LIGHT-MODE");
        } else {
            dark_light.setText("DARK-MODE");
        }

    }

    /**
     * @author Carson Meredith
     * Changes Contrast mode if contrast button is pressed
     * @param actionEvent Button click
     */
    public void contrast_switch(ActionEvent actionEvent) {
        String text = contrast.getText();
        CONTRAST = text.equals("HIGH-CONTRAST-MODE");
        update_contrast(CONTRAST,DARK);
        if(CONTRAST){
            contrast.setText("NORMAL-MODE");
        } else {
            contrast.setText("HIGH-CONTRAST-MODE");
        }
    }
    /**
     * @author Carson Meredith
     * Changes from CONTRAST to NORMAL or NORMAL to CONTRAST
     * @param CONTRAST CONTRAST/NORMAL setting desired
     * @param DARK DARK/LIGHT setting desired
     */
    public void update_contrast(boolean CONTRAST, boolean DARK){
        if(CONTRAST && DARK) {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark-contrast");
            }
        } else if(CONTRAST && !DARK){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-contrast");
            }
        } else if(DARK && !CONTRAST){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark");
            }
        } else {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button");
            }
        }
        if(CONTRAST){
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label) letters_used.getChildren().get(i);
                switch (temp.getStyleClass().toString()) {
                    case "correct-position-letter-label":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("correct-position-letter-label-contrast");
                        break;
                    case "correct-letter-label":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("correct-letter-label-contrast");
                        break;
                    case "wrong-letter-label":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("wrong-letter-label-contrast");
                        break;
                }
            }
            for(int i = 0; i < grid_input.getChildren().size();++i){
                TextField tf = (TextField)grid_input.getChildren().get(i);
                switch (tf.getStyleClass().toString()) {
                    case "correct-position-letter-tf":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("correct-position-letter-tf-contrast");
                        break;
                    case "correct-letter-tf":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("correct-letter-tf-contrast");
                        break;
                    case "wrong-letter-tf":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("wrong-letter-tf-contrast");
                        break;
                }
            }
        } else if(!CONTRAST){
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label) letters_used.getChildren().get(i);
                switch (temp.getStyleClass().toString()) {
                    case "correct-position-letter-label-contrast":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("correct-position-letter-label");
                        break;
                    case "correct-letter-label-contrast":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("correct-letter-label");
                        break;
                    case "wrong-letter-label-contrast":
                        temp.getStyleClass().clear();
                        temp.getStyleClass().add("wrong-letter-label");
                        break;
                }
            }
            for(int i = 0; i < grid_input.getChildren().size();++i){
                TextField tf = (TextField)grid_input.getChildren().get(i);
                switch (tf.getStyleClass().toString()) {
                    case "correct-position-letter-tf-contrast":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("correct-position-letter-tf");
                        break;
                    case "correct-letter-tf-contrast":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("correct-letter-tf");
                        break;
                    case "wrong-letter-tf-contrast":
                        tf.getStyleClass().clear();
                        tf.getStyleClass().add("wrong-letter-tf");
                        break;
                }
            }
        }
    }

    /**
     * @author David Kane
     * Will change amount of guesses user is allowed if number entered is greater than 0
     * @param actionEvent Button click (garbage value)
     */
    public void changeGuessAmount(ActionEvent actionEvent){
        String guess = numGuess.getText();
        try {
            int num = Integer.parseInt(guess);
            if(num > 0) {
                numGuesses = num;
                numGuess.setText("");
                startNewGame();
            }
        } catch (NumberFormatException e){

        }
    }
}

