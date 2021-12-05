package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class CreateInterfaceController extends ModifyInterfaceController {

    private World world;
    private Hero hero;
    ObservableList<String> locationName;
    ObservableList<String> locationDescription;
    ObservableList<String> locationContent;
    @FXML
    private BorderPane mainBorderPane;
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
    private TextField nameTextField;
    @FXML
    private Spinner<Integer> heightSpinner;
    @FXML
    private Spinner<Integer> widthSpinner;
    @FXML
    private Button emptyButton;
    @FXML
    private Button randomButton;
    @FXML
    private Slider zoomSlider;

    public void initialize() {
        this.locationName = GameData.getInstance().getRandomLocationName();
        this.locationDescription = GameData.getInstance().getRandomLocationDescription();
        this.locationContent = GameData.getInstance().getRandomLocationContent();

        makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);

        heightSpinner.getValueFactory().setValue(15);
        widthSpinner.getValueFactory().setValue(15);
    }

    public void create(ActionEvent event) {
        String name = nameTextField.getText();
        int height = heightSpinner.getValue();
        int width = widthSpinner.getValue();
        boolean wasCreated = showCreateHeroDialog();
        World world = new World(name, height, width, this.hero);
        if(event.getSource().equals(randomButton)) {
            world.createRandom(locationName, locationDescription, locationContent, Difficulty.MEDIUM);
            locationsListView.setItems(world.getLocations());
            enemiesListView.setItems(world.getEnemies());
            treasuresListView.setItems(world.getTreasures());
        }
        if(wasCreated) {
            this.world = world;
            createMap(this.world, gameMapGridPane, contentMapGridPane, playerMapGridPane, false);
            emptyButton.setDisable(true);
            randomButton.setDisable(true);
        }
    }

    @FXML
    public void newWorld() {
        changePane(mainBorderPane, "createinterface.fxml");
    }

    @FXML
    public void saveWorld() {
        MainMenuController mainMenuController = changePane(mainBorderPane, "mainmenu.fxml").getController();
        mainMenuController.addWorld(world);
    }

    @FXML
    public boolean showCreateHeroDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Create Hero");
        dialog.setHeaderText("Use this dialog to create hero");
        FXMLLoader fxmlLoader = getFxmlLoader(dialog, "createherodialog.fxml", true,  false);
        if (fxmlLoader == null) return false;

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CreateHeroDialogController controller = fxmlLoader.getController();
            this.hero = controller.processResults();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public FXMLLoader getFxmlLoader(Dialog<ButtonType> dialog, String name, boolean okButton, boolean cancelButton) {
        return super.getFxmlLoader(dialog, name, okButton, cancelButton);
    }

    @Override
    void createMap(World world, GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, boolean readVisited) {
        super.createMap(world, gameMapGridPane, contentMapGridPane, playerMapGridPane, readVisited);
    }

    @Override
    void makeZoomable(GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, Slider zoomSlider) {
        super.makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }

    @Override
    public FXMLLoader changePane(Node node, String name) {
        return super.changePane(node, name);
    }

    public World getWorld() {
        return world;
    }
}
