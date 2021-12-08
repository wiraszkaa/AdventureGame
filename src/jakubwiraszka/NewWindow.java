package jakubwiraszka;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import java.io.IOException;

public class NewWindow {

    private FXMLLoader fxmlLoader;

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

    public Dialog<ButtonType> showDialog(Node owner, String title, String header, String source, boolean okButton, boolean cancelButton) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner.getScene().getWindow());
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        createFxmlLoader(dialog, source, okButton, cancelButton);
        return dialog;
    }

    private void createFxmlLoader(Dialog<ButtonType> dialog, String name, boolean okButton, boolean cancelButton) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(name));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        if(okButton) {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        }
        if(cancelButton) {
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        }
        this.fxmlLoader = fxmlLoader;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }
}
