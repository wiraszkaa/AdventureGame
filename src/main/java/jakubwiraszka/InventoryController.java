package jakubwiraszka;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Item;
import jakubwiraszka.items.Weapon;
import jakubwiraszka.observable.LevelListener;
import jakubwiraszka.visuals.HeroStatsGUI;
import jakubwiraszka.visuals.PointsToSpendGUI;
import jakubwiraszka.visuals.StatsGUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class InventoryController implements LevelListener {
    @FXML
    private BorderPane inventoryBorderPane;
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
    @FXML
    private Button equipButton;
    @FXML
    private Button useButton;
    @FXML
    private Button throwButton;

    private Hero hero;
    private PointsToSpendGUI pointsToSpendGUI;

    public void initialize() {
        inventoryBorderPane.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                Stage stage = (Stage) inventoryBorderPane.getScene().getWindow();
                stage.close();
                ev.consume();
            }
        });

        itemsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Item> call(ListView<Item> itemListView) {

                return new ListCell<>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });

        itemsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observableValue, Item item, Item t1) {
                if(t1 != null) {
                    throwButton.setDisable(false);
                    if(t1.isArmor() || t1.isWeapon()) {
                        equipButton.setDisable(false);
                        useButton.setDisable(true);
                    } else if(t1.isUsable()) {
                        useButton.setDisable(false);
                        equipButton.setDisable(true);
                    }
                }
            }
        });
    }

    @FXML
    public void equip() {
        Item item = itemsListView.getSelectionModel().getSelectedItem();
        hero.equip(item);
    }

    @FXML
    public void use() {
        Item item = itemsListView.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void throwAway() {
        Item item = itemsListView.getSelectionModel().getSelectedItem();
        hero.getInventory().remove(item);
        itemsListView.getItems().remove(item);
        System.out.println(item.getName() + " thrown away");
    }

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
                if(hero.getStatistics().getPower() == 1) {
                    subtractPower.setDisable(false);
                }
            } else if (event.getSource().equals(addAgility)) {
                hero.changeAgility(1);
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
            if(hero.getMaxHealth() == 0) {
                subtractHealth.setDisable(true);
                addHealth.setDisable(false);
            }
        } else if(event.getSource().equals(subtractPower)) {
            hero.changePower(-1);
            if(hero.getStatistics().getPower() == 0) {
                subtractPower.setDisable(true);
                addPower.setDisable(false);
            }
        } else if(event.getSource().equals(subtractAgility)) {
            hero.changeAgility(-1);
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

        pointsToSpendGUI = new PointsToSpendGUI(hero);
        Label pointsToSpendLabel = pointsToSpendGUI.getPointsToSpendGUI();
        GridPane.setColumnSpan(pointsToSpendLabel, 4);
        statsGridPane.add(pointsToSpendLabel, 0, 4);

        HeroStatsGUI heroStatsGUI = new HeroStatsGUI(hero);
        hero.addEnemyListener(heroStatsGUI);
        VBox statsVBox = heroStatsGUI.getStatsVBox();
        GridPane.setRowSpan(statsVBox, 3);
        statsGridPane.add(statsVBox, 1, 1);

        itemsListView.getItems().addAll(hero.getInventory());
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

    public void close() {
        Stage stage = (Stage) statsGridPane.getScene().getWindow();
        stage.close();
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
