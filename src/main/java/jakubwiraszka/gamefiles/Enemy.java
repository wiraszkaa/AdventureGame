package jakubwiraszka.gamefiles;

import jakubwiraszka.fight.Attack;
import jakubwiraszka.fight.Randomize;
import jakubwiraszka.fight.StrongAttack;
import jakubwiraszka.items.Armor;
import jakubwiraszka.items.Weapon;
import jakubwiraszka.observable.EnemyListener;

import java.util.ArrayList;
import java.util.Random;

public class Enemy implements LocationContent {
    private String id;

    String name;
    private final Statistics statistics;
    private boolean alive;
    private Position position;
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private transient Attack attack;

    transient ArrayList<EnemyListener> enemyListeners;

    public Enemy(String name, Statistics statistics) {
        this.name = name;
        this.statistics = statistics;
        this.alive = true;
        this.setPosition(new Position(-1, -1));
        this.equippedWeapon = Weapon.HANDS;
        this.equippedArmor = Armor.CLOTHES;
        this.attack = new StrongAttack();

        this.enemyListeners = new ArrayList<>();
    }

    public double attack(Enemy enemy) {
        Random random = new Random();
        double hitChance = (attack.getHitChance() * equippedWeapon.getHitChance()) + (double) (getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0;
        boolean hit = Randomize.randomize(hitChance, 100);
        if(hit) {
            int attackValue = (int) (getStatistics().getPower() * attack.getPower() * equippedWeapon.getDamageMultiplier() * enemy.getEquippedArmor().getDamageMultiplier());
            int heroDeviation = Math.max((attackValue / 5), 1);
            attackValue += random.nextInt(heroDeviation * 2) - heroDeviation;
            enemy.changeHealth(-attackValue);
            return attackValue;
        } else {
            return -1;
        }
    }

    void notifyListeners() {
        if (!enemyListeners.isEmpty()) {
            for (EnemyListener i : enemyListeners) {
                i.update(statistics.getHealth(), statistics.getHealth(), statistics.getPower(), statistics.getAgility());
            }
        }
    }

    public void addEnemyListener(EnemyListener enemyListener) {
        enemyListeners.add(enemyListener);
    }

    public void setEnemyListeners(ArrayList<EnemyListener> enemyListeners) {
        this.enemyListeners = enemyListeners;
    }

    public void changeHealth(double value) {
        statistics.setHealth(statistics.getHealth() + value);
        notifyListeners();
    }

    public void setHealth(double value) {
        statistics.setHealth(value);
        notifyListeners();
    }

    public void changePower(int value) {
        statistics.setPower(statistics.getPower() + value);
        notifyListeners();
    }

    public void setPower(int value) {
        statistics.setPower(value);
        notifyListeners();
    }

    public void changeAgility(int value) {
        statistics.setAgility(statistics.getAgility() + value);
        notifyListeners();
    }

    public void setAgility(int value) {
        statistics.setAgility(value);
        notifyListeners();
    }

    public String getName() {
        return name;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return (getId() + ": \n"
                + "Health: " + getStatistics().getHealth() + " Power: " + getStatistics().getPower() + " Agility: " + getStatistics().getAgility());
    }

    @Override
    public boolean isEnemy() {
        return true;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isItem() {
        return false;
    }
}

