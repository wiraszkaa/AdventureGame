package jakubwiraszka.dialogs;

import jakubwiraszka.gamefiles.Difficulty;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class DifficultyDialogController {
    @FXML
    private RadioButton easyRadioButton;
    @FXML
    private RadioButton hardRadioButton;
    @FXML
    private TextField nameTextField;

    public Difficulty getDifficulty() {
        if(easyRadioButton.isSelected()) {
            return Difficulty.EASY;
        } else if(hardRadioButton.isSelected()) {
            return Difficulty.HARD;
        } else {
            return Difficulty.MEDIUM;
        }
    }

    public String getName() {
        return nameTextField.getText();
    }
}
