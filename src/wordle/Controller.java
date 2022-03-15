package wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.xml.soap.Text;
import java.util.*;

public class Controller {
    double BUTTON_PADDING = 10;
    ArrayList<List> gridOfTextFieldInputs = new ArrayList();
    List<String> textFieldValues = Arrays.asList("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M");

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
        for(int i = 0; i < 26; i++){
            Label label = new Label(textFieldValues.get(i));
            label.setMaxSize(50, 50);
            label.setMinSize(50, 50);
            label.setPrefSize(50, 50);
            if (i < 10){
                grid.add(label, i%10, 0);
            }
            else if (i < 19){
                grid.add(label, (i-10)%9, 1);
            }
            else {
                grid.add(label, ((i-19) %7) + 1, 2);
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
            for (int c = 0; c < numLetters; c++) {
                TextField tf = new TextField();
                tf.setMaxSize(50, 50);
                grid.add(tf, c, r);
                row.add(tf);

            }
        }

        //Creating Submit Button
        submitButton = new Button("Submit");
        submitButton.setOnAction(this:: submitButtonAction);
        grid.add(submitButton, numGuesses, numLetters / 2);
        return grid;
    }

    private void submitButtonAction(ActionEvent actionEvent) {
        // do verification stuff
    }

    private ArrayList getTextFieldValues(){
        return null;
    }

}
