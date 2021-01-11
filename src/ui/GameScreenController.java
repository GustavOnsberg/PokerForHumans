package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import sample.Main;

import java.io.IOException;

public class GameScreenController {

    public Button btnBack;

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/StartScreen.fxml",Main.pStage);
    }
}
