package jakubwiraszka;

import jakubwiraszka.Randomize;
import jakubwiraszka.fight.Attack;
import jakubwiraszka.fight.ChargedAttack;
import jakubwiraszka.fight.QuickAttack;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.gamefiles.Hero;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class FightInterfaceController implements Randomize {
    private Hero hero;
    private Enemy enemy;
    private Attack attack;
    private int charge;
    private int experience;
    private Random random;

    @FXML
    private Label heroNameLabel;
    @FXML
    private Label heroHealthLabel;
    @FXML
    private Label heroPowerLabel;
    @FXML
    private Label heroAgilityLabel;
    @FXML
    private Label enemyNameLabel;
    @FXML
    private Label enemyHealthLabel;
    @FXML
    private Label enemyPowerLabel;
    @FXML
    private Label enemyAgilityLabel;
    @FXML
    private VBox actionVBox;
    @FXML
    private Button quickAttackButton;
    @FXML
    private Button strongAttackButton;
    @FXML
    private Button chargedAttackButton;
    @FXML
    private Label quickAttackLabel;
    @FXML
    private Label strongAttackLabel;
    @FXML
    private Label chargedAttackLabel;

    public void initialize() {
        random = new Random();
        experience = random.nextInt(31) + 30;
        charge = 1;
        chargedAttackButton.setText("Charge");
    }

    @FXML
    public void attack(ActionEvent actionEvent) {
        setDisableButtons(true);
        if(actionEvent.getSource().equals(quickAttackButton)) {
            attack = new QuickAttack();
        } else if(actionEvent.getSource().equals(strongAttackButton)) {
            attack = new StrongAttack();
        } else if(actionEvent.getSource().equals(chargedAttackButton)) {
            attack = new ChargedAttack();
        }

        double hitChance = attack.getHitChance() + (double) (hero.getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0;
        boolean heroHit = randomize(hitChance, 100);
        double heroAttackValue = hero.getStatistics().getPower() * attack.getPower();
        int heroDeviation = Math.max((int) (heroAttackValue / 5), 1);
        heroAttackValue += random.nextInt(heroDeviation * 2) - heroDeviation;

        if(charge >= attack.getCharge()) {
            if (heroHit) {
                Label heroAttack = new Label("You attack " + enemy.getName() + " for " + (int) heroAttackValue + " with " + attack.toString());
                actionVBox.getChildren().add(heroAttack);
                enemy.changeHealth((int) -heroAttackValue);
                enemyHealthLabel.setText("" + enemy.getStatistics().getHealthValue());
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

        if(enemy.getStatistics().getHealthValue() <= 0) {
            enemy.getStatistics().setHealth(0);
            enemyHealthLabel.setText("" + enemy.getStatistics().getHealthValue());
            enemy.setAlive(false);
            Label win = new Label("\nYou have won and gained " + experience + " experience");
            createDelay(1, event -> actionVBox.getChildren().add(win));
            createDelay(2, event -> {
                Stage stage = (Stage) actionVBox.getScene().getWindow();
                stage.close();
            });
        } else {

            Attack enemyAttackType;
            if(random.nextBoolean()) {
                enemyAttackType = new QuickAttack();
            } else {
                enemyAttackType = new StrongAttack();
            }


            double enemyHitChance = enemyAttackType.getHitChance() - (double) (hero.getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0;
            boolean enemyHit = randomize(enemyHitChance, 100);
            double enemyAttackValue = enemy.getStatistics().getPower() * enemyAttackType.getPower();
            int enemyDeviation = Math.max((int) (enemyAttackValue / 5), 1);
            enemyAttackValue += random.nextInt(enemyDeviation * 2) - enemyDeviation;

            if(enemyHit) {
                Label enemyAttack = new Label(enemy.getName() + " attacks You for " + (int) enemyAttackValue + " with " + enemyAttackType);
                hero.changeHealth((int) -enemyAttackValue);
                createDelay(1, event -> {
                    actionVBox.getChildren().add(enemyAttack);
                    heroHealthLabel.setText("" + hero.getStatistics().getHealthValue());
                });
            } else {
                Label enemyMiss = new Label(enemy.getName() + " misses");
                createDelay(1, event -> actionVBox.getChildren().add(enemyMiss));
            }
            if(hero.getStatistics().getHealthValue() <= 0) {
                hero.getStatistics().setHealth(0);
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

    public void setHeroHealthLabel(String health) {
        heroHealthLabel.setText(health);
    }

    public void setHeroPowerLabel(String power) {
        heroPowerLabel.setText(power);
    }

    public void setHeroAgilityLabel(String agility) {
        heroAgilityLabel.setText(agility);
    }

    public void setEnemyHealthLabel(String health) {
        enemyHealthLabel.setText(health);
    }

    public void setEnemyPowerLabel(String power) {
        enemyPowerLabel.setText(power);
    }

    public void setEnemyAgilityLabel(String agility) {
        enemyAgilityLabel.setText(agility);
    }

    public int getExperience() {
        return experience;
    }

    public void setQuickAttackLabel(String text) {
        quickAttackLabel.setText(text);
    }

    public void setStrongAttackLabel(String text) {
        strongAttackLabel.setText(text);
    }

    public void setChargedAttackLabel(String text) {
        chargedAttackLabel.setText(text);
    }

    public void setHeroNameLabel(String text) {
        heroNameLabel.setText(text);
    }

    public void setEnemyNameLabel(String text) {
        enemyNameLabel.setText(text);
    }

    @Override
    public boolean randomize(double chance, int iterations) {
        return Randomize.super.randomize(chance, iterations);
    }
}
