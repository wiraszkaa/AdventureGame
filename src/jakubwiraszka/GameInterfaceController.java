package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class GameInterfaceController implements ChangePane {

    private String worldName;
    private World world;
    private Location currentLocation;
    private boolean endlessMode;
    private Difficulty difficulty;
    @FXML
    private StackPane mainStackPane;
    @FXML
    private Button enterButton;
    @FXML
    private BorderPane gameBorderPane;
    @FXML
    private GridPane gameMapGridPane;
    @FXML
    private GridPane contentMapGridPane;
    @FXML
    private GridPane playerMapGridPane;
    @FXML
    private Label statisticsLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private HBox statsHBox;
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
    private Slider zoomSlider;

    final String ICONS_LOC = "D:\\Projekty\\Java\\AdventureFX\\icons\\";

    public void initialize() {
        gameBorderPane.setOpacity(0.2);
        spendPointsButton  = new Button();

        mainStackPane.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
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

    @FXML
    public void start() {
        mainStackPane.getChildren().remove(enterButton);
        gameBorderPane.setOpacity(1);

        this.world = GameData.getInstance().findWorld(worldName);
        world.setStart(true);
        world.handleVisited();
        currentLocation = world.findLocation(world.getHero().getPosition().getX(), world.getHero().getPosition().getY(), world.getLocations());

        statisticsLabel.setText(world.getHero().statsToString());
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
                Enemy enemy = world.findEnemy("Boss0");
                enemy.getStatistics().setHealth(world.getHero().getMaxHealth() * 2);
                enemy.getStatistics().setPower(world.getHero().getStatistics().getPower());
                bossLocation.setContent(enemy);
                System.out.println("Added " + enemy + " to " + bossLocation.getPosition());
            }
        }

        createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, true);

        getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Player.png"));

        world.getHero().getLevel().getPointsToSpend().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newNumber) {
                System.out.println("You have " + newNumber.intValue() + " points to spend");
                if(newNumber.intValue() > 0) {
                    spendPointsButton.setText("Spend Points");
                    spendPointsButton.setOnAction(event -> showHeroStatsDialog());
                    try {
                        statsHBox.getChildren().add(spendPointsButton);
                    } catch (Exception e) {
                        System.out.println("Spend Points Button already added");
                    }
                    world.getHero().getStatistics().setHealth(world.getHero().getMaxHealth());
                } else if(newNumber.intValue() == 0 && spendPointsButton.getParent().equals(statsHBox)) {
                    statsHBox.getChildren().remove(spendPointsButton);
                }
            }
        });

        world.getHero().getLevel().getCurrentExperience().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                levelLabel.setText(world.getHero().getLevel().toString());
            }
        });

        world.getHero().getStatistics().getHealth().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                statisticsLabel.setText(world.getHero().statsToString());
            }
        });

        lockButtons();
        if (world.getHero().isAlive()) {
            unlockButtons();
        } else {
            messageLabel.setText("GAME OVER");
        }

        makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }

    void makeZoomable(GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, Slider zoomSlider) {
        gameMapGridPane.scaleXProperty().bind(zoomSlider.valueProperty());
        gameMapGridPane.scaleYProperty().bind(zoomSlider.valueProperty());
        contentMapGridPane.scaleXProperty().bind(zoomSlider.valueProperty());
        contentMapGridPane.scaleYProperty().bind(zoomSlider.valueProperty());
        playerMapGridPane.scaleXProperty().bind(zoomSlider.valueProperty());
        playerMapGridPane.scaleYProperty().bind(zoomSlider.valueProperty());
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

    void createMap(World world, GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, boolean readVisited) {
        Iterator<Location> i = world.getLocations().iterator();
        while (i.hasNext()) {
            Location location = i.next();

            ImageView imageViewMap = new ImageView();
            modifyMapCell(location, imageViewMap, readVisited);
            GridPane.setConstraints(imageViewMap, location.getPosition().getX(), location.getPosition().getY());
            gameMapGridPane.getChildren().add(imageViewMap);

            ImageView imageViewContent = new ImageView();
            modifyContentCell(location, imageViewContent, world, readVisited);
            GridPane.setConstraints(imageViewContent, location.getPosition().getX(), location.getPosition().getY());
            contentMapGridPane.getChildren().add(imageViewContent);

            ImageView imageViewPlayer = new ImageView();
            imageViewPlayer.setImage(new Image(ICONS_LOC + "Nothing.png"));
            GridPane.setConstraints(imageViewPlayer, location.getPosition().getX(), location.getPosition().getY());
            playerMapGridPane.getChildren().add(imageViewPlayer);
        }
    }

    public void modifyMapCell(Location location, ImageView imageView, boolean readVisited) {
        if (location.isVisited() || !readVisited) {
            boolean north = false;
            boolean south = false;
            boolean east = false;
            boolean west = false;
            if (location.getExits().get("N") != null) {
                north = true;
            }
            if (location.getExits().get("S") != null) {
                south = true;
            }
            if (location.getExits().get("E") != null) {
                east = true;
            }
            if (location.getExits().get("W") != null) {
                west = true;
            }
            if (north && south && east && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road4.png"));
            } else if (north && south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(90);
            } else if (north && south && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(-90);
            } else if (west && south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
                imageView.setRotate(180);
            } else if (north && west && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road3.png"));
            } else if (north && south) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.1.png"));
            } else if (east && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.1.png"));
                imageView.setRotate(90);
            } else if (north && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
            } else if (south && east) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(90);
            } else if (south && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(180);
            } else if (north && west) {
                imageView.setImage(new Image(ICONS_LOC + "Road2.2.png"));
                imageView.setRotate(-90);
            } else if (north) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
            } else if (east) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(90);
            } else if (south) {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(180);
            } else {
                imageView.setImage(new Image(ICONS_LOC + "Road1.png"));
                imageView.setRotate(-90);
            }
        } else {
            imageView.setImage(new Image(ICONS_LOC + "Unknown.png"));
        }
        imageView.setFitHeight(81);
        imageView.setPreserveRatio(true);
    }

    public void modifyContentCell(Location location, ImageView imageView, World world, boolean readVisited) {
        if (location.isVisited() || !readVisited) {
            if (location.getContent() != null) {
                if (location.getContent().isEnemy()) {
                    imageView.setImage(new Image(ICONS_LOC + "Enemy.png"));
                } else if (location.getContent().isTreasure()) {
                    imageView.setImage(new Image(ICONS_LOC + "Chest.png"));
                } else {
                    imageView.setImage(new Image(ICONS_LOC + "Portal.png"));
                }
            }
        }
        imageView.setFitHeight(81);
        imageView.setFitWidth(81);
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
        if(position != null) {
            world.getHero().setPosition(position);

            getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));

            if (currentLocation.getContent() == null) {
                getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));
            } else if (currentLocation.getContent().isTreasure()) {
                getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Chest.png"));
            } else if (currentLocation.getContent().isEnemy()) {
                getImageView(contentMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Enemy.png"));
            }

            currentLocation = world.findLocation(position.getX(), position.getY(), world.getLocations());

            getImageView(playerMapGridPane, currentLocation.getPosition().getX(), currentLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Player.png"));

            if (world.allEnemiesDead()) {
                world.getPortal().setActive(true);
            }

            if (world.findVisitedLocation(currentLocation.getPosition()) == null) {
                world.addVisited(currentLocation.getPosition());

                ImageView mapImageView = getImageView(gameMapGridPane, position.getX(), position.getY());
                modifyMapCell(currentLocation, mapImageView, true);

                ImageView contentImageView = getImageView(contentMapGridPane, position.getX(), position.getY());
                modifyContentCell(currentLocation, contentImageView, world, true);

                world.getHero().getLevel().addExperience(10);
                levelLabel.setText(world.getHero().getLevel().toString());
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
                    Label statsLabel = new Label(enemy.getStatistics().toString());
                    statsLabel.setFont(new Font("Arial italic", 17));
                    actionVBox.getChildren().add(statsLabel);
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
                    gameOver(mainStackPane);
                }
            }
        } else {
            System.out.println("You can't go in that direction");
        }
    }

    ImageView getImageView(GridPane gridPane, int x, int y) {
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if (GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                return (ImageView) node;
            }
        }

        return null;
    }

    @FXML
    public void contentAppears(ActionEvent e) {
        if (currentLocation.getContent().isEnemy()) {
            Random random = new Random();

            Enemy enemy = (Enemy) currentLocation.getContent();

            int value = Math.max(world.getHero().getStatistics().getAgility() - enemy.getStatistics().getAgility(), 0);

            if (e.getSource().equals(secondActionButton)) {
                if ((random.nextInt(11) + value) >= 5) {
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
                        gameOver(mainStackPane);
                    } else {
                        world.getHero().getLevel().addExperience(experience);
                        clearContent();
                    }
                }
            } else if (e.getSource().equals(firstActionButton)) {
                lockButtons();
                System.out.println("Fight initialized");
                int experience = showFightDialog(enemy);
                statisticsLabel.setText(world.getHero().statsToString());
                if(!world.getHero().isAlive()) {
                    gameOver(mainStackPane);
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
                GameData.getInstance().getWorlds().remove(world);
                newEndlessWorld(e, getDifficulty(), worldName, world.getHero());
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

    public void newEndlessWorld(ActionEvent event, Difficulty difficulty, String name, Hero hero) {
        World newWorld = new World(name, 15, 15, hero);
        newWorld.createRandom(GameData.getInstance().getRandomLocationName(), GameData.getInstance().getRandomLocationDescription(), GameData.getInstance().getRandomLocationContent(), difficulty);
        GameData.getInstance().getWorlds().add(newWorld);
        Node node = (Node) event.getSource();
        GameInterfaceController gameInterfaceController = changePane(node, "gameinterface.fxml").getController();
        gameInterfaceController.setWorldName(name);
        gameInterfaceController.setEndlessMode(true);
        gameInterfaceController.setDifficulty(difficulty);
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
        statisticsLabel.setText(world.getHero().statsToString());

        if (hero.getStatistics().getHealthValue() <= 0) {
            hero.setAlive(false);
            lockButtons();

            Label loseLabel = new Label("\nYou died!");
            loseLabel.setFont(new Font("Arial bold", 20));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0), event -> actionVBox.getChildren().add(loseLabel)),
                    new KeyFrame(Duration.seconds(1), event -> gameOver(mainStackPane))
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
        changePane(node, "gameover.fxml");
    }

    public void showHeroStatsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Change Statistics");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "herostatsdialog.fxml", true, false);
        if (fxmlLoader == null) return;
        HeroStatsDialogController controller = fxmlLoader.getController();
        Hero hero = world.getHero();;
        controller.setHero(hero);
        controller.setHealthLabel("" + hero.getMaxHealth());
        controller.setPowerLabel("" + hero.getStatistics().getPower());
        controller.setAgilityLabel("" + hero.getStatistics().getAgility());
        controller.setSubtraction(true);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            statisticsLabel.setText(hero.statsToString());
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public int showFightDialog(Enemy enemy) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Fight");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "fightinterface.fxml", false, false);
        if (fxmlLoader == null) return -1;
        FightInterfaceController controller = fxmlLoader.getController();
        Hero hero = world.getHero();
        controller.setHero(hero);
        controller.setEnemy(enemy);
        controller.setHeroNameLabel(hero.getName());
        controller.setHeroHealthLabel("" + hero.getStatistics().getHealthValue());
        controller.setHeroPowerLabel("" + hero.getStatistics().getPower());
        controller.setHeroAgilityLabel("" + hero.getStatistics().getAgility());
        controller.setEnemyNameLabel(enemy.getName());
        controller.setEnemyHealthLabel("" + enemy.getStatistics().getHealthValue());
        controller.setEnemyPowerLabel("" + enemy.getStatistics().getPower());
        controller.setEnemyAgilityLabel("" + enemy.getStatistics().getAgility());
        controller.setQuickAttackLabel("Aprox. Dmg: " + (int) (hero.getStatistics().getPower() * 0.6) +
                                "\nHit Chance: " + (int) ((0.8 + (double) (hero.getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0) * 100) + "%");
        controller.setStrongAttackLabel("Aprox. Dmg: " + hero.getStatistics().getPower() +
                "\nHit Chance: " + (int) ((0.5 + (double) (hero.getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0) * 100) + "%");
        controller.setChargedAttackLabel("Aprox. Dmg: " + (int) (hero.getStatistics().getPower() * 2.2) +
                "\nHit Chance: " + (int) ((0.6 + (double) (hero.getStatistics().getAgility() - enemy.getStatistics().getAgility()) / 50.0) * 100) + "%");
        dialog.showAndWait();
        return controller.getExperience();
    }

    public FXMLLoader getFxmlLoader(Dialog<ButtonType> dialog, String name, boolean okButton, boolean cancelButton) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(name));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return null;
        }

        if(okButton) {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        }
        if(cancelButton) {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }
        return fxmlLoader;
    }

    @FXML
    public void mainMenu() {
        changePane(mainStackPane, "mainmenu.fxml");
        GameData.getInstance().saveAll();
    }

    @Override
    public FXMLLoader changePane(Node node, String name) {
        return ChangePane.super.changePane(node, name);
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public boolean isEndlessMode() {
        return endlessMode;
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
