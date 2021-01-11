package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;


public class StartScreenController {
    public Button btnStart;
    public Button btnQuit;
    public VBox vBoxStart;

    public void handleStartBtn(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/NameScreen.fxml",Main.pStage);

    }

    public void handleQuitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }
}
