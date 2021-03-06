package jakubwiraszka.visuals;

import jakubwiraszka.gamefiles.Hero;
import jakubwiraszka.gamefiles.Location;
import javafx.scene.control.Label;

public class HeroStatsGUI extends StatsGUI {

    private final Label maxHealthValue;

    public HeroStatsGUI(Hero hero) {
        super(hero);
        this.maxHealthValue = new Label("/ " + hero.getMaxHealth());
        this.maxHealthValue.setStyle("-fx-font-family: 'Limelight', regular; -fx-font-size: 40");
        getHealthHBox().getChildren().add(maxHealthValue);
    }

    @Override
    public void update(double health, double maxHealth, int power, int agility, Location location) {
        super.update(health, maxHealth, power, agility, location);
        maxHealthValue.setText("/ " + maxHealth);
    }
}
