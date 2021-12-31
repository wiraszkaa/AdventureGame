package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.observable.LevelListener;
import javafx.scene.control.Label;

public class PointsToSpendGUI implements LevelListener {
    private final Label pointsToSpendLabel;

    public PointsToSpendGUI(Hero hero) {
        pointsToSpendLabel = new Label("Points to spend: " + hero.getLevel().getPointsToSpend().intValue());
        pointsToSpendLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 20");
    }

    public Label getPointsToSpendGUI() {
        return pointsToSpendLabel;
    }

    @Override
    public void update(int currentExperience, int experienceForLevelUp, int currentLevel, int pointsToSpend) {
        pointsToSpendLabel.setText("Points to spend: " + pointsToSpend);
    }
}
