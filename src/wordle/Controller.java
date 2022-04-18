package wordle;

import Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.*;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import static javafx.scene.input.KeyCode.*;

public class Controller {
    public static final boolean DEBUG = true;
    public static final String ADMIN_PASSWORD = "1234";
    private static File dictionaryFile =  new File("src/Resources/wordle-official.txt");
    private static File lastWorkingFile = dictionaryFile;
    public static final double BUTTON_PADDING = 10;
    private int guess = 0;
    ArrayList<List<TextField>> gridOfTextFieldInputs = new ArrayList<>();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M", "DEL");
    Wordle game = null;
    GridPane letters_used;
    GridPane grid_input; // This has the submit button + grid of textfields FYI
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
    boolean ONLINE = true;
    ArrayList<String> guesses = new ArrayList<>();
    ArrayList<String> scores = new ArrayList<>();
    DialogPane win;
    boolean hintFlag = false;

    private HashMap<String, Integer> letters_used_grid_colors;

    private Button hintButton;

    private Session session;


    /**
     * Main Grid of the application
     */
    @FXML
    Pane MAIN_PANE;

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
    TextField username;

    @FXML
    TextField admin;


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
    private Button threeLetterDictionary;

    @FXML
    private Button fourLetterDictionary;

    @FXML
    private Button fiveLetterDictionary;

    @FXML
    private Button sixLetterDictionary;

    @FXML
    private Button sevenLetterDictionary;

    @FXML
    private Button userChange;

    @FXML
    private Button adminToggle;

    @FXML
    private Button resetUser;

    @FXML
    private VBox Scoreboard;

    @FXML
    private Pane ScorePane;


    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<Pane> panes = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();
    ArrayList<TextField> textFields = new ArrayList<>();
    String user;
    Timeline timeline;
    boolean ADMIN = false;
    private Client client;

