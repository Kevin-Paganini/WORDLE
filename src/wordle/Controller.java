package wordle;

import Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
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
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
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
    double avgGuesses = 0;
    boolean DARK = false;
    boolean CONTRAST = false;
    boolean SUGGESTION = false;
    boolean HARD = false;
    boolean RUNNING = false;
    ArrayList<String> guesses = new ArrayList<>();
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
    AnchorPane STATS_PANE;

    @FXML
    Button importDictionaryButton;

    @FXML
    Button suggestion;



    // Statistics Functionality
    @FXML
    TextField numGuess;

    @FXML
    Label winLabel;

    @FXML
    Label lossLabel;

    @FXML
    Label avgNumGuesses;

    @FXML
    Label longestWinStreak;

    @FXML
    Label winLabel2;

    @FXML
    Label lossLabel2;

    @FXML
    Label avgNumGuesses2;

    @FXML
    Label longestWinStreak2;

    @FXML
    Pane frequentWordPane;

    @FXML
    Pane frequentLetterPane;

    @FXML
    Button hard_mode;


    Suggestions suggest;



    /**
     * Makes submit button to enter word
     */
    private Button submitButton;

    private GridPane SUGGESTIONS;

    @FXML
    Label timer;


    @FXML
    private Button dark_light;

    @FXML
    private Button contrast;

    @FXML
    private Button numChange;

    @FXML
    private Button fiveLetterDictionary;

    @FXML
    private Button sixLetterDictionary;

    @FXML
    private Button sevenLetterDictionary;

    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<Pane> panes = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();
    ArrayList<TextField> textFields = new ArrayList<>();
    Timeline timeline;

    /**
     * Initialization function for window
     * Adding style sheets to specific elements
     * Creating various layouts
     * @author Kevin Paganini
     */
    @FXML
    public void initialize(){
        timeline = new Timeline(new KeyFrame(Duration.millis(100),(e)->{
            increaseTimer();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        session = Session.getSession();
        startNewGame();
        openStats();
    }

    /**
     * This used to be in initialize, this needs to happen everytime a new game is started
     *
     */
    public void startNewGame(){
        RUNNING = false;
        guess = 0;
        MAIN_PANE.getChildren().clear();
        gridOfTextFieldInputs.clear();
        // Creating Wordle Game

        try {

            suggest = new Suggestions();
            game = new Wordle(numGuesses, dictionaryFile, session, suggest);
            numLetters = game.getNumLetters();
            suggest.addGame(game);
            if(DEBUG) System.out.println(game.getTarget());

            letters_used_grid_colors = Utils.makeInitialHashMapForKeyBoardColors();


            //Creating grid of inputs
            grid_input = createGridOfInputs(numGuesses, numLetters);
            grid_input.setLayoutX(350);
            grid_input.setLayoutY(50);

            // Create keyboard of used letters
            letters_used = createKeyBoardInputs();
            letters_used.setLayoutX(200);
            letters_used.setLayoutY((numGuesses * 60) + 100);
            letters_used.getStyleClass().add("keyBoardGrid");

            // Create Statistics button
            statButton = createStatisticsButton();
            statButton.setLayoutX(200);
            statButton.setLayoutY(50);
            submitButton = makeSubmitButton();
            submitButton.setLayoutY(50);
            submitButton.setLayoutX(750);

            SUGGESTIONS = new GridPane();
            SUGGESTIONS.setLayoutX(130);
            SUGGESTIONS.setLayoutY((numGuesses*60)+300);
            timer = new Label();
            timer.setText("0");



            // Adding all to main pane
            MAIN_PANE.getChildren().addAll(grid_input, letters_used, statButton, submitButton, SUGGESTIONS, timer);
            //Add all buttons to list of buttons
            buttons.add(submitButton);
            buttons.add(statButton);
            buttons.add(importDictionaryButton);
            buttons.add(dark_light);
            buttons.add(contrast);
            buttons.add(numChange);
            buttons.add(fiveLetterDictionary);
            buttons.add(sixLetterDictionary);
            buttons.add(sevenLetterDictionary);
            buttons.add(suggestion);
            buttons.add(hard_mode);

            //Add all panes to list of panes
            panes.add(SETTINGS_PANE);
            panes.add(MAIN_PANE);
            panes.add(STATS_PANE);
            panes.add(frequentLetterPane);
            panes.add(frequentWordPane);

            //Add all labels to list of labels
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label)letters_used.getChildren().get(i);
                labels.add(temp);
            }
            labels.add(winLabel);
            labels.add(longestWinStreak);
            labels.add(lossLabel);
            labels.add(avgNumGuesses);
            labels.add(winLabel2);
            labels.add(longestWinStreak2);
            labels.add(lossLabel2);
            labels.add(avgNumGuesses2);
            labels.add(timer);

            //Add all textfields to list of textfields
            for(int i = 0; i < grid_input.getChildren().size();++i){
                TextField tf = (TextField)grid_input.getChildren().get(i);
                textFields.add(tf);
            }
            textFields.add(numGuess);

            StylingChanger.update_dark(DARK,CONTRAST,buttons,panes,labels,textFields);
            StylingChanger.update_contrast(DARK,CONTRAST,buttons,panes,labels,textFields);
            runTimer();
        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
            System.out.println("Entered an invalid File");

            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Invalid File");
            win = a.getDialogPane();
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
            win.setHeaderText("INVALID FILE");
            win.setContentText("Please enter a valid file.");
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,0);


            Optional<ButtonType> result = a.showAndWait();
            if (!result.isPresent()) {
                // alert is exited, no button has been pressed.
                session.prettyString();
                Platform.exit();
            } else if (result.get() == ButtonType.OK) {
                //oke button is pressed
                importDictionary();
                startNewGame();
            } else if (result.get() == ButtonType.CANCEL){
                // cancel button is pressed
                session.prettyString();
                Platform.exit();
            }


        } catch (NullPointerException e){
            //TODO: Catch if the opened dictionary file is blank

        }


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
        enterLetter(letter);
    }

    private void enterLetter(String letter){
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

    public void enterSuggestion(MouseEvent event){
        String word = ((Label) event.getSource()).getText().toUpperCase();
        for(int i = 0; i < numLetters; ++i) {
            gridOfTextFieldInputs.get(guess).get(i).setText("");
        }
        for(int i = 0; i < word.length(); ++i) {
            enterLetter(Character.toString(word.charAt(i)));
        }
        submit();
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
     * Checking positions of guess against target
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

        guesses.add(input);
        game.updateGuesses(guesses);

        // Checking positions of guess against target
        int[] position = game.makeGuess(input.toLowerCase(Locale.ROOT));


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
        Utils.recolorTextFields(position, numLetters, gridOfTextFieldInputs, guess,CONTRAST, HARD);



        guess++;
        updateSuggestions();
        //If there is a guess, and it is right
        if(game.isWinner(input.toLowerCase(Locale.ROOT))){
            if (DEBUG) System.out.println("You Won!");
            win_streak++;
            wins++;
            win_percentage = Math.min(100, ((double)wins/(losses+wins)) * 100);
            saveStats();
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
            saveStats();
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
        runTimer();
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Play Again");
        win = a.getDialogPane();
        StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
        win.setHeaderText("Played = " + (wins + losses) + "\nWIN% = " + win_percentage + "%" + "\nGUESSES THIS GAME = " + guess + "\nWINSTREAK = " + win_streak + "\nTIME: = " + timer.getText());
        win.setContentText("PLAY AGAIN?");
        // Updating stats tab every time a game is done
        updateStats();

        Optional<ButtonType> result = a.showAndWait();
        if (!result.isPresent()) {
            // alert is exited, no button has been pressed.
            session.prettyString();
            Platform.exit();
        } else if (result.get() == ButtonType.OK) {
            //oke button is pressed
            startNewGame();
        } else if (result.get() == ButtonType.CANCEL){
            // cancel button is pressed
            session.prettyString();
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
        box.getStyleClass().add(Utils.recolorLabel(letters_used_grid_colors.get(letter),CONTRAST,DARK, HARD));
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
                if(!submitButton.isDisabled())
                submit();
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
        runTimer();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File ("src/Resources/"));
        File temp;
        temp = fc.showOpenDialog(null);
        if (temp != null) {
            dictionaryFile = temp;
            startNewGame();
        }
    }

    public void importDictionary(){
        runTimer();
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
     * Changes dark mode if dark button is pressed
     * @param actionEvent Button click
     */
    public void dark_light_mode_switch(ActionEvent actionEvent) {
        String text = dark_light.getText();
        DARK = text.equals("DARK-MODE");
        StylingChanger.update_dark(DARK,CONTRAST,buttons,panes,labels,textFields);
        if(DARK){
            dark_light.setText("LIGHT-MODE");
        } else {
            dark_light.setText("DARK-MODE");
        }
        saveStats();
    }

    /**
     * @author Carson Meredith
     * Changes Contrast mode if contrast button is pressed
     * @param actionEvent Button click
     */
    public void contrast_switch(ActionEvent actionEvent) {
        String text = contrast.getText();
        CONTRAST = text.equals("HIGH-CONTRAST-MODE");
        StylingChanger.update_contrast(DARK,CONTRAST,buttons,panes,labels,textFields);
        if(CONTRAST){
            contrast.setText("NORMAL-MODE");
        } else {
            contrast.setText("HIGH-CONTRAST-MODE");
        }
        saveStats();
    }

    /**
     * @author David Kane
     * Changes Disables/Enables Suggestions
     * @param actionEvent Button click (garbage value)
     */
    public void suggestion_switch(ActionEvent actionEvent) {

        if (SUGGESTION){
            suggestion.setText("Suggestions: OFF");
            SUGGESTIONS.setVisible(false);
            SUGGESTION = false;
        }
        else{
            suggestion.setText("Suggestions: ON");
            SUGGESTION = true;
            SUGGESTIONS.setVisible(true);
        }
        /*
        if (suggestion.getText().equals("Suggestions: OFF")){
            suggestion.setText("Suggestions: ON");
            SUGGESTION = true;
            SUGGESTIONS.setVisible(true);
        }
        else{
            suggestion.setText("Suggestions: OFF");
            SUGGESTIONS.setVisible(false);
        }

         */
        saveStats();
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
                runTimer();
                numGuesses = num;
                numGuess.setText("");
                startNewGame();
            }
        } catch (NumberFormatException e){

        }
    }

    /**
     * @author David Kane
     * Will change dictionary to wordle default.
     * @param actionEvent Button click (garbage value)
     */
    public void fiveLetterWord(ActionEvent actionEvent){
        try {
            File temp = new File ("src/Resources/wordle-official.txt");
            if (!temp.equals(dictionaryFile)){
                runTimer();
                dictionaryFile = temp;
                startNewGame();
            }
        } catch (NullPointerException e){
            //TODO: Default wordle file could not be found
        }
    }

    /**
     * @author David Kane
     * Will change dictionary to 6 letter words.
     * @param actionEvent Button click (garbage value)
     */
    public void sixLetterWord(ActionEvent actionEvent){
        try {
            File temp = new File ("src/Resources/words_6_letters.txt");
            if (!temp.equals(dictionaryFile)){
                runTimer();
                dictionaryFile = temp;
                startNewGame();
            }
        } catch (NullPointerException e){
            //TODO: Default wordle file could not be found
        }
    }

    /**
     * @author David Kane
     * Will change dictionary to 7 letter words.
     * @param actionEvent Button click (garbage value)
     */
    public void sevenLetterWord(ActionEvent actionEvent){
        try {
            File temp = new File ("src/Resources/words_7_letters.txt");
            if (!temp.equals(dictionaryFile)){
                runTimer();
                dictionaryFile = temp;
                startNewGame();
            }
        } catch (NullPointerException e){
            //TODO: Default wordle file could not be found
        }
    }

    /**
     * Updates stats page after every game
     */
    public void updateStats(){
        //winLabel.setText(String.valueOf(session.getWins()));
        winLabel.setText(String.valueOf(wins));
        //lossLabel.setText(String.valueOf(session.getLosses()));
        lossLabel.setText(String.valueOf(losses));
        //avgNumGuesses.setText(String.valueOf(session.getAverageGuesses()));
        DecimalFormat df = new DecimalFormat("#.#");
        avgNumGuesses.setText(String.valueOf(df.format(avgGuesses)));
        System.out.println(avgNumGuesses.getText());
        //longestWinStreak.setText(String.valueOf(session.getWinStreak()));
        longestWinStreak.setText(String.valueOf(win_streak));
        frequentLetterPane.getChildren().clear();
        frequentWordPane.getChildren().clear();
        BarChart chart = Utils.make5BarChartFromHashMap(session.getWordGuessFrequency());
        chart.getStylesheets().add("Styling//stylesheet.css");
        chart.getStylesheets().add("Styling//dark_stylesheet.css");
        chart.getStylesheets().add("Styling//contrast_stylesheet.css");
        chart.getStyleClass().add("chart-dark");
        frequentWordPane.getChildren().add(chart);
        frequentLetterPane.getChildren().add(Utils.makeLetterBarChart(session.getLetterGuessFrequency()));
    }


    public void updateSuggestions(){
        GridPane grid = Utils.makeSuggestionsGrid(suggest);
        for(int i = 1; i < grid.getChildren().size();++i) {
            grid.getChildren().get(i).setOnMouseClicked(this:: enterSuggestion);
        }
        SUGGESTIONS.getChildren().clear();
            SUGGESTIONS.getChildren().add(grid);

        for(int i = 0; i < grid.getChildren().size();++i){
            Label temp = (Label)grid.getChildren().get(i);
            temp.getStyleClass().clear();
            if(DARK){
                temp.getStyleClass().add("label-dark");
            } else {
                temp.getStyleClass().add("label");
            }
            labels.add(temp);
        }
        if (SUGGESTION) SUGGESTIONS.setVisible(true); else SUGGESTIONS.setVisible(false);

    }


    public void openStats(){
        String pc = getComputerName();
        pc = pc.replaceAll("[\\\\/:*?\"<>|]", "");
        if (!pc.equals(("ERROR"))){
            try{
                File stats = new File("src/Resources/" + pc);
                BufferedReader br = new BufferedReader(new FileReader(stats));
                String line = br.readLine();
                Boolean dk = false, ct = false, sug = false;
                if (line.equals("DARK")){
                    dk = true;
                }
                line = br.readLine();
                if (line.equals("CONTRAST")){
                    ct = true;
                }
                line = br.readLine();
                if (line.equals("SUGGESTION")){
                    sug = true;
                }

                wins = Integer.parseInt(br.readLine());
                losses = Integer.parseInt(br.readLine());
                win_streak = Integer.parseInt(br.readLine());
                avgGuesses = Double.parseDouble(br.readLine());

                line = br.readLine();
                while (line != null){
                    guesses.add(line);
                    line = br.readLine();
                }

                if (dk) dark_light_mode_switch(null);
                if (ct) contrast_switch(null);
                if (sug) suggestion_switch(null);

            }catch (FileNotFoundException e){
                //NO FILE: DO NOTHING.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            //TODO: Handle error
        }
    }

    public void saveStats(){
        String pc = getComputerName();
        pc = pc.replaceAll("[\\\\/:*?\"<>|]", "");
        if (!pc.equals("ERROR")){
            String content = "";
            if (DARK) content += "DARK"; else content += "LIGHT";
            if (CONTRAST) content += "\nCONTRAST"; else content += "\nNORMAL MODE";
            if (SUGGESTION) content += "\nSUGGESTION"; else content += "\nNO SUGGESTIONS";
            if (wins + losses == 0){
                avgGuesses = 0;
            }
            else {
                avgGuesses = ((avgGuesses*(wins+losses -1)) + guess) / (wins + losses);
            }
            content += "\n" + wins + "\n" + losses + "\n" + win_streak + "\n" + avgGuesses;

            for (String s : guesses) {
                content += "\n" + s;
            }

            try {
                Files.write(Paths.get("src/Resources/" + pc), content.getBytes());
            }
            catch (IOException ignored){};


        }
        else{
            //TODO: Handle error
        }
    }

    private String getComputerName()
    {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "ERROR";
    }

    public void changeHardMode(ActionEvent actionEvent) {
        runTimer();
        if(hard_mode.getText().equals("Hard Mode")) {
            hard_mode.setText("Easy Mode");
            HARD = true;
            startNewGame();
        } else {
            hard_mode.setText("Hard Mode");
            HARD = false;
            startNewGame();
        }
    }

    public void runTimer(){
        if(!RUNNING){
            timeline.play();
            RUNNING = true;
        } else {
            timeline.stop();
            RUNNING = false;
        }

    }

    public void increaseTimer(){
        if(RUNNING) {
            double time = 0;
            time = Double.parseDouble(timer.getText());
            time = time * 10;
            time += 1;
            time = time/10;
            timer.setText(Double.toString(time));
        }
    }
}

