package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.gamefiles.Level;
import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.map.GameMapBuilder;
import jakubwiraszka.observable.Listener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelGUI implements Listener {
    private final HBox hBox;
    private final Label currentExperienceLabel;
    private final Label experienceForLevelUpLabel;
    private final Label levelLabel;

    private final Hero hero;
    private int currentLevel;

    public LevelGUI(Hero hero) {
        this.hero = hero;
        Level level = hero.getLevel();
        currentLevel = level.getCurrentLevel();
        currentExperienceLabel = new Label(level.getCurrentExperience() + " /");
        currentExperienceLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 30");
        experienceForLevelUpLabel = new Label("" + level.getExperienceForLevelUp());
        experienceForLevelUpLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 30");
        levelLabel = new Label("" + level.getCurrentLevel());
        levelLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 40");
        hBox = createLevelHBox();
    }

    private HBox createLevelHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);

        ImageView experienceImageView = new ImageView(new Image(GameMapBuilder.getImageUrl("Experience.png")));
        experienceImageView.setFitHeight(60);
        experienceImageView.setPreserveRatio(true);

        ImageView levelImageView = new ImageView(new Image(GameMapBuilder.getImageUrl("Level.png")));
        levelImageView.setFitHeight(50);
        levelImageView.setPreserveRatio(true);

        hBox.getChildren().addAll(Stream.of(experienceImageView, currentExperienceLabel, experienceForLevelUpLabel, levelImageView, levelLabel).collect(Collectors.toList()));
        return hBox;
    }

    public HBox getLevelGUI() {
        return hBox;
    }

    @Override
    public void update(double currentExperience, double experienceForLevelUp, int currentLevel, int pointsToSpend, Location location) {
        currentExperienceLabel.setText("" + (int) currentExperience + " /");
        experienceForLevelUpLabel.setText("" + (int) experienceForLevelUp);
        levelLabel.setText("" + currentLevel);
        if(currentLevel > this.currentLevel) {
            hero.setHealth(hero.getMaxHealth());
            this.currentLevel = currentLevel;
        }
    }
}
