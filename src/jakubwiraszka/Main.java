package jakubwiraszka;

import jakubwiraszka.gamefiles.GameData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainmenu.fxml"));

        primaryStage.setTitle("Adventure");
        primaryStage.setScene(new Scene(root, 1920, 1000));
        primaryStage.show();
    }

    @Override
    public void init() {
        GameData.getInstance().loadAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
