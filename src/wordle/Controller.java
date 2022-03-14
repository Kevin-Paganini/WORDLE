package wordle;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class Controller {
    double BUTTON_PADDING = 10;
    ArrayList gridOfInputs = new ArrayList();

    /**
     * Main Grid of the application
     */
    @FXML
    Pane MAIN_PANE;

    @FXML
    public void initialize(){

        //Creating grid of inputs
        GridPane grid = createGridOfInputs(5, 5);
        MAIN_PANE.getChildren().add(grid);
    }

    /**
     *
     * Create grid of inputFields for words
     * @param numGuesses (Number of guesses)
     * @param numLetters (NUmber of letters being used for words)
     * @return Gridpane of inputs + filled out arrayList called gridOfInputs
     */
    private GridPane createGridOfInputs(int numGuesses, int numLetters){
        if (gridOfInputs.size() != 0){
            gridOfInputs = new ArrayList();
        }
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(BUTTON_PADDING));
        grid.setHgap(BUTTON_PADDING);
        grid.setVgap(BUTTON_PADDING);

        for (int r = 0; r < numGuesses; r++) {
            ArrayList row = new ArrayList();
            gridOfInputs.add(row);
            for (int c = 0; c < numLetters; c++) {
                TextField tf = new TextField();
                tf.setMaxSize(50, 50);
                grid.add(tf, c, r);
                row.add(tf);

            }
        }
        return grid;
    }

}
