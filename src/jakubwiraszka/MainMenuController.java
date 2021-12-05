package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class MainMenuController extends CreateInterfaceController implements ChangePane {

    private String worldName;
    private ObservableList<World> worlds;
    @FXML
    private ListView<World> worldsListView;
    @FXML
    private Label messageLabel;
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        worldsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<World>() {
            @Override
            public void changed(ObservableValue<? extends World> observableValue, World oldValue, World newValue) {
                if(newValue != null) {
                    worldName = newValue.getName();
                }
            }
        });

        worldsListView.setItems(GameData.getInstance().getWorlds());
        worldsListView.getSelectionModel().selectFirst();

        worldsListView.getItems().addListener(new ListChangeListener<World>() {
            @Override
            public void onChanged(Change<? extends World> change) {
                while(change.next()) {
                    if (change.wasUpdated()) {
//                        GameData.getInstance().saveAll();
                        System.out.println("World was changed");

                    } else {
                        for (World removedWorld : change.getRemoved()) {
                            System.out.println(removedWorld.getName() + " was removed");
                        }
                        for (World addedWorld : change.getAddedSubList()) {
                            System.out.println(addedWorld.getName() + " was added");
                            GameData.getInstance().saveAll();
                        }
                    }
                }
            }
        });

        worldsListView.setCellFactory(new Callback<ListView<World>, ListCell<World>>() {
            @Override
            public ListCell<World> call(ListView<World> worldListView) {
                ListCell<World> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(World world, boolean empty) {
                        super.updateItem(world, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(world.toString());
                            setFont(new Font("Arial italic", 70));
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    public void play(ActionEvent event) {
        Node node = (Node) event.getSource();
        GameInterfaceController gameInterfaceController = changePane(node, "gameinterface.fxml").getController();
        gameInterfaceController.setWorld(GameData.getInstance().findWorld(worldName));
        gameInterfaceController.start();
    }

    @FXML
    public void startNew(ActionEvent event) {
        Dialog<ButtonType> heroDialog = new Dialog<>();
        heroDialog.initOwner(mainBorderPane.getScene().getWindow());
        heroDialog.setTitle("Create Hero");
        FXMLLoader heroFxmlLoader = getFxmlLoader(heroDialog, "herostatsdialog.fxml", true, false);
        if(heroFxmlLoader == null) return;
        Hero hero = new Hero("Hero", new Statistics(4, 2, 0));
        hero.getLevel().setPointsToSpend(10);
        HeroStatsDialogController heroStatsDialogController = heroFxmlLoader.getController();
        heroStatsDialogController.setHero(hero);
        heroStatsDialogController.setHealthLabel("" + hero.getMaxHealth());
        heroStatsDialogController.setPowerLabel("" + hero.getStatistics().getPower());
        heroStatsDialogController.setAgilityLabel("" + hero.getStatistics().getAgility());
        GridPane gridPane = heroStatsDialogController.getMainGridPane();
        TextField nameTextField = new TextField();
        GridPane.setConstraints(nameTextField, 0, 0);
        GridPane.setColumnSpan(nameTextField, 4);
        gridPane.getChildren().add(nameTextField);
        heroDialog.showAndWait();
        hero.setName(nameTextField.getText());

        Dialog<ButtonType> difficultyDialog = new Dialog<>();
        difficultyDialog.initOwner(mainBorderPane.getScene().getWindow());
        difficultyDialog.setTitle("Choose Difficulty");
        FXMLLoader fxmlLoader = getFxmlLoader(difficultyDialog, "difficultydialog.fxml", true, false);
        if (fxmlLoader == null) return;
        DifficultyDialogController controller = fxmlLoader.getController();
        Optional<ButtonType> result = difficultyDialog.showAndWait();
        Difficulty difficulty = null;
        String name = "";
        if (result.isPresent() && result.get() == ButtonType.OK) {
            difficulty = controller.getDifficulty();
            name = controller.getName();
        } else {
            System.out.println("Cancel Pressed");
        }
        if(difficulty != null && !name.equals("")) {
            newEndlessWorld(event, difficulty, name, hero);
        }
    }

    @FXML
    public void modify(ActionEvent event) {
        if(!GameData.getInstance().findWorld(worldName).isStart()) {
            Node node = (Node) event.getSource();
            ModifyInterfaceController modifyInterfaceController = changePane(node, "modifyinterface.fxml").getController();
            modifyInterfaceController.setWorld(GameData.getInstance().findWorld(worldName));
            modifyInterfaceController.start();
        } else {
            messageLabel.setText("You can't modify this world, because it has already started!");
        }
    }

    @FXML
    public void create(ActionEvent event) {
        Node node = (Node) event.getSource();
        changePane(node, "createinterface.fxml");
    }

    public void addWorld(World world) {
        if(findWorld(world.getName()) == null) {
            GameData.getInstance().getWorlds().add(world);
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
    public FXMLLoader changePane(Node node, String name) {
        return super.changePane(node, name);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public FXMLLoader getFxmlLoader(Dialog<ButtonType> dialog, String name, boolean okButton, boolean cancelButton) {
        return super.getFxmlLoader(dialog, name, okButton, cancelButton);
    }

    @Override
    public void newEndlessWorld(ActionEvent event, Difficulty difficulty, String name, Hero hero) {
        super.newEndlessWorld(event, difficulty, name, hero);
    }
}
