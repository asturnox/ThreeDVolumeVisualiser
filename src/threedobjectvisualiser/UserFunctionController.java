package threedobjectvisualiser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main controller for the UserFunction prompt. Leads to the opening of the Viewer window.
 */
public class UserFunctionController {

    /**
     * Opens Viewer window.
     */
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

    /**
     * Run when confirmButton is clicked: sets userFunctionString to the string left by the user in functionText.
     * Subsequently opens Viewer window.
     */
    public void setUserFunction() {
         ViewerController.userFunctionString = functionText.getText();
         openViewWindow();
    }

    @FXML
    TextField functionText;

    @FXML
    Button confirmButton;


}
