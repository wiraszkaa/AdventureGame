package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.observable.Listener;
import javafx.scene.control.Label;

public class PointsToSpendGUI implements Listener {
    private final Label pointsToSpendLabel;

    public PointsToSpendGUI(Hero hero) {
        pointsToSpendLabel = new Label("Points to spend: " + hero.getLevel().getPointsToSpend().intValue());
        pointsToSpendLabel.setStyle("-fx-font-family: 'Limelight', cursive; -fx-font-size: 20");
    }

    public Label getPointsToSpendGUI() {
        return pointsToSpendLabel;
    }

    @Override
    public void update(double currentExperience, double experienceForLevelUp, int currentLevel, int pointsToSpend, Location location) {
        pointsToSpendLabel.setText("Points to spend: " + pointsToSpend);
    }
}
