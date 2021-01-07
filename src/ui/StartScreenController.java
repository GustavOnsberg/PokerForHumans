package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;


public class StartScreenController {
    public Button btnStart;
    public Button btnQuit;

    public void handleStartBtn(ActionEvent actionEvent) throws IOException {
        Parent gameScreen = FXMLLoader.load(getClass().getResource("GameScreen.fxml"));
        Main.pStage.setScene(new Scene(gameScreen, Main.width, Main.height));

    }

    public void handleQuitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }
}
