package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Optional;

public class ModifyInterfaceController extends GameInterfaceController {

    private World world;
    private final NewWindow newWindow = new NewWindow();
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label locationDescriptionLabel;
    @FXML
    private Label locationNameLabel;
    @FXML
    private StackPane mapStackPane;
    @FXML
    private GridPane gameMapGridPane;
    @FXML
    private GridPane contentMapGridPane;
    @FXML
    private GridPane playerMapGridPane;
    @FXML
    private ListView<Location> locationsListView;
    @FXML
    private ListView<LocationContent> enemiesListView;
    @FXML
    private ListView<LocationContent> treasuresListView;
    @FXML
    private ContextMenu locationContextMenu;
    @FXML
    private Slider zoomSlider;

    public void initialize() {
    }

    public void start() {
        locationsListView.setItems(world.getLocations());
        enemiesListView.setItems(world.getEnemies());
        treasuresListView.setItems(world.getTreasures());

        CreateMap.createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, false);

        createContextMenu(enemiesListView);
        createContextMenu(treasuresListView);
        locationContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            Location location = locationsListView.getSelectionModel().getSelectedItem();
            delete(location);
        });

        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> {
            Location location = locationsListView.getSelectionModel().getSelectedItem();
            edit(location);
        });

        locationContextMenu.getItems().addAll(deleteMenuItem);
        locationContextMenu.getItems().addAll(editMenuItem);

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

        locationsListView.getItems().addListener((ListChangeListener<Location>) change -> {
            while (change.next()) {
                for (Location removedLocation : change.getRemoved()) {
                    CreateMap.getImageView(gameMapGridPane, removedLocation.getPosition().getX(), removedLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Unknown.png"));
                }
                for (Location addedLocation : change.getAddedSubList()) {
                    locationsListView.getSelectionModel().select(addedLocation);
                }
            }
        });

        locationsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, location, newLocation) -> {
            if (newLocation != null) {
                CreateMap.getImageView(playerMapGridPane, newLocation.getPosition().getX(), newLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Player.png"));
                locationNameLabel.setText(newLocation.getName());
                locationDescriptionLabel.setText(newLocation.getDescription());
                if (location != null) {
                    CreateMap.getImageView(playerMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
                }
            }
        });


        enemiesListView.getItems().addListener((ListChangeListener<LocationContent>) change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    System.out.println("Change!");
                    enemiesListView.setItems(world.getEnemies());
                } else {
                    for (LocationContent removedEnemy : change.getRemoved()) {
                        System.out.println(removedEnemy.getId() + " was removed");
                        Position position = removedEnemy.getPosition();
                        if (position.getX() != -1 && position.getY() != -1) {
                            CreateMap.getImageView(contentMapGridPane, position.getX(), position.getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
                        }
                    }
                    for (LocationContent addedEnemy : change.getAddedSubList()) {
                        System.out.println(addedEnemy.getId() + " was added");
                        enemiesListView.getSelectionModel().select(addedEnemy);
                    }
                }
            }
        });

        makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }

    @FXML
    public void showNewEnemyDialog() {
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Add New Enemy", "Use this dialog to add new enemy", "newenemydialog.fxml", true, true);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewEnemyDialogController controller = newWindow.getFxmlLoader().getController();
            controller.setWorld(world);
            Enemy newEnemy = controller.newEnemy();
            enemiesListView.getSelectionModel().select(newEnemy);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void showNewTreasureDialog() {
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Add New Treasure", "Use this dialog to create a new treasure", "newtreasuredialog.fxml", true, true);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewTreasureDialogController controller = newWindow.getFxmlLoader().getController();
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
    }

    @FXML
    private void showNewLocationDialog(Node node) {
        int x = GridPane.getColumnIndex(node);
        int y = GridPane.getRowIndex(node);
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Add New Location", "Use this dialog to create a new location", "newlocationdialog.fxml", true, true);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewLocationDialogController controller = newWindow.getFxmlLoader().getController();
            controller.setPosition(new Position(x, y));
            controller.setWorld(world);
            Location newLocation = controller.newLocation();
            locationsListView.getSelectionModel().select(newLocation);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditEnemyDialog(Enemy enemy) {
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Edit Enemy", "Use this dialog to edit enemy", "newenemydialog.fxml", true, true);
        NewEnemyDialogController controller = newWindow.getFxmlLoader().getController();
        controller.setWorld(world);
        controller.setNameTextField(enemy.getName());
        controller.setHealthSpinnerValue(enemy.getStatistics().getHealthValue());
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
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Edit Treasure", "Use this dialog to edit Treasure", "newtreasuredialog.fxml", true, true);
        NewTreasureDialogController controller = newWindow.getFxmlLoader().getController();
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

    public void showEditLocationDialog(Location location) {
        Dialog<ButtonType> dialog = newWindow.showDialog(mainBorderPane, "Edit location", "Use this dialog to edit location", "newlocationdialog.fxml", true, true);
        NewLocationDialogController controller = newWindow.getFxmlLoader().getController();
        controller.setWorld(world);
        controller.setNameTextField(location.getName());
        controller.setDescriptionTextArea(location.getDescription());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateLocation(location);
            System.out.println(location + " was updated");
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    private void createContextMenu(ListView<LocationContent> listView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
            String id = listView.getSelectionModel().getSelectedItem().getId();
            if (world.findEnemy(id) != null) {
                delete(world.findEnemy(id));
            } else if (world.findTreasure(id) != null) {
                delete(world.findTreasure(id));
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

        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<LocationContent> call(ListView<LocationContent> deletableListView) {
                ListCell<LocationContent> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(LocationContent deletable, boolean empty) {
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
                                cell.setContextMenu(contextMenu);
                            }
                        }
                );

                return cell;
            }
        });
    }

    @FXML
    public void delete(LocationContent locationContent) {
        Location location = world.findLocationByContent(locationContent.getId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + locationContent.getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (locationContent instanceof Enemy) {
                if (world.removeEnemy((Enemy) locationContent)) {
                    enemiesListView.getItems().remove(locationContent);
                }
            } else if (locationContent instanceof Treasure) {
                if (world.removeTreasure((Treasure) locationContent)) {
                    treasuresListView.getItems().remove(locationContent);
                    CreateMap.getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
                }
            }
        }
    }

    @FXML
    public void delete(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + location.getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CreateMap.getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
            HashMap<String, Position> exits = location.getExits();
            if (world.removeLocation(location)) {
                locationsListView.getItems().remove(location);
                for (String key : exits.keySet()) {
                    if (!key.equals("Q")) {
                        Position position = exits.get(key);
                        Location nearLocation = world.findLocation(position, world.getLocations());
                        CreateMap.modifyMapCell(nearLocation, CreateMap.getImageView(gameMapGridPane, position.getX(), position.getY()), false);
                    }
                }
            }
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
    public void edit(Location location) {
        showEditLocationDialog(location);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    void makeZoomable(GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, Slider zoomSlider) {
        super.makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }
}
