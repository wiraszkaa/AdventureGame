package jakubwiraszka.gamefiles;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Level {
    private final IntegerProperty currentLevel;
    private int experienceForLevelUp;
    private final IntegerProperty currentExperience;
    private final IntegerProperty pointsToSpend;

    public Level(int level) {
        currentLevel  = new SimpleIntegerProperty(level);
        pointsToSpend = new SimpleIntegerProperty();
        currentExperience = new SimpleIntegerProperty();
        experienceForLevelUp = level * 100;
        currentLevel.addListener((observableValue, level1, newLevel) -> {
            if(newLevel != null) {
                if(newLevel.intValue() > level1.intValue()) {
                    experienceForLevelUp = newLevel.intValue() * 100;
                    pointsToSpend.set(pointsToSpend.intValue() + 1);
                }
            }
        });
    }

    public void addExperience(int value) {
        currentExperience.set(currentExperience.intValue() + value);
        if(currentExperience.intValue() >= experienceForLevelUp) {
            currentLevel.set(currentLevel.getValue() + 1);
            currentExperience.set(currentExperience.intValue() - experienceForLevelUp + 100);
        }
    }

    public int getCurrentLevel() {
        return currentLevel.intValue();
    }

    public int getExperienceForLevelUp() {
        return experienceForLevelUp;
    }

    public SimpleIntegerProperty getCurrentExperience() {
        return (SimpleIntegerProperty) currentExperience;
    }

    public SimpleIntegerProperty getPointsToSpend() {
        return (SimpleIntegerProperty) pointsToSpend;
    }

    public void setPointsToSpend(int pointsToSpend) {
        this.pointsToSpend.set(pointsToSpend);
    }

    public void changePointsToSpend(int value) {
        pointsToSpend.set(pointsToSpend.intValue() + value);
    }

    @Override
    public String toString() {
        return (getCurrentExperience().intValue() + "/" + getExperienceForLevelUp() + " Level: " + getCurrentLevel());
    }
}
