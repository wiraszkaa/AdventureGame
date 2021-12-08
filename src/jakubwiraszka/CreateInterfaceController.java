package jakubwiraszka;

import jakubwiraszka.gamefiles.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        this.locationName = GameData.getRandomLocationName();
        this.locationDescription = GameData.getRandomLocationDescription();
        this.locationContent = GameData.getRandomLocationContent();

        makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);

        nameTextField.setText("World");
        heightSpinner.getValueFactory().setValue(15);
        widthSpinner.getValueFactory().setValue(15);
    }

    @FXML
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
            CreateMap.createMap(this.world, gameMapGridPane, contentMapGridPane, playerMapGridPane, false);
            emptyButton.setDisable(true);
            randomButton.setDisable(true);
        }
    }

    @FXML
    public void newWorld() {
        NewWindow.changePane(mainBorderPane, "createinterface.fxml");
    }

    @FXML
    public void saveWorld() {
        MainMenuController mainMenuController = NewWindow.changePane(mainBorderPane, "mainmenu.fxml").getController();
        mainMenuController.addWorld(world);
    }

    @FXML
    public boolean showCreateHeroDialog() {
        NewWindow newDialog = new NewWindow();
        Dialog<ButtonType> dialog = newDialog.showDialog(mainBorderPane, "Create Hero", "Use this dialog to create hero", "newenemydialog.fxml", true, false);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewEnemyDialogController controller = newDialog.getFxmlLoader().getController();
            controller.setHealthSpinnerValue(10);
            controller.setPowerSpinnerValue(5);
            controller.setAgilitySpinnerValue(4);
            this.hero = controller.createHero();
            return true;
        } else {
            return false;
        }
    }

    @Override
    void makeZoomable(GridPane gameMapGridPane, GridPane contentMapGridPane, GridPane playerMapGridPane, Slider zoomSlider) {
        super.makeZoomable(gameMapGridPane, contentMapGridPane, playerMapGridPane, zoomSlider);
    }
}
