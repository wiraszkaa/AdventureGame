package jakubwiraszka.visuals;

import jakubwiraszka.CreateMap;
import jakubwiraszka.gamefiles.Enemy;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class StatsGUI {
    private final HBox hBox;
    private final Label healthValue;
    private final Label powerValue;
    private final Label agilityValue;

    public StatsGUI(Enemy enemy) {
        healthValue = new Label("" + enemy.getStatistics().getHealthValue());
        healthValue.setFont(new Font("Arial italic", 40));
        powerValue = new Label("" + enemy.getStatistics().getPowerValue());
        powerValue.setFont(new Font("Arial italic", 40));
        agilityValue = new Label("" + enemy.getStatistics().getAgilityValue());
        agilityValue.setFont(new Font("Arial italic", 40));
        hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(createStatHBox("Health.png", healthValue));
        hBox.getChildren().add(createStatHBox("Power.png", powerValue));
        hBox.getChildren().add(createStatHBox("Agility2.png", agilityValue));
        enemy.getStatistics().getHealth().addListener((observableValue, number, t1) -> {
            if(t1.intValue() <= 0) {
                healthValue.setText("0");
            } else {
                healthValue.setText("" + enemy.getStatistics().getHealthValue());
            }
        });
        enemy.getStatistics().getPower().addListener(observable -> powerValue.setText("" + enemy.getStatistics().getPowerValue()));
        enemy.getStatistics().getAgility().addListener(observable -> agilityValue.setText("" + enemy.getStatistics().getAgilityValue()));
    }

    private HBox createStatHBox(String source, Label statValue) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        ImageView icon = new ImageView();
        icon.setImage(new Image(CreateMap.ICONS_LOC + source));
        icon.setFitHeight(40);
        icon.setPreserveRatio(true);
        hBox.getChildren().add(icon);
        hBox.getChildren().add(statValue);
        return hBox;
    }

    public HBox getStatsGUI() {
        return hBox;
    }
}
