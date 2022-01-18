package jakubwiraszka;

import jakubwiraszka.dialogs.DialogBuilder;
import jakubwiraszka.dialogs.FightInterfaceController;
import jakubwiraszka.gamefiles.*;
import jakubwiraszka.items.Item;
import jakubwiraszka.map.GameMapBuilder;
import jakubwiraszka.visuals.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.Random;

public class GameInterfaceController {

    private World world;
    private Location currentLocation;
    private GameMapBuilder gameMapBuilder;
    private Randomize randomize;
    private final DialogBuilder dialogBuilder = new DialogBuilder();
    @FXML
    private BorderPane gameBorderPane;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private StackPane gameMapStackPane;
    @FXML
    private HBox infoHBox;
    @FXML
    private Label locationNameLabel;
    @FXML
    private Label locationDescriptionLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Label actionSpaceLabel;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private Button rightButton;
    @FXML
    private Button leftButton;
    @FXML
    private Button firstActionButton;
    @FXML
    private Button secondActionButton;
    @FXML
    private VBox actionVBox;

    public void initialize() {
        gameBorderPane.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.W) {
                upButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.S) {
                downButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.D) {
                rightButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.A) {
                leftButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.Q) {
                firstActionButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.E) {
                secondActionButton.fire();
                ev.consume();
            } else if (ev.getCode() == KeyCode.I) {
                showInventory();
                ev.consume();
            }
        });
    }

    private void showInventory() {
        dialogBuilder.reset();
        dialogBuilder.setOwner(gameBorderPane);
        dialogBuilder.setTitle("Inventory");
        dialogBuilder.setSource("inventory.fxml");
        dialogBuilder.addOkButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        InventoryController inventoryController = dialogBuilder.getFxmlLoader().getController();
        inventoryController.setHero(world.getHero());
        inventoryController.setSubtraction(true);
        inventoryController.getNameTextField().setText(world.getHero().getName());
        inventoryController.getNameTextField().setEditable(false);
        world.getHero().getLevel().addLevelListener(inventoryController.getPointsToSpendGUI());
        dialog.showAndWait();
    }

    public void start() {
        world.handleVisited();
        currentLocation = world.findLocation(world.getHero().getPosition().getX(), world.getHero().getPosition().getY(), world.getLocations());
        Hero hero = world.getHero();

        locationNameLabel.setText(currentLocation.getName());
        locationDescriptionLabel.setText(currentLocation.getDescription());
        actionSpaceLabel.setText("\t\t\t\t\t\t\t\t\t\t\t\t");

        if(world.isEndlessMode() && !world.isStart()) {
            Position startPosition = world.getLocations().get(0).getPosition();
            Position furthestPosition = new Position(-1, -1);
            Position secondFurthestPosition = new Position(-1, -1);
            double furthestDistance = Double.MIN_VALUE;
            for(Location i: world.getLocations()) {
                double checkedDistance = (i.getPosition().getX() - startPosition.getX())*(i.getPosition().getX() - startPosition.getX()) + (i.getPosition().getY() - startPosition.getY())*(i.getPosition().getY() - startPosition.getY());
                if(i.getContent() == null) {
                    if (checkedDistance > furthestDistance) {
                        secondFurthestPosition = furthestPosition;
                        furthestDistance = checkedDistance;
                        furthestPosition = i.getPosition();
                    }
                }
            }
            Location portalLocation = world.findLocation(furthestPosition, world.getLocations());
            if(portalLocation != null) {
                portalLocation.setContent(world.getPortal());
                System.out.println("Added Portal to " + portalLocation.getPosition().toString());
            }
            Location bossLocation = world.findLocation(secondFurthestPosition, world.getLocations());
            if(bossLocation != null) {
                Enemy enemy = world.getBoss();
                enemy.setHealth(hero.getMaxHealth() * 2);
                enemy.setPower(hero.getStatistics().getPower());
                enemy.setAgility(hero.getStatistics().getAgility());
                bossLocation.setContent(enemy);
                System.out.println("Added " + enemy + " to " + bossLocation.getPosition());
            }
        }

        world.setStart(true);

        gameMapBuilder = new GameMapBuilder(world);
        gameMapStackPane.getChildren().addAll(gameMapBuilder.createGameMap(true));
        for(Location i: world.getLocations()) {
            i.addListener(gameMapBuilder);
        }

        randomize = new Randomize(world);

        gameScrollPane.setMaxWidth((world.getWidth() + 1) * 81);
        gameScrollPane.setMaxHeight((world.getHeight() + 1) * 81);
        scrollGameScrollPane(hero.getPosition(), gameScrollPane, world);

        gameMapBuilder.setPlayerPosition(hero.getPosition());

        HeroStatsGUI heroStatsGUI = new HeroStatsGUI(hero);
        hero.addEnemyListener(heroStatsGUI);

        LevelGUI levelGUI = new LevelGUI(hero);
        hero.getLevel().addLevelListener(levelGUI);

        PointsToSpendGUI pointsToSpendGUI = new PointsToSpendGUI(hero);
        hero.getLevel().addLevelListener(pointsToSpendGUI);

        infoHBox.getChildren().add(heroStatsGUI.getStatsHBox());
        infoHBox.getChildren().add(levelGUI.getLevelGUI());
        infoHBox.getChildren().add(pointsToSpendGUI.getPointsToSpendGUI());

        lockButtons();
        if (world.getHero().isAlive()) {
            unlockButtons();
        } else {
            messageLabel.setText("GAME OVER");
        }
    }

    void scrollGameScrollPane(Position position, ScrollPane scrollPane, World world) {
        scrollPane.setHmax(world.getWidth());
        scrollPane.setVmax(world.getHeight());
        scrollPane.setHvalue(position.getX());
        scrollPane.setVvalue(position.getY());
    }

    private void lockButtons() {
        firstActionButton.setDisable(true);
        secondActionButton.setDisable(true);
        upButton.setDisable(true);
        downButton.setDisable(true);
        leftButton.setDisable(true);
        rightButton.setDisable(true);
    }

    private void unlockButtons() {
        if (currentLocation.getExits().get("N") != null) {
            upButton.setDisable(false);
        }
        if (currentLocation.getExits().get("E") != null) {
            rightButton.setDisable(false);
        }
        if (currentLocation.getExits().get("W") != null) {
            leftButton.setDisable(false);
        }
        if (currentLocation.getExits().get("S") != null) {
            downButton.setDisable(false);
        }
    }

    @FXML
    public void move(ActionEvent e) {
        String direction = "Q";
        if (e.getSource().equals(upButton)) {
            direction = "N";
        } else if (e.getSource().equals(downButton)) {
            direction = "S";
        } else if (e.getSource().equals(rightButton)) {
            direction = "E";
        } else if (e.getSource().equals(leftButton)) {
            direction = "W";
        }

        Position position = currentLocation.getExits().get(direction);

        scrollGameScrollPane(position, gameScrollPane, world);

        world.getHero().setPosition(position);
        gameMapBuilder.setPlayerPosition(world.getHero().getPosition());

        currentLocation = world.findLocation(position.getX(), position.getY(), world.getLocations());

        randomize.addRandomContent(currentLocation);

        if (world.allEnemiesDead()) {
            world.getPortal().setActive(true);
        }

        if (world.findVisitedLocation(currentLocation.getPosition()) == null) {
            world.addVisited(currentLocation.getPosition());
            world.getHero().getLevel().addExperience(10);
            randomize.move();
        }

        locationNameLabel.setText(currentLocation.getName());
        locationDescriptionLabel.setText(currentLocation.getDescription());
        actionVBox.getChildren().clear();

        lockButtons();
        if (currentLocation.getContent() != null) {
            Label comeAcrossLabel = new Label("You come across " + currentLocation.getContent().getName());
            comeAcrossLabel.setFont(new Font("Arial bold", 20));
            actionVBox.getChildren().add(comeAcrossLabel);

            firstActionButton.setDisable(false);
            secondActionButton.setDisable(false);

            if (currentLocation.getContent().isEnemy()) {
                System.out.println("Enemy found at " + currentLocation.getPosition().toString());
                firstActionButton.setText("Attack");
                secondActionButton.setText("Run");
                Enemy enemy = (Enemy) currentLocation.getContent();
                actionVBox.getChildren().add(new StatsGUI(enemy).getStatsHBox());
            } else if (currentLocation.getContent().isTreasure()) {
                System.out.println("Treasure found at " + currentLocation.getPosition().toString());
                firstActionButton.setText("Take");
                secondActionButton.setText("Leave");
            } else if (currentLocation.getContent().isItem()) {
                System.out.println("Item found at " + currentLocation.getPosition().toString());
                firstActionButton.setText("Take");
                secondActionButton.setText("Leave");
            } else {
                System.out.println("Portal found at " + currentLocation.getPosition().toString());
                firstActionButton.setText("Jump");
                secondActionButton.setText("Leave");
                Portal portal = (Portal) currentLocation.getContent();
                if (!portal.isActive()) {
                    firstActionButton.setDisable(true);
                }
            }
        } else {
            if (world.getHero().isAlive()) {
                unlockButtons();
            } else {
                messageLabel.setText("GAME OVER");
                gameOver(gameBorderPane);
            }
        }
    }

    @FXML
    public void contentAppears(ActionEvent e) {
        if (currentLocation.getContent().isEnemy()) {
            Random random = new Random();

            Enemy enemy = (Enemy) currentLocation.getContent();

            int value = Math.max(world.getHero().getStatistics().getAgility() - enemy.getStatistics().getAgility(), 0);

            if (e.getSource().equals(secondActionButton)) {
                if ((random.nextInt(11) + value) >= 5 || currentLocation.getContent().equals(world.getBoss())) {
                    unlockButtons();
                    firstActionButton.setDisable(true);
                    secondActionButton.setDisable(true);
                    Label positiveEscapeLabel = new Label("\nYou have managed to escape!");
                    positiveEscapeLabel.setFont(new Font("Arial bold", 20));
                    actionVBox.getChildren().add(positiveEscapeLabel);
                } else {
                    Label negativeEscapeLabel = new Label("\nBetter luck next time!");
                    negativeEscapeLabel.setFont(new Font("Arial italic", 20));
                    actionVBox.getChildren().add(negativeEscapeLabel);
                    fightInitialize(enemy);
                }
            } else if (e.getSource().equals(firstActionButton)) {
                fightInitialize(enemy);
            }
        } else if(currentLocation.getContent().isTreasure() || currentLocation.getContent().isItem()) {
            if (e.getSource().equals(firstActionButton)) {
                lockButtons();
                take(currentLocation.getContent().isTreasure());
            } else if (e.getSource().equals(secondActionButton)) {
                leave("\nYou leave it!", "Arial italic");
            }
        } else {
            if(e.getSource().equals(firstActionButton)) {
                GameData.getWorlds().remove(world);
                newEndlessWorld(e, world.getDifficulty(), world.getName(), world.getHero());
            } else if (e.getSource().equals(secondActionButton)) {
                leave("\nYou leave portal alone", "Arial bold");
            }
        }
    }

    private void leave(String text, String font) {
        Label leaveLabel = new Label(text);
        leaveLabel.setFont(new Font(font, 20));
        actionVBox.getChildren().add(leaveLabel);
        unlockButtons();
        firstActionButton.setDisable(true);
        secondActionButton.setDisable(true);
    }

    private void fightInitialize(Enemy enemy) {
        lockButtons();
        System.out.println("Fight initialized");
        int experience = showFightDialog(enemy);
        if(!world.getHero().isAlive()) {
            gameOver(gameBorderPane);
        } else {
            world.getHero().getLevel().addExperience(experience);
            clearContent();
            actionVBox.getChildren().clear();
        }
    }

    private void take(boolean isTreasure) {
        Hero hero = world.getHero();
        Label contentLabel = new Label();
        contentLabel.setFont(new Font("Arial bold", 20));
        if(isTreasure) {
            Treasure treasure = (Treasure) currentLocation.getContent();

            hero.use(treasure.getContent());

            contentLabel.setText("You take " + treasure.getName() + " and get " + treasure.getContent().getValue() + " " + treasure.getContent().getStatistic());
        } else {
            Item item = (Item) currentLocation.getContent();
            hero.getInventory().add(item);
            contentLabel.setText("You take " + item.getName());
        }

        actionVBox.getChildren().add(contentLabel);

        if (hero.getStatistics().getHealth() <= 0) {
            hero.setAlive(false);
            lockButtons();

            Label loseLabel = new Label("\nYou died!");
            loseLabel.setFont(new Font("Arial bold", 20));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), event -> actionVBox.getChildren().add(loseLabel)),
                    new KeyFrame(Duration.seconds(1), event -> gameOver(gameBorderPane))
            );
            timeline.play();
        } else {
            clearContent();
        }
    }

    private void clearContent() {
        unlockButtons();
        firstActionButton.setDisable(true);
        secondActionButton.setDisable(true);
        world.removeContent(currentLocation.getContent());
    }

    public void gameOver(Node node) {
        NewWindow.changePane(node, "gameover.fxml");
    }

    public int showFightDialog(Enemy enemy) {
        dialogBuilder.reset();
        dialogBuilder.setOwner(gameBorderPane);
        dialogBuilder.setTitle("Fight");
        dialogBuilder.setSource("fightinterface.fxml");
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        FightInterfaceController controller = dialogBuilder.getFxmlLoader().getController();
        Hero hero = world.getHero();
        controller.setHero(hero);
        controller.setEnemy(enemy);
        controller.setHeroNameLabel(hero.getName());
        controller.setEnemyNameLabel(enemy.getName());

        HeroStatsGUI heroStatsGUI = new HeroStatsGUI(hero);
        hero.addEnemyListener(heroStatsGUI);
        controller.getHeroVBox().getChildren().add(heroStatsGUI.getStatsHBox());

        StatsGUI statsGUI = new StatsGUI(enemy);
        enemy.addEnemyListener(statsGUI);
        controller.getEnemyVBox().getChildren().add(statsGUI.getStatsHBox());

        GridPane attackGridPane = controller.getAttackGridPane();
        ApproxDamageGUI approxDamageGUI = new ApproxDamageGUI(hero, enemy);
        attackGridPane.add(approxDamageGUI.getQuickAttackGridPane(), 0, 1);
        attackGridPane.add(approxDamageGUI.getStrongAttackGridPane(), 1, 1);
        attackGridPane.add(approxDamageGUI.getChargedAttackGridPane(), 2, 1);
        dialog.showAndWait();
        return controller.getExperience();
    }

    @FXML
    public void mainMenu() {
        NewWindow.changePane(gameBorderPane, "mainmenu.fxml");
        GameData.save();
    }

    public void newEndlessWorld(ActionEvent event, Difficulty difficulty, String name, Hero hero) {
        World newWorld = new World(name, 15, 15, hero);
        newWorld.createStart();
        newWorld.createRandom(GameData.getRandomLocationName(), GameData.getRandomLocationDescription());
        newWorld.setEndlessMode(true);
        newWorld.setDifficulty(difficulty);
        GameData.getWorlds().add(newWorld);
        Node node = (Node) event.getSource();
        FXMLLoader fxmlLoader = NewWindow.changePane(node, "gameinterface.fxml");
        if(fxmlLoader != null) {
            GameInterfaceController gameInterfaceController = fxmlLoader.getController();
            gameInterfaceController.setWorld(newWorld);
            gameInterfaceController.start();
        }
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
