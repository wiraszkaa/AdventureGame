package jakubwiraszka;

import jakubwiraszka.dialogs.*;
import jakubwiraszka.gamefiles.*;
import jakubwiraszka.map.GameMapBuilder;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.Optional;

public class ModifyInterfaceController extends GameInterfaceController {

    private World world;
    private final DialogBuilder dialogBuilder = new DialogBuilder();
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label locationDescriptionLabel;
    @FXML
    private Label locationNameLabel;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private StackPane mapStackPane;
    @FXML
    private ListView<Location> locationsListView;
    @FXML
    private ListView<Enemy> enemiesListView;
    @FXML
    private ListView<Treasure> treasuresListView;
    @FXML
    private ContextMenu locationContextMenu;
    @FXML
    private GridPane buttonGridPane;

    public void initialize() {
    }

    public void start() {
        locationsListView.setItems(world.getLocations());
        enemiesListView.setItems(world.getEnemies());
        treasuresListView.setItems(world.getTreasures());

        GameMapBuilder gameMapBuilder = new GameMapBuilder(world);
        mapStackPane.getChildren().addAll(gameMapBuilder.createGameMap(false));
        for(Location i: world.getLocations()) {
            i.addListener(gameMapBuilder);
        }

        ContextMenu enemiesContextMenu = createContextMenu(enemiesListView);
        ContextMenu treasuresContextMenu = createContextMenu(treasuresListView);
        locationContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            Location location = locationsListView.getSelectionModel().getSelectedItem();
            remove(location);
        });
        locationContextMenu.getItems().addAll(deleteMenuItem);

        locationsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Location> call(ListView<Location> deletableListView) {
                ListCell<Location> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Location location, boolean empty) {
                        super.updateItem(location, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(location.toString());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(locationContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

        enemiesListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Enemy> call(ListView<Enemy> enemyListView) {
                ListCell<Enemy> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Enemy deletable, boolean empty) {
                        super.updateItem(deletable, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(deletable.toString());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(enemiesContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

        treasuresListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Treasure> call(ListView<Treasure> treasuresListView) {
                ListCell<Treasure> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Treasure deletable, boolean empty) {
                        super.updateItem(deletable, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(deletable.toString());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(treasuresContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

        locationsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, location, newLocation) -> {
            gameMapBuilder.setPlayerPosition(newLocation.getPosition());
            scrollGameScrollPane(newLocation.getPosition(), gameScrollPane, world);
        });
    }

    @FXML
    public void showNewEnemyDialog() {
        dialogBuilder.reset();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Add New Enemy");
        dialogBuilder.setSource("newenemydialog.fxml");
        dialogBuilder.addOkButton();
        dialogBuilder.addCancelButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewEnemyDialogController controller = dialogBuilder.getFxmlLoader().getController();
            controller.setWorld(world);
            Enemy newEnemy = controller.newEnemy();
            enemiesListView.getSelectionModel().select(newEnemy);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void showNewTreasureDialog() {
        dialogBuilder.reset();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Add New Treasure");
        dialogBuilder.setSource("newtreasuredialog.fxml");
        dialogBuilder.addOkButton();
        dialogBuilder.addCancelButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewTreasureDialogController controller = dialogBuilder.getFxmlLoader().getController();
            controller.setWorld(world);
            Treasure newTreasure = controller.newTreasure();
            treasuresListView.getSelectionModel().select(newTreasure);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void addButtonGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        for(Location i: world.getLocations()) {
            if(i.getExits().get("N") == null) {
                if(i.getPosition().getY() - 1 >= 0) {
                    Button buttonN = new Button();
                    buttonN.setPrefHeight(81);
                    buttonN.setPrefWidth(81);
                    buttonN.setOnAction(event -> showNewLocationDialog(buttonN));
                    gridPane.add(buttonN, i.getPosition().getX(), (i.getPosition().getY() - 1));
                }
            }
            if(i.getExits().get("E") == null) {
                if(i.getPosition().getX() + 1 <= world.getWidth()) {
                    Button buttonE = new Button();
                    buttonE.setPrefHeight(81);
                    buttonE.setPrefWidth(81);
                    buttonE.setOnAction(event -> showNewLocationDialog(buttonE));
                    gridPane.add(buttonE, (i.getPosition().getX() + 1), i.getPosition().getY());
                }
            }
            if(i.getExits().get("W") == null) {
                if(i.getPosition().getX() - 1 >= 0) {
                    Button buttonW = new Button();
                    buttonW.setPrefHeight(81);
                    buttonW.setPrefWidth(81);
                    buttonW.setOnAction(event -> showNewLocationDialog(buttonW));
                    gridPane.add(buttonW, (i.getPosition().getX() - 1), i.getPosition().getY());
                }
            }
            if(i.getExits().get("S") == null) {
                if(i.getPosition().getY() + 1 <= world.getHeight()) {
                    Button buttonS = new Button();
                    buttonS.setPrefHeight(81);
                    buttonS.setPrefWidth(81);
                    buttonS.setOnAction(event -> showNewLocationDialog(buttonS));
                    gridPane.add(buttonS, i.getPosition().getX(), (i.getPosition().getY() + 1));
                }
            }
        }
        mapStackPane.getChildren().add(gridPane);
        buttonGridPane = gridPane;

        Delay.createDelay(2, event -> mapStackPane.getChildren().remove(gridPane));
    }

    @FXML
    private void showNewLocationDialog(Node node) {
        mapStackPane.getChildren().remove(buttonGridPane);
        int x = GridPane.getColumnIndex(node);
        int y = GridPane.getRowIndex(node);
        dialogBuilder.reset();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Add New Location");
        dialogBuilder.setSource("newlocationdialog.fxml");
        dialogBuilder.addOkButton();
        dialogBuilder.addCancelButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewLocationDialogController controller = dialogBuilder.getFxmlLoader().getController();
            controller.setPosition(new Position(x, y));
            controller.setWorld(world);
            Location newLocation = controller.newLocation();
            locationsListView.getSelectionModel().select(newLocation);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditEnemyDialog(Enemy enemy) {
        dialogBuilder.reset();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Edit Enemy");
        dialogBuilder.setSource("newenemyialog.fxml");
        dialogBuilder.addOkButton();
        dialogBuilder.addCancelButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        NewEnemyDialogController controller = dialogBuilder.getFxmlLoader().getController();
        controller.setWorld(world);
        controller.setNameTextField(enemy.getName());
        controller.setHealthSpinnerValue((int) enemy.getStatistics().getHealth());
        controller.setPowerSpinnerValue(enemy.getStatistics().getPower());
        controller.setAgilitySpinnerValue(enemy.getStatistics().getAgility());
        controller.blockNameTextField();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateEnemy(enemy);
            System.out.println(enemy + " was updated");
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditTreasureDialog(Treasure treasure) {
        dialogBuilder.reset();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Edit Treasure");
        dialogBuilder.setSource("newtreasuredialog.fxml");
        dialogBuilder.addOkButton();
        dialogBuilder.addCancelButton();
        Dialog<ButtonType> dialog = dialogBuilder.getDialog();
        NewTreasureDialogController controller = dialogBuilder.getFxmlLoader().getController();
        controller.setWorld(world);
        controller.setNameTextField(treasure.getName());
        controller.setRadioButton(treasure.getContent().getStatistic());
        controller.setValueSpinner(treasure.getContent().getValue());
        controller.blockNameTextField();
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateTreasure(treasure);
            System.out.println(treasure + " was updated");
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    private ContextMenu createContextMenu(ListView<? extends LocationContent> listView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            String id = listView.getSelectionModel().getSelectedItem().getId();
            if (world.findEnemy(id) != null) {
                remove(world.findEnemy(id));
            } else if (world.findTreasure(id) != null) {
                remove(world.findTreasure(id));
            }
        });

        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> {
            String id = listView.getSelectionModel().getSelectedItem().getId();
            if (world.findEnemy(id) != null) {
                edit(world.findEnemy(id));
                enemiesListView.getItems().set(enemiesListView.getSelectionModel().getSelectedIndex(), world.findEnemy(id));
            } else if (world.findTreasure(id) != null) {
                edit(world.findTreasure(id));
                treasuresListView.getItems().set(treasuresListView.getSelectionModel().getSelectedIndex(), world.findTreasure(id));
            }
        });

        contextMenu.getItems().addAll(deleteMenuItem);
        contextMenu.getItems().addAll(editMenuItem);

        return contextMenu;
    }

    @FXML
    public void remove(LocationContent locationContent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + locationContent.getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)) {
            world.removeContent(locationContent);
        }
    }

    @FXML
    public void remove(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + location.getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)) {
            world.removeLocation(location);
        }
    }

    @FXML
    public void edit(LocationContent locationContent) {
        if (locationContent instanceof Enemy) {
            showEditEnemyDialog((Enemy) locationContent);
        } else if (locationContent instanceof Treasure) {
            showEditTreasureDialog((Treasure) locationContent);
        }
    }

    @FXML
    public void exit() {
        NewWindow.changePane(mainBorderPane, "mainmenu.fxml");
        GameData.save();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    void scrollGameScrollPane(Position position, ScrollPane scrollPane, World world) {
        super.scrollGameScrollPane(position, scrollPane, world);
    }
}
