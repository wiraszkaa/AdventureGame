package jakubwiraszka;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.items.Item;
import jakubwiraszka.observable.LevelListener;
import jakubwiraszka.visuals.PointsToSpendGUI;
import jakubwiraszka.visuals.StatsGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class InventoryController implements LevelListener {
    @FXML
    private ListView<Item> itemsListView;
    @FXML
    private GridPane statsGridPane;
    @FXML
    private TextField nameTextField;
    @FXML
    private Button addHealth;
    @FXML
    private Button addPower;
    @FXML
    private Button addAgility;
    @FXML
    private Button subtractHealth;
    @FXML
    private Button subtractPower;
    @FXML
    private Button subtractAgility;

    private Hero hero;
    private PointsToSpendGUI pointsToSpendGUI;

    @FXML
    public void add(ActionEvent event) {
        if(hero.getLevel().getPointsToSpend().intValue() > 0) {
            if (event.getSource().equals(addHealth)) {
                hero.addMaxHealth(2);
                hero.changeHealth(2);
                if(hero.getMaxHealth() == 2) {
                    subtractHealth.setDisable(false);
                }
            } else if (event.getSource().equals(addPower)) {
                hero.changePower(1);
                if(hero.getStatistics().getPowerValue() == 1) {
                    subtractPower.setDisable(false);
                }
            } else if (event.getSource().equals(addAgility)) {
                hero.changeAgility(1);
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
            if(hero.getMaxHealth() == 0) {
                subtractHealth.setDisable(true);
                addHealth.setDisable(false);
            }
        } else if(event.getSource().equals(subtractPower)) {
            hero.changePower(-1);
            if(hero.getStatistics().getPowerValue() == 0) {
                subtractPower.setDisable(true);
                addPower.setDisable(false);
            }
        } else if(event.getSource().equals(subtractAgility)) {
            hero.changeAgility(-1);
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

        pointsToSpendGUI = new PointsToSpendGUI(hero);
        Label pointsToSpendLabel = pointsToSpendGUI.getPointsToSpendGUI();
        GridPane.setColumnSpan(pointsToSpendLabel, 4);
        statsGridPane.add(pointsToSpendLabel, 0, 4);

        VBox statsVBox = new StatsGUI(hero).getStatsVBox();
        GridPane.setRowSpan(statsVBox, 3);
        statsGridPane.add(statsVBox, 1, 1);
    }

    public PointsToSpendGUI getPointsToSpendGUI() {
        return pointsToSpendGUI;
    }

    public TextField getNameTextField() {
        return nameTextField;
    }

    public GridPane getStatsGridPane() {
        return statsGridPane;
    }

    @Override
    public void update(int currentExperience, int experienceForLevelUp, int currentLevel, int pointsToSpend) {
        if(pointsToSpend == 0) {
            setAddition(true);
        }
        if(pointsToSpend == 1) {
            setAddition(false);
        }
    }
}
