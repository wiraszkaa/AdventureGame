package jakubwiraszka;

import jakubwiraszka.gamefiles.GameData;
import jakubwiraszka.map.GameMapBuilder;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameOverController {

    @FXML
    private ImageView imageView;

    public void initialize() {
        imageView.setImage(new Image(GameMapBuilder.getImageUrl("GameOver.png")));
    }

    public void restart() {
        GameData.save();
        Node node = imageView;
        NewWindow.changePane(node, "mainmenu.fxml");
    }
}
