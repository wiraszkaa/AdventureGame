package jakubwiraszka;

import jakubwiraszka.dialogs.DialogBuilder;
import jakubwiraszka.dialogs.DifficultyDialogController;
import jakubwiraszka.dialogs.HeroStatsDialogController;
import jakubwiraszka.gamefiles.*;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.Objects;
import java.util.Optional;

public class MainMenuController extends CreateInterfaceController {
    private String worldName;

    @FXML
    private ListView<World> worldsListView;
    @FXML
    private Label messageLabel;
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        worldsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                worldName = newValue.getName();
            }
        });

        worldsListView.setItems(GameData.getWorlds());
        worldsListView.getSelectionModel().selectFirst();

        worldsListView.getItems().addListener((ListChangeListener<World>) change -> {
            while(change.next()) {
                if (change.wasUpdated()) {
                    GameData.save();
                    System.out.println("World was changed");

                } else {
                    for (World removedWorld : change.getRemoved()) {
                        System.out.println(removedWorld.getName() + " was removed");
                    }
                    for (World addedWorld : change.getAddedSubList()) {
                        System.out.println(addedWorld.getName() + " was added");
                        GameData.save();
                    }
                }
            }
        });

        worldsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<World> call(ListView<World> worldListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(World world, boolean empty) {
                        super.updateItem(world, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(world.toString());
                            setFont(new Font("Arial italic", 70));
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void play(ActionEvent event) {
        Node node = (Node) event.getSource();
        GameInterfaceController gameInterfaceController = NewWindow.changePane(node, "gameinterface.fxml").getController();
        gameInterfaceController.setWorld(GameData.findWorld(worldName));
        gameInterfaceController.start();
    }

    @FXML
    public void startNew(ActionEvent event) {
        Hero hero = new Hero("Hero", new Statistics(4, 2, 0));
        hero.getLevel().setPointsToSpend(10);
        InventoryController inventoryController = NewWindow.newWindow("inventory.fxml").getController();
        inventoryController.setHero(hero);
        hero.getLevel().addLevelListener(inventoryController);
        hero.getLevel().addLevelListener(inventoryController.getPointsToSpendGUI());
        Button okButton = new Button("Okay");
        okButton.pressedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1) {
                hero.setName(inventoryController.getNameTextField().getText());
                inventoryController.close();
                DialogBuilder dialogBuilder = new DialogBuilder();
                dialogBuilder.setOwner(mainBorderPane);
                dialogBuilder.setTitle("Choose Difficulty");
                dialogBuilder.setSource("difficultydialog.fxml");
                dialogBuilder.addOkButton();
                Dialog<ButtonType> dialog = dialogBuilder.getDialog();
                DifficultyDialogController controller = dialogBuilder.getFxmlLoader().getController();
                Optional<ButtonType> result = dialog.showAndWait();
                Difficulty difficulty = null;
                String name = "";
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    difficulty = controller.getDifficulty();
                    name = controller.getName();
                } else {
                    System.out.println("Cancel Pressed");
                }
                if (difficulty != null && !name.equals("")) {
                    newEndlessWorld(event, difficulty, name, hero);
                }
            }
        });
        inventoryController.getStatsGridPane().add(okButton, 0, 5);
    }

    @FXML
    public void modify(ActionEvent event) {
        if(!Objects.requireNonNull(GameData.findWorld(worldName)).isStart()) {
            Node node = (Node) event.getSource();
            ModifyInterfaceController modifyInterfaceController = NewWindow.changePane(node, "modifyinterface.fxml").getController();
            modifyInterfaceController.setWorld(GameData.findWorld(worldName));
            modifyInterfaceController.start();
        } else {
            messageLabel.setText("You can't modify this world, because it has already started!");
        }
    }

    @FXML
    public void create(ActionEvent event) {
        Node node = (Node) event.getSource();
        NewWindow.changePane(node, "createinterface.fxml");
    }

    public void addWorld(World world) {
        if(findWorld(world.getName()) == null) {
            GameData.getWorlds().add(world);
        }
    }

    private World findWorld(String worldName) {
        for(World world: worldsListView.getItems()) {
            if(world.getName().equals(worldName)) {
                return world;
            }
        }
        return null;
    }

    @Override
    public void newEndlessWorld(ActionEvent event, Difficulty difficulty, String name, Hero hero) {
        super.newEndlessWorld(event, difficulty, name, hero);
    }
}
