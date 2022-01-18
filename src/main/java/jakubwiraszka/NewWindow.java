package jakubwiraszka;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NewWindow {

    @FXML
    public static FXMLLoader changePane(Node node, String name) {
        FXMLLoader loader = new FXMLLoader(NewWindow.class.getResource(name));
        Parent secondPane = null;
        try {
            secondPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(secondPane != null) {
            Scene scene = new Scene(secondPane, 1920, 1000);
            Stage primaryStage = (Stage) node.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();
            return loader;
        } else {
            return null;
        }
    }
}
