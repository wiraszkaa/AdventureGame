package jakubwiraszka;

import jakubwiraszka.gamefiles.GameData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameOverController implements ChangePane {

    @FXML
    private ImageView imageView;

    public void initialize() {
        imageView.setImage(new Image("D:\\Projekty\\Java\\AdventureFX\\icons\\GameOver.png"));
    }

    public void restart() {
        GameData.getInstance().saveAll();
        Node node = (Node) imageView;
        changePane(node, "mainmenu.fxml");
    }

    @Override
    public FXMLLoader changePane(Node node, String name) {
        return ChangePane.super.changePane(node, name);
    }
}
