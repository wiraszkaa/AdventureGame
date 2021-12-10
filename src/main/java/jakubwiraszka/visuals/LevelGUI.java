package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Level;
import jakubwiraszka.map.GameMapBuilder;
import jakubwiraszka.observable.LevelListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelGUI implements LevelListener {
    private final HBox hBox;
    private final Label currentExperienceLabel;
    private final Label experienceForLevelUpLabel;
    private final Label levelLabel;

    public LevelGUI(Level level) {
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
    public void update(int currentExperience, int experienceForLevelUp, int currentLevel, int pointsToSpend) {
        currentExperienceLabel.setText("" + currentExperience + " /");
        experienceForLevelUpLabel.setText("" + experienceForLevelUp);
        levelLabel.setText("" + currentLevel);
    }
}
