package jakubwiraszka;

import jakubwiraszka.dialogs.DialogBuilder;
import jakubwiraszka.dialogs.FightInterfaceController;
import jakubwiraszka.dialogs.HeroStatsDialogController;
import jakubwiraszka.gamefiles.*;
import jakubwiraszka.visuals.ApproxDamageGUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jakubwiraszka.visuals.StatsGUI;
import java.util.Objects;
import java.util.Random;

public class GameInterfaceController {

    private World world;
    private Location currentLocation;
    private boolean endlessMode;
    private Difficulty difficulty;
    private final DialogBuilder dialogBuilder = new DialogBuilder();
    @FXML
    private BorderPane gameBorderPane;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private GridPane gameMapGridPane;
    @FXML
    private GridPane contentMapGridPane;
    @FXML
    private GridPane playerMapGridPane;
    @FXML
    private Label levelLabel;
    @FXML
    private HBox infoHBox;
    @FXML
    private Button spendPointsButton;
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
    @FXML
    private HBox statsHBox;

    public void initialize() {
        spendPointsButton  = new Button();

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
            }
        });
    }

    public void start() {
        world.setStart(true);
        world.handleVisited();
        currentLocation = world.findLocation(world.getHero().getPosition().getX(), world.getHero().getPosition().getY(), world.getLocations());

        locationNameLabel.setText(currentLocation.getName());
        locationDescriptionLabel.setText(currentLocation.getDescription());
        actionSpaceLabel.setText("\t\t\t\t\t\t\t\t\t\t\t\t");

        if(endlessMode) {
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
                enemy.getStatistics().setHealth(world.getHero().getMaxHealth() * 2);
                enemy.getStatistics().setPower(world.getHero().getStatistics().getPowerValue());
                enemy.getStatistics().setAgility(world.getHero().getStatistics().getAgilityValue());
                bossLocation.setContent(enemy);
                System.out.println("Added " + enemy + " to " + bossLocation.getPosition());
            }
        }

        CreateMap.createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, true);

        gameScrollPane.setMaxWidth((world.getWidth() + 1) * 81);
        gameScrollPane.setMaxHeight((world.getHeight() + 1) * 81);

        CreateMap.getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Player.png"));

        world.getHero().getLevel().getPointsToSpend().addListener((observableValue, number, newNumber) -> {
            System.out.println("You have " + newNumber.intValue() + " points to spend");
            if(newNumber.intValue() == 1) {
                spendPointsButton.setText("Spend Points");
                spendPointsButton.setOnAction(event -> showHeroStatsDialog());
                infoHBox.getChildren().add(spendPointsButton);
                world.getHero().getStatistics().setHealth(world.getHero().getMaxHealth());
            } else if(newNumber.intValue() == 0 && spendPointsButton.getParent().equals(infoHBox)) {
                infoHBox.getChildren().remove(spendPointsButton);
            }
        });

        world.getHero().getLevel().getCurrentExperience().addListener((observableValue, number, t1) -> levelLabel.setText(world.getHero().getLevel().toString()));

        lockButtons();
        if (world.getHero().isAlive()) {
            unlockButtons();
        } else {
            messageLabel.setText("GAME OVER");
        }

        StatsGUI statsGUI = new StatsGUI(world.getHero());
        statsHBox.getChildren().add(statsGUI.getStatsGUI());
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

        if(position != null) {
            world.getHero().setPosition(position);

            CreateMap.getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));

            if (currentLocation.getContent() == null) {
                CreateMap.getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
            } else if (currentLocation.getContent().isTreasure()) {
                CreateMap.getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Chest.png"));
            } else if (currentLocation.getContent().isEnemy()) {
                CreateMap.getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Enemy.png"));
            }

            currentLocation = world.findLocation(position.getX(), position.getY(), world.getLocations());
            CreateMap.getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Player.png"));

            if (world.allEnemiesDead()) {
                world.getPortal().setActive(true);
            }

            if (world.findVisitedLocation(currentLocation.getPosition()) == null) {
                world.addVisited(currentLocation.getPosition());

                ImageView mapImageView = CreateMap.getImageView(gameMapGridPane, position.getX(), position.getY());
                CreateMap.modifyMapCell(currentLocation, mapImageView, true);

                ImageView contentImageView = CreateMap.getImageView(contentMapGridPane, position.getX(), position.getY());
                CreateMap.modifyContentCell(currentLocation, contentImageView, true);

                world.getHero().getLevel().addExperience(10);
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
                    actionVBox.getChildren().add(new StatsGUI(enemy).getStatsGUI());
                } else if (currentLocation.getContent().isTreasure()) {
                    System.out.println("Treasure found at " + currentLocation.getPosition().toString());
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
        } else {
            System.out.println("You can't go in that direction");
        }
    }

    @FXML
    public void contentAppears(ActionEvent e) {
        if (currentLocation.getContent().isEnemy()) {
            Random random = new Random();

            Enemy enemy = (Enemy) currentLocation.getContent();

            int value = Math.max(world.getHero().getStatistics().getAgilityValue() - enemy.getStatistics().getAgilityValue(), 0);

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
                    lockButtons();
                    System.out.println("Fight initialized");
                    int experience = showFightDialog(enemy);
                    if(!world.getHero().isAlive()) {
                        gameOver(gameBorderPane);
                    } else {
                        world.getHero().getLevel().addExperience(experience);
                        clearContent();
                    }
                }
            } else if (e.getSource().equals(firstActionButton)) {
                lockButtons();
                System.out.println("Fight initialized");
                int experience = showFightDialog(enemy);
                if(!world.getHero().isAlive()) {
                    gameOver(gameBorderPane);
                } else {
                    world.getHero().getLevel().addExperience(experience);
                    clearContent();
                }
            }
        } else if(currentLocation.getContent().isTreasure()) {
            if (e.getSource().equals(firstActionButton)) {
                lockButtons();
                take();
            } else if (e.getSource().equals(secondActionButton)) {
                Label leaveLabel = new Label("\nYou leave it!");
                leaveLabel.setFont(new Font("Arial italic", 20));
                actionVBox.getChildren().add(leaveLabel);
                unlockButtons();
                firstActionButton.setDisable(true);
                secondActionButton.setDisable(true);
            }
        } else {
            if(e.getSource().equals(firstActionButton)) {
                GameData.getWorlds().remove(world);
                newEndlessWorld(e, getDifficulty(), world.getName(), world.getHero());
            } else if (e.getSource().equals(secondActionButton)) {
                Label leaveLabel = new Label("\nYou leave portal alone");
                leaveLabel.setFont(new Font("Arial bold", 20));
                actionVBox.getChildren().add(leaveLabel);
                unlockButtons();
                firstActionButton.setDisable(true);
                secondActionButton.setDisable(true);
            }
        }
    }

    private void take() {
        Hero hero = world.getHero();
        Treasure treasure = (Treasure) currentLocation.getContent();

        String statistic = treasure.getContent().getStatistic();
        if (Objects.equals(statistic, "Health")) {
            hero.changeHealth(treasure.getContent().getValue());
            hero.addMaxHealth(treasure.getContent().getValue());
        } else if (Objects.equals(statistic, "Power")) {
            hero.changePower(treasure.getContent().getValue());
        } else if (Objects.equals(statistic, "Agility")) {
            hero.changeAgility(treasure.getContent().getValue());
        }

        Label contentLabel = new Label("You take " + treasure.getName() + " and get " + treasure.getContent().getValue() + " " + statistic);
        contentLabel.setFont(new Font("Arial bold", 20));
        actionVBox.getChildren().add(contentLabel);

        if (hero.getStatistics().getHealthValue() <= 0) {
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
        currentLocation.setContent(null);
    }

    public void gameOver(Node node) {
        NewWindow.changePane(node, "gameover.fxml");
    }

    public void showHeroStatsDialog() {
        dialogBuilder.reset();
        dialogBuilder.setOwner(gameBorderPane);
        dialogBuilder.setTitle("Change Statistics");
        dialogBuilder.setSource("herostatsdialog.fxml");
        dialogBuilder.addOkButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        HeroStatsDialogController controller = dialogBuilder.getFxmlLoader().getController();
        Hero hero = world.getHero();
        controller.setHero(hero);
        controller.setHealthLabel("" + hero.getMaxHealth());
        controller.setPowerLabel("" + hero.getStatistics().getPowerValue());
        controller.setAgilityLabel("" + hero.getStatistics().getAgilityValue());
        controller.setSubtraction(true);
        dialog.showAndWait();
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
        controller.getHeroVBox().getChildren().add(new StatsGUI(hero).getStatsGUI());
        controller.setEnemyNameLabel(enemy.getName());
        controller.getEnemyVBox().getChildren().add(new StatsGUI(enemy).getStatsGUI());
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
        GameData.saveAll();
    }

    public void newEndlessWorld(ActionEvent event, Difficulty difficulty, String name, Hero hero) {
        World newWorld = new World(name, 15, 15, hero);
        newWorld.createRandom(GameData.getRandomLocationName(), GameData.getRandomLocationDescription(), GameData.getRandomLocationContent(), difficulty);
        GameData.getWorlds().add(newWorld);
        Node node = (Node) event.getSource();
        GameInterfaceController gameInterfaceController = NewWindow.changePane(node, "gameinterface.fxml").getController();
        gameInterfaceController.setWorld(newWorld);
        gameInterfaceController.setEndlessMode(true);
        gameInterfaceController.setDifficulty(difficulty);
        gameInterfaceController.start();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setEndlessMode(boolean endlessMode) {
        this.endlessMode = endlessMode;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
