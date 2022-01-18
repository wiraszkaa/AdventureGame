package jakubwiraszka;

import jakubwiraszka.dialogs.DialogBuilder;
import jakubwiraszka.dialogs.DifficultyDialogController;
import jakubwiraszka.gamefiles.*;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
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
    @FXML
    private Button playButton;
    @FXML
    private Button modifyButton;

    public void initialize() {
        worldsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null) {
                worldName = newValue.getName();
            }
        });

        worldsListView.setItems(GameData.getWorlds());
        worldsListView.getSelectionModel().selectFirst();

        if(worldsListView.getItems().isEmpty()) {
            playButton.setDisable(true);
            modifyButton.setDisable(true);
        }

        worldsListView.getItems().addListener((ListChangeListener<World>) change -> {
            while(change.next()) {
                if (change.wasUpdated()) {
                    GameData.save();
                    System.out.println("World was changed");

                } else {
                    for (World removedWorld : change.getRemoved()) {
                        GameData.save();
                        System.out.println(removedWorld.getName() + " was removed");
                    }
                    for (World addedWorld : change.getAddedSubList()) {
                        GameData.save();
                        playButton.setDisable(false);
                        modifyButton.setDisable(false);
                        System.out.println(addedWorld.getName() + " was added");
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
        FXMLLoader fxmlLoader = NewWindow.changePane(node, "gameinterface.fxml");
        if(fxmlLoader != null) {
            GameInterfaceController gameInterfaceController = fxmlLoader.getController();
            gameInterfaceController.setWorld(GameData.findWorld(worldName));
            gameInterfaceController.start();
        }
    }

    @FXML
    public void startNew(ActionEvent event) {
        Hero hero = new Hero("Hero", new Statistics(4, 2, 0));
        hero.getLevel().setPointsToSpend(10);
        DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.setOwner(mainBorderPane);
        dialogBuilder.setTitle("Inventory");
        dialogBuilder.setSource("inventory.fxml");
        dialogBuilder.addOkButton();
        InventoryController inventoryController = dialogBuilder.getFxmlLoader().getController();
        inventoryController.setHero(hero);
        hero.getLevel().addLevelListener(inventoryController);
        hero.getLevel().addLevelListener(inventoryController.getPointsToSpendGUI());
        Optional<ButtonType> result = dialogBuilder.getDialog().showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            hero.setName(inventoryController.getNameTextField().getText());
            dialogBuilder.reset();
            dialogBuilder.setOwner(mainBorderPane);
            dialogBuilder.setTitle("Choose Difficulty");
            dialogBuilder.setSource("difficultydialog.fxml");
            dialogBuilder.addOkButton();
            Dialog<ButtonType> dialog = dialogBuilder.getDialog();
            DifficultyDialogController controller = dialogBuilder.getFxmlLoader().getController();
            Optional<ButtonType> difficultyResult = dialog.showAndWait();
            Difficulty difficulty = null;
            String name = "";
            if (difficultyResult.isPresent() && difficultyResult.get() == ButtonType.OK) {
                difficulty = controller.getDifficulty();
                name = controller.getName();
            } else {
                System.out.println("Cancel Pressed");
            }
            if (difficulty != null && !name.equals("")) {
                newEndlessWorld(event, difficulty, name, hero);
            }
        }
    }

    @FXML
    public void modify(ActionEvent event) {
        if(!Objects.requireNonNull(GameData.findWorld(worldName)).isStart()) {
            Node node = (Node) event.getSource();
            FXMLLoader fxmlLoader = NewWindow.changePane(node, "modifyinterface.fxml");
            if(fxmlLoader != null) {
                ModifyInterfaceController modifyInterfaceController = fxmlLoader.getController();
                modifyInterfaceController.setWorld(GameData.findWorld(worldName));
                modifyInterfaceController.start();
            }
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