    /**
     * Initialization function for window
     * Adding style sheets to specific elements
     * Creating various layouts
     * @author Kevin Paganini
     */
    @FXML
    public void initialize(){
        timeline = new Timeline(new KeyFrame(Duration.millis(100), (e)->increaseTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        session = new Session();
        user = getUserName();
        client = new Client();
        startNewGame();
        openStats();
    }

    /**
     * Loads a new Wordle object into the UI. Error checking for invalid file is contained.
     * This should be called after any game or when any game is to be reloaded. Initialize should not be called.
     */
    public void startNewGame(){
        try {
            if(ONLINE) client.receive();
            game = new Wordle(numGuesses, dictionaryFile, session);
            lastWorkingFile = dictionaryFile;
            RUNNING = false;
            guess = 0;
            MAIN_PANE.getChildren().clear();
            gridOfTextFieldInputs.clear();
            numLetters = game.getNumLetters();
            if(DEBUG) System.out.println("Target: " + game.getTarget());

            letters_used_grid_colors = Utils.makeInitialHashMapForKeyBoardColors();


            //Creating grid of inputs
            //width scale = 1.92
            //height scale = 1.235
            grid_input = createGridOfInputs(numGuesses, numLetters);
            grid_input.setLayoutX(350 - (numLetters -5) * 30);
            grid_input.setLayoutY(50);

            // Create keyboard of used letters
            letters_used = createKeyBoardInputs();
            letters_used.setLayoutX(200);
            letters_used.setLayoutY((numGuesses * 60) + 100);
            letters_used.getStyleClass().add("keyBoardGrid");
            // Create Statistics button
            hintButton = createHintButton();
            hintButton.setLayoutX(200);
            hintButton.setLayoutY(50);
            hintButton.setDisable(true);
            hintFlag = false;
            submitButton = makeSubmitButton();
            submitButton.setLayoutY(50);
            submitButton.setLayoutX(grid_input.getLayoutX() + (numLetters * 60) + 100);

            SUGGESTIONS = new GridPane();
            SUGGESTIONS.setLayoutX(130);
            SUGGESTIONS.setLayoutY((numGuesses*60)+300);
            timer = new Label();
            timer.setText("0.0");

            // Adding all to main pane
            MAIN_PANE.getChildren().addAll(grid_input, letters_used, hintButton, submitButton, SUGGESTIONS, timer);

            //Add all buttons to list of buttons
            buttons.add(submitButton);
            buttons.add(hintButton);
            buttons.add(importDictionaryButton);
            buttons.add(dark_light);
            buttons.add(contrast);
            buttons.add(numChange);
            buttons.add(threeLetterDictionary);
            buttons.add(fourLetterDictionary);
            buttons.add(fiveLetterDictionary);
            buttons.add(sixLetterDictionary);
            buttons.add(sevenLetterDictionary);
            buttons.add(suggestion);
            buttons.add(hard_mode);
            buttons.add(userChange);
            buttons.add(resetUser);
            buttons.add(adminToggle);

            //Add all panes to list of panes
            panes.add(SETTINGS_PANE);
            panes.add(MAIN_PANE);
            panes.add(STATS_PANE);
            panes.add(frequentLetterPane);
            panes.add(frequentWordPane);
            panes.add(Scoreboard);
            panes.add(ScorePane);

            //Add all labels to list of labels
            for(int i = 0; i < letters_used.getChildren().size();++i){
                Label temp = (Label)letters_used.getChildren().get(i);
                labels.add(temp);
            }
            for(int i = 0; i < Scoreboard.getChildren().size();++i){
                Label temp = (Label)Scoreboard.getChildren().get(i);
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

            Scoreboard.setSpacing(10.0);
            for(int i = 1; i < Scoreboard.getChildren().size();++i) {
                Label temp = (Label)Scoreboard.getChildren().get(i);
                temp.setText("");
            }
            scores = Utils.readScoreboard();
            if(!scores.isEmpty()){
                int dif = 0;
                if (HARD) {
                    dif = 1;
                }
                int sug = 0;
                if(SUGGESTION) {
                    sug = 1;
                }
                Scoreboard = Utils.updateScoreboard(scores,Scoreboard, numLetters, dif, sug);
            }
            StylingChanger.update_dark(DARK,CONTRAST,buttons,panes,labels,textFields);
            StylingChanger.update_contrast(DARK,CONTRAST,buttons,panes,labels,textFields);
        } catch (IOException e) {
            //TODO: Catch if the wordle-official file does not exist
            System.out.println("Entered an invalid File");

            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Invalid File");
            win = a.getDialogPane();
            win.getStylesheets().add("Styling/stylesheet.css");
            win.getStylesheets().add("Styling/dark_stylesheet.css");
            win.getStylesheets().add("Styling/contrast_stylesheet.css");
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
            win.setHeaderText("INVALID FILE");
            win.setContentText("Please enter a valid file.");
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,0);

            Optional<ButtonType> result = a.showAndWait();
            if (!result.isPresent()) {
                // alert is exited, no button has been pressed.
                session.prettyString();
                //Platform.exit();
            } else if (result.get() == ButtonType.OK) {
                //oke button is pressed
                importDictionary();
            } else if (result.get() == ButtonType.CANCEL){
                // cancel button is pressed
                session.prettyString();
                //stack
                //Platform.exit();
            }
        } catch (NullPointerException e){
            //TODO: Catch if the opened dictionary file is blank

        } finally {
            dictionaryFile = lastWorkingFile;
            toggleTimer();
        }
    }

    /**
     * Statistics Button for later
     * @return Button for statistics
     * @author Kevin Paganini
     * //TODO Maybe pick a better icon
     */
    private Button createHintButton() {

        Image image = new Image("file:src/Resources/hint.png", 30, 30, false, false);
        ImageView view = new ImageView(image);

        Button button = new Button();
        button.setPrefSize(30, 30);
        button.setGraphic(view);
        button.setOnAction(this::showHint);
        button.setLayoutX((numLetters * 60) + 75);
        button.setLayoutY(310);
        return button;

    }

    /**
     * Hint button is disabled until first guess is made
     * Hint button gets turned on once first guess is made
     * User gets one hint per game
     * Hint button disabled after one hint
     * User gets letter until all letters in target have been guessed than gives suggestion word
     * @param actionEvent button click
     */
    private void showHint(ActionEvent actionEvent) {
        Pair<Integer, Character> hint = game.getHint();
        updateSuggestions();
        TextField tf = gridOfTextFieldInputs.get(guess).get(hint.getKey());
        tf.setText(String.valueOf(hint.getValue()));
        tf.setDisable(true);
        hintButton.setDisable(true);
    }



    /**
     * Creating keyboard replica to display to user what has been chosen
     * and the colour of the corresponding letter
     * @return GridPane containing labels of all keyboard inputs which can be clicked.
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
     * Functionality for keyboard inputs. Allows the auxiliary keyboard to be pressed to simulate a letter
     * being entered into the application
     * @param mouseEvent On click
     * @author David Kane
     */
    private void mouseClick(MouseEvent mouseEvent) {
        String letter = ((Label) mouseEvent.getSource()).getText().toUpperCase();
        enterLetter(letter);
    }

    /**
     * Allows for backend letter entry into the input grid
     * Does not interface with user and is not a listener
     * @param letter Letter being entered
     */
    private void enterLetter(String letter){
        if (letter.equals("DEL")) {
            for (int i = numLetters-1; i >= 0; i--){
                TextField tf = gridOfTextFieldInputs.get(guess).get(i);
                if (!tf.getText().equals("")) {
                    tf.setText("");
                    tf.requestFocus();
                    break;
                }
            }
        } else {
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
     * Allows the user to click the suggestion and automatically guess it
     * @param event button click
     */
    public void enterSuggestion(MouseEvent event) {
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
     * Creates primary Wordle interface for exactly 1 game. The most basic functionality of Wordle.
     * Each input field can hold exactly 1 character. When entered, the previous row of inputs
     * is disabled and coloured according to wordle.makeGuess() return. Textfield listeners is declared
     * elsewhere.
     *
     * @param numGuesses Guesses that can be made in this game - columns in grid
     * @param numLetters Length of the guesses that can be made - rows in grid
     * @return Gridpane of inputs + filled out arrayList called gridOfTextFieldInputs
     * @author Kevin Paganini
     */
    private GridPane createGridOfInputs(int numGuesses, int numLetters){
        this.numLetters = numLetters;
        if (gridOfTextFieldInputs.size() != 0){
            gridOfTextFieldInputs = new ArrayList<>();
        }
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(BUTTON_PADDING));
        grid.setHgap(BUTTON_PADDING);
        grid.setVgap(BUTTON_PADDING);

        for (int r = 0; r < numGuesses; r++) {
            ArrayList<TextField> row = new ArrayList<>();
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
     * Makes submit button for grid. Listener is declared below
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
     *
     * Gets input from guess fields
     * Disables text fields that were enabled
     * Checks positions of guess against target
     *
     * @author David Kane & Carson Meredith & Kevin Paganini
     */
    private void submit() {
        // do verification stuff

        // Turning on hint button after first guess
        if(!hintFlag && !HARD){
            hintButton.setDisable(false);
            hintFlag = true;
        }
        //Getting input from guess text fields
        String input = "";
        for(int i = 0; i < numLetters; i++){
            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            tf.setDisable(true);
            input += tf.getText();
        }

        if (DEBUG) System.out.println(input);

        guesses.add(input);
        updateStats();
        game.updateGuesses(input.toUpperCase(Locale.ROOT));

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

        if (DEBUG) {
            for(int i : position) {
                System.out.println(i);
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

            saveGlobalData("Yes");

            win_percentage = Math.min(100, ((double)wins/(losses+wins)) * 100);

            int dif = HARD ? 1 : 0;
            int sug = SUGGESTION ? 1 : 0;
            //Adds a new score to the list of scores
            scores.add(user + "," + timer.getText() + ";" + guess + ":" + numLetters + "/" + dif + "|" + sug);
            scores.sort(Utils::sortScoreboard);
            //Saves scoreboard
            Utils.saveScoreboard(scores,Scoreboard, numLetters, dif,sug);
            saveStats();
            showWinAlert();
        //If the guess is wrong but the user isn't out of guesses
        } else if (guess != numGuesses){
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
            saveStats();
            win_percentage = ((double)wins/(losses+wins)) * 100;
            if(win_percentage > 100) {
                win_percentage = 100;
            }

            saveGlobalData("No");

            showWinAlert();
        }
        submitButton.setDisable(true);


    }

    public void saveGlobalData(String winner){
        String fileInput = "User: " + user + "\nGame Number: " + (wins+losses) + "\nTarget: " + game.getTarget().toUpperCase(Locale.ROOT) + "\nNumber of Guesses: " + guess + "\nWin: " + winner;
        int size = guesses.size();
        for(int i = guess; i > 0; i--){
            fileInput += "\n" + guesses.get(size-i);
        }
        fileInput += "\n";
        String text = "";
        try {
            File stats = new File("src/Resources/UserData/GlobalData");
            BufferedReader br = new BufferedReader(new FileReader(stats));
            String line = br.readLine();
            while (line != null){
                text += line + "\n";
                line = br.readLine();
            }

        } catch (IOException e){}
        //TODO
        try {
            text += "\n" + fileInput;
            Files.write(Paths.get("src/Resources/UserData/GlobalData"), text.getBytes());
        } catch (IOException e){
            System.out.println("clown");
        }
    }
    /**
     * Creates alert when user either wins or loses their game of wordle
     * Shows information on win streak, guesses made, and win percentage
     * @author Carson Merediith
     */
    private void showWinAlert() {
        toggleTimer();
        if(ONLINE) client.send();
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Play Again");
        win = a.getDialogPane();
        StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
        DecimalFormat df = new DecimalFormat("0.00");
        win.setHeaderText("Played = " + (wins + losses) + "\nWIN% = " + df.format(win_percentage) + "%" + "\nGUESSES THIS GAME = " + guess + "\nWINSTREAK = " + win_streak + "\nTIME: = " + timer.getText());
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
     * Validates that the word in the row (one guess) is valid. Called
     * every time a key is pressed.
     * @author David Kane
     */
    private void getTextFieldValues(){


        submitButton.setDisable(false);
        String input = "";
        for(int i = 0; i < numLetters; i++){
            TextField tf = gridOfTextFieldInputs.get(guess).get(i);
            input += tf.getText();
        }
        //Gets the value of the character being input
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
     * Allows User to navigate guess boxes and also lets the user press "enter" to submit if possible.
     * Handles arrow-keys and backspace key. Does not handle inputs
     * @param e keyEvent to trigger the function
     * @author David Kane
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
                if(gridOfTextFieldInputs.get(guess).get(pos - 1).isDisabled()) {
                    if (pos-1 > 0){
                        gridOfTextFieldInputs.get(guess).get(pos - 2).requestFocus();
                    }
                    else{
                        gridOfTextFieldInputs.get(guess).get(gridOfTextFieldInputs.get(guess).size() - 1).requestFocus();
                    }
                }
                else {
                    gridOfTextFieldInputs.get(guess).get(pos - 1).requestFocus();
                }
            }
            else{
                if (gridOfTextFieldInputs.get(guess).get(gridOfTextFieldInputs.get(guess).size() -1).isDisabled()){
                    gridOfTextFieldInputs.get(guess).get(gridOfTextFieldInputs.get(guess).size() - 2).requestFocus();
                }
                else {
                    gridOfTextFieldInputs.get(guess).get(gridOfTextFieldInputs.get(guess).size() - 1).requestFocus();
                }
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
                if(gridOfTextFieldInputs.get(guess).get(pos + 1).isDisabled()) {
                    if (pos + 2 < gridOfTextFieldInputs.get(guess).size()) {
                        gridOfTextFieldInputs.get(guess).get(pos + 2).requestFocus();
                    }
                    else{
                        gridOfTextFieldInputs.get(guess).get(0).requestFocus();
                    }
                }
                else {
                    gridOfTextFieldInputs.get(guess).get(pos + 1).requestFocus();
                }            }
            else{
                if (gridOfTextFieldInputs.get(guess).get(0).isDisabled()){
                    gridOfTextFieldInputs.get(guess).get(1).requestFocus();
                }
                else {
                    gridOfTextFieldInputs.get(guess).get(0).requestFocus();
                }
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
                if (gridOfTextFieldInputs.get(guess).get(pos-1).isDisabled()){
                 if (pos-1 > 0){
                     gridOfTextFieldInputs.get(guess).get(pos - 2).setText("");
                     gridOfTextFieldInputs.get(guess).get(pos - 2).requestFocus();
                 }
                }
                else {
                    gridOfTextFieldInputs.get(guess).get(pos - 1).setText("");
                    gridOfTextFieldInputs.get(guess).get(pos - 1).requestFocus();
                }
            }
        }
    }



    /**
     * Ensure only letters can be entered, and moves the boxes accordingly
     * @author David Kane
     * @param e ???
     * @param oldValue ???
     * @param newValue ???
     * @param index index of the box the key is being entered in (???)
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
                    if(gridOfTextFieldInputs.get(guess).get(index + 1).isDisabled()) {
                        if (index + 2 < gridOfTextFieldInputs.get(guess).size()) {
                            gridOfTextFieldInputs.get(guess).get(index + 2).requestFocus();
                        }
                    }
                    else {
                            gridOfTextFieldInputs.get(guess).get(index + 1).requestFocus();
                    }
                }
            }
        }
    }

    /**
     * Prompts the user to change the dictionary and loads a new Wordle if the dictionary was
     * changed. StartNewGame handles the event where the file doesn't work.
     * @param actionEvent Button click (garbage value)
     * @author Atreyu Schilling & David Kane
     */
    public void importDictionary(ActionEvent actionEvent) {
        importDictionary();
    }

    public void importDictionary(){
        timeline.pause();
        RUNNING = false;
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File ("src/Resources/"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            importDictionary(file);
        }
    }

    public void importDictionary(File file) {
        if (file != null) {
            dictionaryFile = file;
            startNewGame();
        }
        timeline.play();
        RUNNING = true;
    }

    /**
     * Changes dark mode if dark button is pressed
     * @param actionEvent Button click
     * @author Carson Meredith
     */
    public void dark_light_mode_switch(ActionEvent actionEvent) {
        setDark();
        saveStats();
    }

    /**
     * Changes Contrast mode if contrast button is pressed
     * @param actionEvent Button click
     * @author Carson Meredith
     */
    public void contrast_switch(ActionEvent actionEvent) {
        setContrast();
        saveStats();
    }

    /**
     * Disables/Enables Suggestions
     * @param actionEvent Button click (garbage value)
     * @author David Kane
     */
    public void suggestion_switch(ActionEvent actionEvent) {
        setSuggestion();
        saveStats();
        int dif = 0;
        if(HARD) {
            dif = 1;
        }
        int sug = 0;
        if(SUGGESTION) {
            sug = 1;
        }
        Utils.updateScoreboard(scores,Scoreboard,numLetters,dif,sug);
    }

    /**
     * Toggles dark mode - used in the button listener
     */
    public void setDark(){
        String text = dark_light.getText();
        DARK = text.equals("DARK-MODE");
        StylingChanger.update_dark(DARK,CONTRAST,buttons,panes,labels,textFields);
        if(DARK){
            dark_light.setText("LIGHT-MODE");
        } else {
            dark_light.setText("DARK-MODE");
        }
    }

    /**
     * Toggles suggestions - used in the button listener
     */
    public void setSuggestion(){
        if(HARD) {
            suggestion.setDisable(true);
        } else {
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
        }
    }

    /**
     * Toggles contrast mode - used in the button listener
     */
    public void setContrast(){
        String text = contrast.getText();
        CONTRAST = text.equals("HIGH-CONTRAST-MODE");
        StylingChanger.update_contrast(DARK,CONTRAST,buttons,panes,labels,textFields);
        if(CONTRAST){
            contrast.setText("NORMAL-MODE");
        } else {
            contrast.setText("HIGH-CONTRAST-MODE");
        }
    }

    /**
     * Changes amount of guesses user is allowed
     * If <= 0, does nothing
     * @param actionEvent Button click (garbage value)
     * @author David Kane
     */
    public void changeGuessAmount(ActionEvent actionEvent){
        String guess = numGuess.getText();
        try {
            int num = Integer.parseInt(guess);
            if(num > 0) {
                toggleTimer();
                numGuesses = num;
                numGuess.setText("");
                startNewGame();
            }
        } catch (NumberFormatException e){
            //TODO
        }
    }



    /**
     * Loads a dictionary specified by the programmer into the wordle
     * Does check that file is readable and formatted correctly before attempting to load it
     *
     * @param file file to load in
     * @author David Cane, Carson Meredith, Atreyu Schilling
     */
    public void loadPrebuiltDictionary(File file) {
        if (!file.equals(dictionaryFile) && file.exists() && file.isFile()){
            toggleTimer();
            try {
                new Wordle(numGuesses, file, session);
                dictionaryFile = file;
                startNewGame();
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Invalid File");
                win = a.getDialogPane();
                StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
                win.setHeaderText("FILE UNREADABLE");
                win.setContentText(file.getName() + " is unreadable and may have been changed.");
                StylingChanger.changeAlert(a,win,DARK,CONTRAST,0);
                a.showAndWait();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Invalid File");
            win = a.getDialogPane();
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
            win.setHeaderText("INVALID FILE");
            win.setContentText(file.getName() + " does not exist and may have been deleted");
            StylingChanger.changeAlert(a,win,DARK,CONTRAST,0);
            a.showAndWait();
        }
    }

    public void threeLetterWord(ActionEvent actionEvent){
        loadPrebuiltDictionary(new File("src/Resources/words_3_letters.txt"));
    }

    public void fourLetterWord(ActionEvent actionEvent){
        loadPrebuiltDictionary(new File("src/Resources/words_4_letters.txt"));
    }

    public void fiveLetterWord(ActionEvent actionEvent){
        loadPrebuiltDictionary(new File("src/Resources/wordle-official.txt"));
    }

    public void sixLetterWord(ActionEvent actionEvent){
        loadPrebuiltDictionary(new File("src/Resources/words_6_letters.txt"));
    }

    public void sevenLetterWord(ActionEvent actionEvent){
        loadPrebuiltDictionary(new File("src/Resources/words_7_letters.txt"));
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
        if (DEBUG) System.out.println(avgNumGuesses.getText());
        //longestWinStreak.setText(String.valueOf(session.getWinStreak()));
        longestWinStreak.setText(String.valueOf(win_streak));
        frequentLetterPane.getChildren().clear();
        frequentWordPane.getChildren().clear();
        HashMap<String, Integer> wordFrequency = new HashMap<>();
        HashMap<String, Integer> letterFrequency = new HashMap<>();

        for(int i = 0; i < 26; i++){
            int asciiValue = 65 + i;
            char letter = (char)asciiValue;
            letterFrequency.put(String.valueOf(letter), 0);
        }

        for(String word : guesses){
            if (wordFrequency.containsKey(word)){
                wordFrequency.replace(word, wordFrequency.get(word)+1);
            }
            else{
                wordFrequency.put(word, 1);
            }
            for (char c : word.toCharArray()) {
                String letter = String.valueOf(c);
                if (letterFrequency.containsKey(letter)){
                    letterFrequency.replace(letter, letterFrequency.get(letter)+1);
                }
                else {
                    letterFrequency.put(letter, 1);
                }
            }
        }

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        wordFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        frequentWordPane.getChildren().add(Utils.make5BarChartFromHashMap(reverseSortedMap));
        frequentLetterPane.getChildren().add(Utils.makeLetterBarChart(letterFrequency));
        panes.add(frequentWordPane);
        panes.add(frequentLetterPane);
    }

    /**
     * Updates the suggestions grid then determines if it should be visible
     */
    public void updateSuggestions(){
        GridPane grid = Utils.makeSuggestionsGrid(game.getSuggestions());
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
        SUGGESTIONS.setVisible(SUGGESTION);
    }


    public void openStats(){
        String pc = getComputerName();
        pc = pc.replaceAll("[\\\\/:*?\"<>|]", "");
        if (!pc.equals(("ERROR"))){
            try{
                File pcFile = new File("src/Resources/" + pc);
                BufferedReader br = new BufferedReader(new FileReader(pcFile));
                String line = br.readLine();
                if (line != null){
                    updateUser(line);
                } else{
                    //TODO: THROW ERROR
                }


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
        if (pc.equals("ERROR")) {
            //TODO: Computer name error
            return;
        }
        try {
            Files.write(Paths.get("src/Resources/" + pc), user.getBytes());
        }
        catch (IOException e){
            //TODO: PC File does weirdness
            return;
        }
        String content = "";
        if (ADMIN) content += "ADMIN"; else content += "USER";
        if (DARK) content += "\nDARK"; else content += "\nLIGHT";
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
            Files.write(Paths.get("src/Resources/UserData/" + user), content.getBytes());
        }
        catch (IOException e){
            //TODO: Handle error
        }
    }

    private String getComputerName()
    {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else return env.getOrDefault("HOSTNAME", "ERROR");
    }

    private String getUserName(){
        return System.getProperty("user.name");
    }

    /**
     * Toggles hard-mode and restarts the game
     * @param actionEvent button press
     */
    public void changeHardMode(ActionEvent actionEvent) {
        toggleTimer();
        if (hard_mode.getText().equals("Hard Mode")) {
            hard_mode.setText("Easy Mode");
            HARD = true;
            suggestion.setDisable(true);
            SUGGESTION = false;
            suggestion.setText("Suggestions: OFF");
            startNewGame();
        } else {
            hard_mode.setText("Hard Mode");
            suggestion.setDisable(false);
            HARD = false;
        }
        startNewGame();
    }

    /**
     * Toggles the timer
     * @author Carson Meredith
     */
    public void toggleTimer(){
        if(!RUNNING){
            timeline.play();
            RUNNING = true;
        } else {
            timeline.stop();
            RUNNING = false;
        }

    }

    /**
     * Method to update the Label for the timer/End game if hard mode
     * @author Carson Meredith
     */
    public void increaseTimer(){
        if(RUNNING) {
            double time = Double.parseDouble(timer.getText());
            time = time * 10;
            time += 1;
            time = time/10;
            timer.setText(Double.toString(time));
            if(HARD && time>=45) {
                timeline.stop();
                win_streak = 0;
                losses++;
                saveStats();
                win_percentage = ((double)wins/(losses+wins)) * 100;
                if(win_percentage > 100) {
                    win_percentage = 100;
                }

                saveGlobalData("No");
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "OUT OF TIME");
                win = a.getDialogPane();
                StylingChanger.changeAlert(a,win,DARK,CONTRAST,win_streak);
                win.setHeaderText("YOU GOTTA BE QUICKER THAN THAT");
                win.setContentText("TRY AGAIN");
                a.setOnCloseRequest(evt -> startNewGame());
                a.show();
            }

        }
    }

    public void changeUser(ActionEvent e){
        String user = username.getText();
        user = user.replaceAll("[\\\\/:*?\"<>|]", "");
        if (!user.trim().equals("") && !user.equalsIgnoreCase(this.user)){
            updateUser(user);
        }
    }


    public void updateUser(String user){
        this.user = user;
        try {
            File stats = new File("src/Resources/UserData/" + user);
            BufferedReader br = new BufferedReader(new FileReader(stats));
            String line = br.readLine();

            boolean dk = false, ct = false, sug = false, ad = false;
            if (line.equals("ADMIN")) {
                ad = true;
            }
            line = br.readLine();
            if (line.equals("DARK")) {
                dk = true;
            }
            line = br.readLine();
            if (line.equals("CONTRAST")) {
                ct = true;
            }
            line = br.readLine();
            if (line.equals("SUGGESTION")) {
                sug = true;
            }

            wins = Integer.parseInt(br.readLine());
            losses = Integer.parseInt(br.readLine());
            win_streak = Integer.parseInt(br.readLine());
            avgGuesses = Double.parseDouble(br.readLine());

            line = br.readLine();
            guesses.clear();
            while (line != null) {
                line = line.toUpperCase(Locale.ROOT);
                guesses.add(line);
                game.updateGuesses(line);
                line = br.readLine();
            }

            if (dk != DARK) setDark();
            if (ct != CONTRAST) setContrast();
            if (sug != SUGGESTION) setSuggestion();
            if (ad != ADMIN) setAdmin();
            saveStats();
            startNewGame();
            updateStats();
        }
        catch (FileNotFoundException e){
            resetUser();
        }
        catch (IOException e){
            //TODO HANDLE ERROR
        }
    }


    public void toggle_admin(ActionEvent e){
        //TODO ADMIN
        String userInput = admin.getText();

        if (userInput.equals(ADMIN_PASSWORD) && !ADMIN){
            setAdmin();
            saveStats();
        }
    }
    public void setAdmin(){
        ADMIN = !ADMIN;
        if (ADMIN){

        } else {

        }
    }

    /**
     * Resets the game and resets the game's user.
     */
    public void resetUser(){
        startNewGame();
        wins = 0;
        losses = 0;
        win_streak = 0;
        avgGuesses = 0;
        guesses.clear();
        game.clearGuesses();
        if (DARK) setDark();
        if (CONTRAST) setContrast();
        if (SUGGESTION) setSuggestion();
        if (ADMIN) setAdmin();
        saveStats();
        updateStats();
    }
}
