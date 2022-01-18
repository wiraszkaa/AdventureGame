package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Enemy;
import jakubwiraszka.map.GameMapBuilder;
import jakubwiraszka.observable.EnemyListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatsGUI implements EnemyListener {
    private final Label healthValue;
    private final Label powerValue;
    private final Label agilityValue;
    private final HBox healthHBox;
    private final HBox powerHBox;
    private final HBox agilityHBox;

    public StatsGUI(Enemy enemy) {
        healthValue = new Label("" + String.format("%.1f", enemy.getStatistics().getHealth()));
        healthValue.setStyle("-fx-font-family: 'Limelight', regular; -fx-font-size: 40");
        powerValue = new Label("" + enemy.getStatistics().getPower());
        powerValue.setStyle("-fx-font-family: 'Limelight', regular; -fx-font-size: 40");
        agilityValue = new Label("" + enemy.getStatistics().getAgility());
        agilityValue.setStyle("-fx-font-family: 'Limelight', regular; -fx-font-size: 40");

        healthHBox = createStatHBox("Health.png", healthValue);
        powerHBox = createStatHBox("Power.png", powerValue);
        agilityHBox = createStatHBox("Agility2.png", agilityValue);
    }

    private HBox createStatHBox(String source, Label statValue) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        ImageView icon = new ImageView();
        icon.setImage(new Image(GameMapBuilder.getImageUrl(source)));
        icon.setFitHeight(40);
        icon.setPreserveRatio(true);
        hBox.getChildren().add(icon);
        hBox.getChildren().add(statValue);
        return hBox;
    }

    public VBox getStatsVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(Stream.of(healthHBox, powerHBox, agilityHBox).collect(Collectors.toList()));
        return vBox;
    }

    public HBox getStatsHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(Stream.of(healthHBox, powerHBox, agilityHBox).collect(Collectors.toList()));
        return hBox;
    }

    HBox getHealthHBox() {
        return healthHBox;
    }

    @Override
    public void update(double health, double maxHealth, int power, int agility) {
        healthValue.setText("" + String.format("%.1f", health));
        powerValue.setText("" + power);
        agilityValue.setText("" + agility);
    }
}
