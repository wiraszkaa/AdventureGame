package jakubwiraszka;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public interface ChangePane {

    @FXML
    default FXMLLoader changePane(Node node, String name) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(name));
        Parent secondPane = null;
        try {
            secondPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(secondPane, 1920, 1000);

        Stage primaryStage = (Stage) node.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.show();

        return loader;
    }
}
