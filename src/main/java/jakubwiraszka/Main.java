package jakubwiraszka;

import jakubwiraszka.gamefiles.GameData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainmenu.fxml")));
        primaryStage.setTitle("Adventure");
        primaryStage.setScene(new Scene(root, 1800, 900));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void init() {
        GameData.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
