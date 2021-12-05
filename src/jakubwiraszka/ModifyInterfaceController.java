package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Optional;

public class ModifyInterfaceController extends GameInterfaceController {

    private World world;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Label locationDescriptionLabel;
    @FXML
    private Label locationNameLabel;
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
                    getImageView(gameMapGridPane, removedLocation.getPosition().getX(), removedLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Unknown.png"));
                }
                for (Location addedLocation : change.getAddedSubList()) {
                    locationsListView.getSelectionModel().select(addedLocation);
                }
            }
        });

        locationsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, location, newLocation) -> {
            if (newLocation != null) {
                getImageView(playerMapGridPane, newLocation.getPosition().getX(), newLocation.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Player.png"));
                locationNameLabel.setText(newLocation.getName());
                locationDescriptionLabel.setText(newLocation.getDescription());
                if (location != null) {
                    getImageView(playerMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
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
                            getImageView(contentMapGridPane, position.getX(), position.getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Enemy");
        dialog.setHeaderText("Use this dialog to create a new enemy");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newenemydialog.fxml", true, true);
        if (fxmlLoader == null) return;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewEnemyDialogController controller = fxmlLoader.getController();
            controller.setWorld(world);
            Enemy newEnemy = controller.newEnemy();
            enemiesListView.getSelectionModel().select(newEnemy);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void showNewTreasureDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Treasure");
        dialog.setHeaderText("Use this dialog to create a new treasure");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newtreasuredialog.fxml", true, true);
        if (fxmlLoader == null) return;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewTreasureDialogController controller = fxmlLoader.getController();
            controller.setWorld(world);
            Treasure newTreasure = controller.newTreasure();
            treasuresListView.getSelectionModel().select(newTreasure);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void showNewLocationDialog() {


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Location");
        dialog.setHeaderText("Use this dialog to create a new location");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newlocationdialog.fxml", true, true);
        if (fxmlLoader == null) return;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewLocationDialogController controller = fxmlLoader.getController();
            controller.setWorld(world);
            Location newLocation = controller.newLocation();
            locationsListView.getSelectionModel().select(newLocation);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditEnemyDialog(Enemy enemy) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Enemy");
        dialog.setHeaderText("Use this dialog to edit enemy");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newenemydialog.fxml", true, true);
        if (fxmlLoader == null) return;
        NewEnemyDialogController controller = fxmlLoader.getController();
        controller.setWorld(world);
        controller.setNameTextField(enemy.getName());
        controller.setHealthSpinnerValue(enemy.getStatistics().getHealthValue());
        controller.setPowerSpinnerValue(enemy.getStatistics().getPower());
        controller.setAgilitySpinnerValue(enemy.getStatistics().getAgility());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateEnemy(enemy);
            System.out.println(enemy + " was updated");
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditTreasureDialog(Treasure treasure) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Treasure");
        dialog.setHeaderText("Use this dialog to edit treasure");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newtreasuredialog.fxml", true, true);
        if (fxmlLoader == null) return;
        NewTreasureDialogController controller = fxmlLoader.getController();
        controller.setWorld(world);
        controller.setNameTextField(treasure.getName());
        controller.setRadioButton(treasure.getContent().getStatistic());
        controller.setValueSpinner(treasure.getContent().getValue());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateTreasure(treasure);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditLocationDialog(Location location) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Location");
        dialog.setHeaderText("Use this dialog to edit location");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newlocationdialog.fxml", true, true);
        if (fxmlLoader == null) return;
        NewLocationDialogController controller = fxmlLoader.getController();
        controller.setWorld(world);
        controller.setNameTextField(location.getName());
        controller.setDescriptionTextArea(location.getDescription());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            controller.updateLocation(location);
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
                    getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
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
            getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(CreateMap.ICONS_LOC + "Nothing.png"));
            HashMap<String, Position> exits = location.getExits();
            if (world.removeLocation(location)) {
                locationsListView.getItems().remove(location);
                for (String key : exits.keySet()) {
                    if (!key.equals("Q")) {
                        Position position = exits.get(key);
                        Location nearLocation = world.findLocation(position, world.getLocations());
                        CreateMap.modifyMapCell(nearLocation, getImageView(gameMapGridPane, position.getX(), position.getY()), false);
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
    ImageView getImageView(GridPane gridPane, int x, int y) {
        return super.getImageView(gridPane, x, y);
    }

    @Override
    void makeZoomable(GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, Slider zoomSlider) {
        super.makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }

    @Override
    public FXMLLoader getFxmlLoader(Dialog<ButtonType> dialog, String name, boolean okButton, boolean cancelButton) {
        return super.getFxmlLoader(dialog, name, okButton, cancelButton);
    }

    @Override
    public FXMLLoader changePane(Node node, String name) {
        return super.changePane(node, name);
    }
}
