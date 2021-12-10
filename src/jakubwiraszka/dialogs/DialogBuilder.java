package jakubwiraszka.dialogs;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;

public class DialogBuilder {

    private Dialog<ButtonType> dialog;
    private FXMLLoader fxmlLoader;

    public DialogBuilder() {
        dialog = new Dialog<>();
        fxmlLoader = new FXMLLoader();
    }

    public void reset() {
        dialog = new Dialog<>();
        fxmlLoader = new FXMLLoader();
    }

    public void setOwner(Node node) {
        dialog.initOwner(node.getScene().getWindow());
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void setHeader(String header) {
        dialog.setHeaderText(header);
    }

    public void setSource(String sourceName) {
        fxmlLoader.setLocation(getClass().getResource(sourceName));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
    }

    public void addOkButton() {
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    public void addCancelButton() {
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public Dialog<ButtonType> getDialog() {
        return dialog;
    }
}
