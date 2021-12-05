package jakubwiraszka;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.gamefiles.Statistics;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class CreateHeroDialogController {

    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> healthSpinner;
    @FXML
    private Spinner<Integer> powerSpinner;
    @FXML
    private Spinner<Integer> agilitySpinner;

    public Hero processResults() {
        String name = nameTextField.getText().trim();
        int health = healthSpinner.getValue();
        int power = powerSpinner.getValue();
        int agility = agilitySpinner.getValue();

        return new Hero(name,  new Statistics(health, power, agility));
    }
}
