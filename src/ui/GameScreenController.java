package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import sample.Main;

import java.io.IOException;

public class GameScreenController {

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Parent gameScreen = FXMLLoader.load(getClass().getResource("StartScreen.fxml"));
        Main.pStage.setScene(new Scene(gameScreen, Main.width, Main.height));
    }
}
