package wordle;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.List;

public abstract class StylingChanger {
    /**
     * @author Carson Meredith
     * Changes from light to dark or dark to light
     * @param DARK DARK/LIGHT setting desired
     * @param CONTRAST CONTRAST/NORMAL setting desired
     */
    public static void update_dark(boolean DARK, boolean CONTRAST, List<Button> buttons, List<Pane> panes,List<Label> labels, List<TextField> textFields,TabPane tabPane) {
        if(DARK && CONTRAST) {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark-contrast");
            }
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/dark_contrast_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else if(CONTRAST){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-contrast");
            }
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/contrast_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else if(DARK){
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button-dark");
            }
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/dark_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else {
            for(Button button : buttons) {
                button.getStyleClass().clear();
                button.getStyleClass().add("button");
            }
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        }
        if(DARK){
            for(Pane pane : panes) {
                pane.getStylesheets().clear();
                pane.getStylesheets().add("Styling/dark_stylesheet.css");
                pane.getStyleClass().clear();
                pane.getStyleClass().add("pane-dark");
            }
            for(Label label : labels) {
                if(label.getStyleClass().toString().equals("label")) {
                    label.getStyleClass().clear();
                    label.getStyleClass().add("label-dark");
                }
            }
            for(TextField tf : textFields) {
                if(tf.getStyleClass().toString().equals("text-input text-field") || tf.getStyleClass().toString().equals("text-field")) {
                    tf.getStyleClass().clear();
                    tf.getStyleClass().add("text-field-dark");
                }
            }
        } else {
            for(Pane pane : panes) {
                pane.getStylesheets().clear();
                pane.getStylesheets().add("Styling/stylesheet.css");
                pane.getStyleClass().clear();
                pane.getStyleClass().add("pane");
            }
            for(Label label : labels) {
                if(label.getStyleClass().toString().equals("label-dark")) {
                    label.getStyleClass().clear();
                    label.getStyleClass().add("label");
                }
            }
            for(TextField tf : textFields) {
                if(tf.getStyleClass().toString().equals("text-field-dark")) {
                    tf.getStyleClass().clear();
                    tf.getStyleClass().add("text-field");
                }
            }
        }
    }
    /**
     * @author Carson Meredith
     * Changes from CONTRAST to NORMAL or NORMAL to CONTRAST
     * @param CONTRAST CONTRAST/NORMAL setting desired
     * @param DARK DARK/LIGHT setting desired
     */
    public static void update_contrast(boolean DARK, boolean CONTRAST, List<Button> buttons, List<Pane> panes,List<Label> labels, List<TextField> textFields,TabPane tabPane){
        String format = "button";
        if(DARK) {
            format+="-dark";
        }
        if(CONTRAST) {
            format+="-contrast";
        }
        for(Button button : buttons) {
            button.getStyleClass().clear();
            button.getStyleClass().add(format);
        }
        if(DARK && CONTRAST) {
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/dark_contrast_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else if(CONTRAST){
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/contrast_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else if(DARK){
            tabPane.getStylesheets().remove("Styling/stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/dark_stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        } else {
            tabPane.getStylesheets().remove("Styling/dark_contrast_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/dark_stylesheet.css");
            tabPane.getStylesheets().remove("Styling/contrast_stylesheet.css");
            tabPane.getStylesheets().add("Styling/stylesheet.css");
            tabPane.getStyleClass().clear();
            tabPane.getStyleClass().add("pane");
        }
        if(CONTRAST){
            for(Label temp : labels) {
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
            for(TextField tf : textFields) {
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
        } else {
            for(Label temp : labels) {
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
            for(TextField tf : textFields) {
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

    public static void changeAlert(Alert a, DialogPane win, boolean DARK, boolean CONTRAST, int win_streak) {
        win.getStylesheets().add("Styling//stylesheet.css");
        win.getStylesheets().add("Styling//dark_stylesheet.css");
        win.getStylesheets().add("Styling//contrast_stylesheet.css");
        String format = "";
        //Did player win or lose
        win.getStyleClass().clear();
        if(win_streak==0) {
            format+="loser";
        } else {
            format += "winner";
        }
        format+="-dialog";
        if(DARK) {
            format+="-dark";
        }
        if(CONTRAST){
            format+="-contrast";
        }
        win.getStyleClass().add(format);
    }
}
