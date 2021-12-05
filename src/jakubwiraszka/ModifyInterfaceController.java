package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Optional;

public class ModifyInterfaceController extends GameInterfaceController {

    private String worldName;
    private World world;
    @FXML
    private StackPane mainStackPane;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button enterButton;
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
    private ContextMenu enemyContextMenu;
    @FXML
    private ContextMenu treasureContextMenu;
    @FXML
    private ContextMenu locationContextMenu;
    @FXML
    private Slider zoomSlider;

    public void initialize() {

        mainBorderPane.setOpacity(0.1);
    }

    @FXML
    public void start() {
        mainStackPane.getChildren().remove(enterButton);
        mainBorderPane.setOpacity(1);

        this.world = GameData.getInstance().findWorld(worldName);

        locationsListView.setItems(world.getLocations());
        enemiesListView.setItems(world.getEnemies());
        treasuresListView.setItems(world.getTreasures());

        createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, false);

        createContextMenu(enemyContextMenu, enemiesListView);
        createContextMenu(treasureContextMenu, treasuresListView);
        locationContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Location location = locationsListView.getSelectionModel().getSelectedItem();
                delete(location);
            }
        });

        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Location location = locationsListView.getSelectionModel().getSelectedItem();
//                edit(location);
            }
        });

        locationContextMenu.getItems().addAll(deleteMenuItem);
        locationContextMenu.getItems().addAll(editMenuItem);

        locationsListView.setCellFactory(new Callback<ListView<Location>, ListCell<Location>>() {
            @Override
            public ListCell<Location> call(ListView<Location> deletableListView) {
                ListCell<Location> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Location location, boolean empty) {
                        super.updateItem(location, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(location.toString());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(locationContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

        locationsListView.getItems().addListener(new ListChangeListener<Location>() {
            @Override
            public void onChanged(Change<? extends Location> change) {
                while (change.next()) {
                    if (change.wasUpdated()) {

                    } else {
                        for (Location removedLocation : change.getRemoved()) {
                            getImageView(gameMapGridPane, removedLocation.getPosition().getX(), removedLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Unknown.png"));
                        }
                        for (Location addedLocation : change.getAddedSubList()) {
                            locationsListView.getSelectionModel().select(addedLocation);
                        }
                    }
                }
            }
        });

        locationsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Location>() {
            @Override
            public void changed(ObservableValue<? extends Location> observableValue, Location location, Location newLocation) {
                if(newLocation != null) {
                    getImageView(playerMapGridPane, newLocation.getPosition().getX(), newLocation.getPosition().getY()).setImage(new Image(ICONS_LOC + "Player.png"));
                    locationNameLabel.setText(newLocation.getName());
                    locationDescriptionLabel.setText(newLocation.getDescription());
                    if(location != null) {
                        getImageView(playerMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));
                    }
                }
            }
        });


        enemiesListView.getItems().addListener(new ListChangeListener<LocationContent>() {
            @Override
            public void onChanged(Change<? extends LocationContent> change) {
                while (change.next()) {
                    if (change.wasUpdated()) {
                        System.out.println("Change!");
                        enemiesListView.setItems(world.getEnemies());
                    } else {
                        for (LocationContent removedEnemy : change.getRemoved()) {
                            System.out.println(removedEnemy.getId() + " was removed");
                            Position position = removedEnemy.getPosition();
                            if(position.getX() != -1 && position.getY() != -1) {
                                getImageView(contentMapGridPane, position.getX(), position.getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));
                            }
                        }
                        for (LocationContent addedEnemy : change.getAddedSubList()) {
                            System.out.println(addedEnemy.getId() + " was added");
                            enemiesListView.getSelectionModel().select(addedEnemy);
                        }
                    }
                }
            }
        });

        makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }

    @FXML
    public void showNewEnemyDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Add New Enemy");
        dialog.setHeaderText("Use this dialog to create a new enemy");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newenemydialog.fxml", true, true);
        if (fxmlLoader == null) return;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewEnemyDialogController controller = fxmlLoader.getController();
            controller.setWorldName(worldName);
            Enemy newEnemy = controller.newEnemy();
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    @FXML
    public void showNewTreasureDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Add New Treasure");
        dialog.setHeaderText("Use this dialog to create a new treasure");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newtreasuredialog.fxml", true, true);
        if (fxmlLoader == null) return;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewTreasureDialogController controller = fxmlLoader.getController();
            controller.setWorldName(worldName);
            Treasure newTreasure = controller.newTreasure();
            treasuresListView.getSelectionModel().select(newTreasure);
        } else {
            System.out.println("Cancel Pressed");
        }
    }

    public void showEditEnemyDialog(Enemy enemy) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Edit Enemy");
        dialog.setHeaderText("Use this dialog to edit enemy");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newenemydialog.fxml", true, true);
        if (fxmlLoader == null) return;
        NewEnemyDialogController controller = fxmlLoader.getController();
        controller.setWorldName(worldName);
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
        dialog.initOwner(mainStackPane.getScene().getWindow());
        dialog.setTitle("Edit Treasure");
        dialog.setHeaderText("Use this dialog to edit treasure");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "newtreasuredialog.fxml", true, true);
        if (fxmlLoader == null) return;
        NewTreasureDialogController controller = fxmlLoader.getController();
        controller.setWorldName(worldName);
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

    private void createContextMenu(ContextMenu contextMenu, ListView<LocationContent> listView) {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String id = listView.getSelectionModel().getSelectedItem().getId();
                if (world.findEnemy(id) != null) {
                    delete(world.findEnemy(id));
                } else if (world.findTreasure(id) != null) {
                    delete(world.findTreasure(id));
                }
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

        ContextMenu finalContextMenu = contextMenu;
        listView.setCellFactory(new Callback<ListView<LocationContent>, ListCell<LocationContent>>() {
            @Override
            public ListCell<LocationContent> call(ListView<LocationContent> deletableListView) {
                ListCell<LocationContent> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(LocationContent deletable, boolean empty) {
                        super.updateItem(deletable, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(deletable.toString());
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(finalContextMenu);
                            }
                        }
                );

                return cell;
            }
        });
    }

    @FXML
    public boolean delete(LocationContent locationContent) {
        Location location = world.findLocationByContent(locationContent.getId());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete item");
            alert.setHeaderText("Delete item: " + locationContent.getName());
            alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
            Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            if(locationContent instanceof Enemy) {
                if(world.removeEnemy((Enemy) locationContent)) {
                    enemiesListView.getItems().remove(locationContent);
                    return true;
                } else {
                    return false;
                }
            } else if(locationContent instanceof Treasure) {
                if(world.removeTreasure((Treasure) locationContent)) {
                    treasuresListView.getItems().remove(locationContent);
                    getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    @FXML
    public boolean delete(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Delete item: " + location.getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            getImageView(contentMapGridPane, location.getPosition().getX(), location.getPosition().getY()).setImage(new Image(ICONS_LOC + "Nothing.png"));
            HashMap<String, Position> exits = location.getExits();
            if (world.removeLocation(location)) {
                locationsListView.getItems().remove(location);
                for(String key: exits.keySet()) {
                    if(!key.equals("Q")) {
                        Position position = exits.get(key);
                        Location nearLocation = world.findLocation(position, world.getLocations());
                        modifyMapCell(nearLocation, getImageView(gameMapGridPane, position.getX(), position.getY()), false);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @FXML
    public void edit(LocationContent locationContent) {
        if(locationContent instanceof Enemy) {
            showEditEnemyDialog((Enemy) locationContent);
        } else if(locationContent instanceof Treasure) {
            showEditTreasureDialog((Treasure) locationContent);
        }
    }

    @Override
    public void modifyMapCell(Location location, ImageView imageView, boolean readVisited) {
        super.modifyMapCell(location, imageView, readVisited);
    }

    @Override
    public void modifyContentCell(Location location, ImageView imageView, World world, boolean readVisited) {
        super.modifyContentCell(location, imageView, world, readVisited);
    }

    @Override
    void createMap(World world, GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, boolean readVisited) {
        super.createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, readVisited);
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

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
