package jakubwiraszka;

import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.gamefiles.GameData;
import jakubwiraszka.gamefiles.Statistics;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class NewEnemyDialogController {

    private String worldName;
    private String enemyId;
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> healthSpinner;
    @FXML
    private Spinner<Integer> powerSpinner;
    @FXML
    private Spinner<Integer> agilitySpinner;

    public Enemy newEnemy() {
        String name = nameTextField.getText().trim();
        int health = healthSpinner.getValue();
        int power = powerSpinner.getValue();
        int agility = agilitySpinner.getValue();

        Enemy newEnemy = new Enemy(name,  new Statistics(health, power, agility));
        GameData.getInstance().findWorld(worldName).createEnemy(newEnemy);
        return newEnemy;
    }

    public void updateEnemy(Enemy enemy) {
        enemy.getStatistics().setHealth(healthSpinner.getValue());
        enemy.getStatistics().setPower(powerSpinner.getValue());
        enemy.getStatistics().setAgility(agilitySpinner.getValue());
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setNameTextField(String name) {
        nameTextField.setText(name);
    }

    public void setHealthSpinnerValue(int health) {
        healthSpinner.getValueFactory().setValue(health);
    }

    public void setPowerSpinnerValue(int power) {
        powerSpinner.getValueFactory().setValue(power);
    }

    public void setAgilitySpinnerValue(int agility) {
        agilitySpinner.getValueFactory().setValue(agility);
    }

}
