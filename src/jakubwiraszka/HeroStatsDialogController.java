package jakubwiraszka;

import jakubwiraszka.gamefiles.Hero;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HeroStatsDialogController {
    private Hero hero;

    @FXML
    private GridPane mainGridPane;
    @FXML
    private Label healthLabel;
    @FXML
    private Label powerLabel;
    @FXML
    private Label agilityLabel;
    @FXML
    private Button addHealth;
    @FXML
    private Button subtractHealth;
    @FXML
    private Button addPower;
    @FXML
    private Button subtractPower;
    @FXML
    private Button addAgility;
    @FXML
    private Button subtractAgility;
    @FXML
    private Label pointsToSpendLabel;

    @FXML
    public void add(ActionEvent event) {
        if(hero.getLevel().getPointsToSpend().intValue() > 0) {
            if (event.getSource().equals(addHealth)) {
                hero.addMaxHealth(2);
                hero.changeHealth(2);
                healthLabel.setText("" + hero.getMaxHealth());
                if(hero.getMaxHealth() == 2) {
                    subtractHealth.setDisable(false);
                }
            } else if (event.getSource().equals(addPower)) {
                hero.changePower(1);
                powerLabel.setText("" + hero.getStatistics().getPower());
                if(hero.getStatistics().getPower() == 1) {
                    subtractPower.setDisable(false);
                }
            } else if (event.getSource().equals(addAgility)) {
                hero.changeAgility(1);
                agilityLabel.setText("" + hero.getStatistics().getAgility());
                if(hero.getStatistics().getAgility() == 1) {
                    subtractAgility.setDisable(false);
                }
            }
            hero.getLevel().changePointsToSpend(-1);
        }
    }

    @FXML
    public void subtract(ActionEvent event) {
        if(event.getSource().equals(subtractHealth)) {
            hero.addMaxHealth(-2);
            hero.changeHealth(-2);
            healthLabel.setText("" + hero.getMaxHealth());
            if(hero.getMaxHealth() == 0) {
                subtractHealth.setDisable(true);
                addHealth.setDisable(false);
            }
        } else if(event.getSource().equals(subtractPower)) {
            hero.changePower(-1);
            powerLabel.setText("" + hero.getStatistics().getPower());
            if(hero.getStatistics().getPower() == 0) {
                subtractPower.setDisable(true);
                addPower.setDisable(false);
            }
        } else if(event.getSource().equals(subtractAgility)) {
            hero.changeAgility(-1);
            agilityLabel.setText("" + hero.getStatistics().getAgility());
            if(hero.getStatistics().getAgility() == 0) {
                subtractAgility.setDisable(true);
                addAgility.setDisable(false);
            }
        }
        hero.getLevel().changePointsToSpend(1);
    }

    public void setSubtraction(boolean disabled) {
        subtractHealth.setDisable(disabled);
        subtractPower.setDisable(disabled);
        subtractAgility.setDisable(disabled);
    }

    public void setAddition(boolean disabled) {
        addHealth.setDisable(disabled);
        addPower.setDisable(disabled);
        addAgility.setDisable(disabled);
    }

    public void setHero(Hero hero) {
        this.hero = hero;
        pointsToSpendLabel.setText("Points left: " + hero.getLevel().getPointsToSpend().intValue());
        hero.getLevel().getPointsToSpend().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newNumber) {
                pointsToSpendLabel.setText("Points left: " + newNumber.intValue());
                if(newNumber.intValue() == 0) {
                    setAddition(true);

                }
                if(newNumber.intValue() == 1) {
                    setAddition(false);
                }
            }
        });
    }

    public void setHealthLabel(String value) {
        healthLabel.setText(value);
    }

    public void setPowerLabel(String value) {
        powerLabel.setText(value);
    }

    public void setAgilityLabel(String value) {
        agilityLabel.setText(value);
    }

    public GridPane getMainGridPane() {
        return mainGridPane;
    }
}
