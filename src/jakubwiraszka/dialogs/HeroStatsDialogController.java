package jakubwiraszka.dialogs;

import jakubwiraszka.CreateMap;
import jakubwiraszka.gamefiles.Hero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public void initialize() {
        ImageView healthIcon = new ImageView(new Image(CreateMap.ICONS_LOC + "Health.png"));
        healthIcon.setFitHeight(40);
        healthIcon.setPreserveRatio(true);
        ImageView powerIcon = new ImageView(new Image(CreateMap.ICONS_LOC + "Power.png"));
        powerIcon.setFitHeight(40);
        powerIcon.setPreserveRatio(true);
        ImageView agilityIcon = new ImageView(new Image(CreateMap.ICONS_LOC + "Agility.png"));
        agilityIcon.setFitHeight(40);
        agilityIcon.setPreserveRatio(true);
        mainGridPane.add(healthIcon, 0, 1);
        mainGridPane.add(powerIcon, 0, 2);
        mainGridPane.add(agilityIcon, 0, 3);
    }

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
                powerLabel.setText("" + hero.getStatistics().getPowerValue());
                if(hero.getStatistics().getPowerValue() == 1) {
                    subtractPower.setDisable(false);
                }
            } else if (event.getSource().equals(addAgility)) {
                hero.changeAgility(1);
                agilityLabel.setText("" + hero.getStatistics().getAgilityValue());
                if(hero.getStatistics().getAgilityValue() == 1) {
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
            powerLabel.setText("" + hero.getStatistics().getPowerValue());
            if(hero.getStatistics().getPowerValue() == 0) {
                subtractPower.setDisable(true);
                addPower.setDisable(false);
            }
        } else if(event.getSource().equals(subtractAgility)) {
            hero.changeAgility(-1);
            agilityLabel.setText("" + hero.getStatistics().getAgilityValue());
            if(hero.getStatistics().getAgilityValue() == 0) {
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
        hero.getLevel().getPointsToSpend().addListener((observableValue, number, newNumber) -> {
            pointsToSpendLabel.setText("Points left: " + newNumber.intValue());
            if(newNumber.intValue() == 0) {
                setAddition(true);

            }
            if(newNumber.intValue() == 1) {
                setAddition(false);
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
