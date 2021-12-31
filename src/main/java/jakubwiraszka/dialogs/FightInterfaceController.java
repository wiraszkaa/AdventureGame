package jakubwiraszka.dialogs;

import jakubwiraszka.InventoryController;
import jakubwiraszka.fight.ChargedAttack;
import jakubwiraszka.fight.QuickAttack;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.map.GameMapBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class FightInterfaceController {
    private Hero hero;
    private Enemy enemy;
    private int charge;
    private int experience;
    private Random random;

    @FXML
    private DialogPane mainDialogPane;
    @FXML
    private Label heroNameLabel;
    @FXML
    private VBox heroVBox;
    @FXML
    private Label enemyNameLabel;
    @FXML
    private VBox enemyVBox;
    @FXML
    private VBox actionVBox;
    @FXML
    private Button quickAttackButton;
    @FXML
    private Button strongAttackButton;
    @FXML
    private Button chargedAttackButton;
    @FXML
    private GridPane attackGridPane;

    public void initialize() {
        random = new Random();
        experience = random.nextInt(31) + 30;
        charge = 1;
        chargedAttackButton.setText("Charge");
        heroVBox.getChildren().add((new ImageView(new Image(GameMapBuilder.getImageUrl("Hero.png")))));
        enemyVBox.getChildren().add(new ImageView(new Image(GameMapBuilder.getImageUrl("Monster.png"))));

        heroNameLabel.setStyle("-fx-font-family: 'Arial Black'; -fx-font-size: 40");
        enemyNameLabel.setStyle("-fx-font-family: 'Arial Black'; -fx-font-size: 40");
    }

    @FXML
    public void attack(ActionEvent actionEvent) {
        setDisableButtons(true);
        if(actionEvent.getSource().equals(quickAttackButton)) {
            hero.setAttack(new QuickAttack());
        } else if(actionEvent.getSource().equals(strongAttackButton)) {
            hero.setAttack(new StrongAttack());
        } else if(actionEvent.getSource().equals(chargedAttackButton)) {
            hero.setAttack(new ChargedAttack());
        }

        if(charge >= hero.getAttack().getCharge()) {
            double damage = hero.attack(enemy);
            if (damage != -1) {
                Label heroAttack = new Label("You attack for " + damage + " with " + hero.getEquippedWeapon().getName());
                actionVBox.getChildren().add(heroAttack);
            } else {
                Label heroMiss = new Label("Unfortunately you miss");
                actionVBox.getChildren().add(heroMiss);
            }
            resetCharge();
            chargedAttackButton.setText("Charge");
        } else {
            Label heroCharge = new Label("You are charging");
            actionVBox.getChildren().add(heroCharge);
            addCharge();
            chargedAttackButton.setText("Charged Attack");
        }

        if(enemy.getStatistics().getHealth() <= 0) {
            enemy.setHealth(0);
            enemy.setAlive(false);
            Label win = new Label("\nYou have won and gained " + experience + " experience");
            createDelay(1, event -> actionVBox.getChildren().add(win));
            createDelay(2, event -> {
                Stage stage = (Stage) actionVBox.getScene().getWindow();
                stage.close();
            });
        } else {

            if(random.nextBoolean()) {
                enemy.setAttack(new QuickAttack());
            } else {
                enemy.setAttack(new StrongAttack());
            }

            double enemyDamage = enemy.attack(hero);
            if(enemyDamage != -1) {
                Label enemyAttack = new Label(enemy.getName() + " attacks for " + enemyDamage + " with " + enemy.getEquippedWeapon().getName());
                createDelay(1, event -> actionVBox.getChildren().add(enemyAttack));
            } else {
                Label enemyMiss = new Label(enemy.getName() + " misses");
                createDelay(1, event -> actionVBox.getChildren().add(enemyMiss));
            }
            if(hero.getStatistics().getHealth() <= 0) {
                hero.setHealth(0);
                Label loseLabel = new Label("\nYou died!");
                createDelay(2, event -> {
                    actionVBox.getChildren().add(loseLabel);
                    hero.setAlive(false);
                });
                createDelay(3, event -> {
                    Stage stage = (Stage) actionVBox.getScene().getWindow();
                    stage.close();
                });
            } else {
                createDelay(1, event -> setDisableButtons(false));
            }
        }
    }

    @FXML
    public void openInventory() {
        DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.setOwner(mainDialogPane);
        dialogBuilder.setTitle("Inventory");
        dialogBuilder.setSource("inventory.fxml");
        dialogBuilder.addOkButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        InventoryController inventoryController = dialogBuilder.getFxmlLoader().getController();
        inventoryController.setHero(hero);
        inventoryController.setSubtraction(true);
        inventoryController.getNameTextField().setText(hero.getName());
        inventoryController.getNameTextField().setEditable(false);
        inventoryController.setFightMode(enemy);
        hero.getLevel().addLevelListener(inventoryController.getPointsToSpendGUI());
        dialog.showAndWait();
    }

    private void createDelay(int seconds, EventHandler<ActionEvent> eventHandler) {
        Timeline timeLine = new Timeline(
                new KeyFrame(Duration.seconds(seconds), eventHandler)
        );
        timeLine.play();
    }

    private void setDisableButtons(boolean disableButtons) {
        quickAttackButton.setDisable(disableButtons);
        strongAttackButton.setDisable(disableButtons);
        chargedAttackButton.setDisable(disableButtons);
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setEnemy(Enemy enemy) {
        this.enemy = enemy;
    }

    private void resetCharge() {
        charge = 1;
    }

    private void addCharge() {
        charge++;
    }

    public int getExperience() {
        return experience;
    }

    public GridPane getAttackGridPane() {
        return attackGridPane;
    }

    public void setHeroNameLabel(String text) {
        heroNameLabel.setText(text);
    }

    public void setEnemyNameLabel(String text) {
        enemyNameLabel.setText(text);
    }

    public VBox getHeroVBox() {
        return heroVBox;
    }

    public VBox getEnemyVBox() {
        return enemyVBox;
    }
}
