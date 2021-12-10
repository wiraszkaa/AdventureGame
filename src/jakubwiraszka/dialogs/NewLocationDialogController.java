package jakubwiraszka.dialogs;

import jakubwiraszka.gamefiles.Location;
import jakubwiraszka.gamefiles.Position;
import jakubwiraszka.gamefiles.World;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewLocationDialogController {
    private World world;
    private Position position;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea descriptionTextArea;

    @FXML
    public Location newLocation() {
        String name = nameTextField.getText();
        String description = descriptionTextArea.getText();
        Location location = new Location(name, description, position);
        world.addLocation(location, false);
        return location;
    }

    @FXML
    public void updateLocation(Location location) {
        location.setName(nameTextField.getText());
        location.setDescription(descriptionTextArea.getText());
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setNameTextField(String name) {
        this.nameTextField.setText(name);
    }

    public void setDescriptionTextArea(String description) {
        this.descriptionTextArea.setText(description);
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
