package wordle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javax.xml.soap.Text;
import java.util.*;

public class Controller {
    double BUTTON_PADDING = 10;
    ArrayList<List> gridOfTextFieldInputs = new ArrayList();
    ArrayList<String> textFieldValues = new ArrayList<>();

    /**
     * Main Grid of the application
     */
    @FXML
    Pane MAIN_PANE;

    Button submitButton;

    @FXML
    public void initialize(){


        //Creating grid of inputs
        GridPane grid = createGridOfInputs(5, 5);


        //Creating Submit Button
        submitButton = new Button("Submit");
        submitButton.setOnAction(this:: submitButtonAction);
        submitButton.setLayoutX(250);
        submitButton.setLayoutY(300);

        // Adding all to grid pane
        MAIN_PANE.getChildren().addAll(grid, submitButton);

    }



    /**
     *
     * Create grid of inputFields for words
     * @param numGuesses (Number of guesses)
     * @param numLetters (NUmber of letters being used for words)
     * @return Gridpane of inputs + filled out arrayList called gridOfTextFieldInputs
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
        return grid;
    }

    private void submitButtonAction(ActionEvent actionEvent) {
        // do verification stuff
    }

    private ArrayList getTextFieldValues(){
        return null;
    }

}
