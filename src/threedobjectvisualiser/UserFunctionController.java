package threedobjectvisualiser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class UserFunctionController {

    public void openViewWindow() {
        Parent root;
        try {
            root = FXMLLoader.load(UserFunctionController.class.getResource("ViewerWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Viewer");
            stage.setScene(new Scene(root, 1024, 728));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserFunction() {
         ViewerController.userFunctionString = functionText.getText();
         openViewWindow();
    }

    @FXML
    TextField functionText;

    @FXML
    Button confirmButton;


}
