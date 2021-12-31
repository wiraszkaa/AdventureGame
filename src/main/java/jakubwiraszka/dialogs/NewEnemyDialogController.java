package jakubwiraszka.dialogs;

import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.gamefiles.Statistics;
import jakubwiraszka.gamefiles.World;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class NewEnemyDialogController {

    private World world;
    @FXML
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> healthSpinner;
    @FXML
    private Spinner<Integer> powerSpinner;
    @FXML
    private Spinner<Integer> agilitySpinner;

    public Hero createHero() {
        String name = nameTextField.getText().trim();
        int health = healthSpinner.getValue();
        int power = powerSpinner.getValue();
        int agility = agilitySpinner.getValue();

        return new Hero(name,  new Statistics(health, power, agility));
    }

    public Enemy newEnemy() {
        String name = nameTextField.getText().trim();
        int health = healthSpinner.getValue();
        int power = powerSpinner.getValue();
        int agility = agilitySpinner.getValue();

        Enemy newEnemy = new Enemy(name,  new Statistics(health, power, agility));
        world.createEnemy(newEnemy);
        return newEnemy;
    }

    public void updateEnemy(Enemy enemy) {
        enemy.setHealth(healthSpinner.getValue());
        enemy.setPower(powerSpinner.getValue());
        enemy.setAgility(agilitySpinner.getValue());
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
