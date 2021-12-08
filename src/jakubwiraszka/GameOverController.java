package jakubwiraszka;

import jakubwiraszka.gamefiles.GameData;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameOverController {

    @FXML
    private ImageView imageView;

    public void initialize() {
        imageView.setImage(new Image("D:\\Projekty\\Java\\AdventureFX\\icons\\GameOver.png"));
    }

    public void restart() {
        GameData.saveAll();
        Node node = imageView;
        NewWindow.changePane(node, "mainmenu.fxml");
    }
}
