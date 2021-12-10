package jakubwiraszka.visuals;

import jakubwiraszka.fight.Attack;
import jakubwiraszka.fight.ChargedAttack;
import jakubwiraszka.fight.QuickAttack;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.map.GameMapBuilder;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class ApproxDamageGUI {
    private final GridPane quickAttackGridPane;
    private final GridPane strongAttackGridPane;
    private final GridPane chargedAttackGridPane;

    public ApproxDamageGUI(Hero hero, Enemy enemy) {
        quickAttackGridPane = new GridPane();
        strongAttackGridPane = new GridPane();
        chargedAttackGridPane = new GridPane();
        createAttackGridPane(quickAttackGridPane, new QuickAttack(), hero, enemy);
        createAttackGridPane(strongAttackGridPane, new StrongAttack(), hero, enemy);
        createAttackGridPane(chargedAttackGridPane, new ChargedAttack(), hero, enemy);
    }

    private void createAttackGridPane(GridPane gridPane, Attack attack, Hero hero, Enemy enemy) {
        ImageView dmgImageView = new ImageView(new Image(GameMapBuilder.getImageUrl("Damage.png")));
        dmgImageView.setFitHeight(30);
        dmgImageView.setPreserveRatio(true);
        ImageView chanceImageView = new ImageView(new Image(GameMapBuilder.getImageUrl("HitChance.png")));
        chanceImageView.setFitHeight(30);
        chanceImageView.setPreserveRatio(true);
        Label dmgLabel = new Label("" + (int) (hero.getStatistics().getPowerValue() * attack.getPower()));
        dmgLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 20");
        double agilityChance = ((double) (hero.getStatistics().getAgilityValue() - enemy.getStatistics().getAgilityValue()) / 50);
        Label chanceLabel = new Label(((attack.getHitChance() + agilityChance) * 100) + "%");
        chanceLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 20");
        gridPane.add(dmgImageView, 0, 0);
        gridPane.add(dmgLabel, 1, 0);
        gridPane.add(chanceImageView, 0, 1);
        gridPane.add(chanceLabel, 1, 1);
    }

    public GridPane getQuickAttackGridPane() {
        return quickAttackGridPane;
    }

    public GridPane getStrongAttackGridPane() {
        return strongAttackGridPane;
    }

    public GridPane getChargedAttackGridPane() {
        return chargedAttackGridPane;
    }
}
