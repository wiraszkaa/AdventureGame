package jakubwiraszka.dialogs;

import jakubwiraszka.gamefiles.ItemContent;
import jakubwiraszka.gamefiles.Treasure;
import jakubwiraszka.gamefiles.World;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class NewTreasureDialogController {

    private World world;
    @FXML
    private TextField nameTextField;
    @FXML
    private RadioButton powerRadioButton;
    @FXML
    private RadioButton agilityRadioButton;
    @FXML
    private Spinner<Integer> valueSpinner;


    public Treasure newTreasure() {
        String name = nameTextField.getText().trim();
        String statistic = "Health";
        if(powerRadioButton.isSelected()) {
            statistic = "Power";
        } else if (agilityRadioButton.isSelected()) {
            statistic = "Agility";
        }

        int value = valueSpinner.getValue();

        Treasure newTreasure = new Treasure(name, new ItemContent(statistic, value));
        world.createTreasure(newTreasure);
        return newTreasure;
    }

    public void updateTreasure(Treasure treasure) {
        String statistic = "Health";
        if(powerRadioButton.isSelected()) {
            statistic = "Power";
        } else if (agilityRadioButton.isSelected()) {
            statistic = "Agility";
        }

        int value = valueSpinner.getValue();

        treasure.getContent().setValue(value);
        treasure.getContent().setStatistic(statistic);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setNameTextField(String name) {
        nameTextField.setText(name);
    }

    public void blockNameTextField() {
        nameTextField.setEditable(false);
    }

    public void setRadioButton(String statistic) {
        if(statistic.equals("Power")) {
            powerRadioButton.selectedProperty().set(true);
        } else if(statistic.equals("Agility")) {
            agilityRadioButton.selectedProperty().set(true);
        }
    }

    public void setValueSpinner(int value) {
        valueSpinner.getValueFactory().setValue(value);
    }
}
