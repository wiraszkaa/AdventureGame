package jakubwiraszka.gamefiles;

import jakubwiraszka.observable.Listener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;

public class Level {
    private int currentLevel;
    private int experienceForLevelUp;
    private int currentExperience;
    private final IntegerProperty pointsToSpend;

    private transient ArrayList<Listener> levelListeners;

    public Level(int level) {
        currentLevel  = level;
        pointsToSpend = new SimpleIntegerProperty();
        currentExperience = 0;
        experienceForLevelUp = level * 100;

        levelListeners = new ArrayList<>();
    }

    public void addExperience(int value) {
        currentExperience += value;
        if(currentExperience >= experienceForLevelUp) {
            currentLevel += 1;
            currentExperience -= experienceForLevelUp;
            experienceForLevelUp = currentLevel * 100;
            pointsToSpend.set(pointsToSpend.intValue() + 1);
        }
        notifyListeners();
    }

     private void notifyListeners() {
         if (!levelListeners.isEmpty()) {
             for (Listener i : levelListeners) {
                 i.update(currentExperience, experienceForLevelUp, currentLevel, pointsToSpend.intValue(), null);
             }
         }
     }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getExperienceForLevelUp() {
        return experienceForLevelUp;
    }

    public int getCurrentExperience() {
        return currentExperience;
    }

    public SimpleIntegerProperty getPointsToSpend() {
        return (SimpleIntegerProperty) pointsToSpend;
    }

    public void setPointsToSpend(int pointsToSpend) {
        this.pointsToSpend.set(pointsToSpend);
        notifyListeners();
    }

    public void changePointsToSpend(int value) {
        pointsToSpend.set(pointsToSpend.intValue() + value);
        notifyListeners();
    }

    public void addLevelListener(Listener levelListener) {
        levelListeners.add(levelListener);
    }

    public void setLevelListeners(ArrayList<Listener> levelListeners) {
        this.levelListeners = levelListeners;
    }

    @Override
    public String toString() {
        return (getCurrentExperience() + "/" + getExperienceForLevelUp() + " Level: " + getCurrentLevel());
    }
}
