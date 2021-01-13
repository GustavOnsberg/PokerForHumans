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
    public Button btnServer;

    public void handleStartBtn(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/NameScreen.fxml",Main.pStage);

    }

    public void handleQuitBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    public void handleServerBtn(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/NameScreen.fxml"));
        Stage server = new Stage();
        server.setTitle("Server for -Poker For Humans-");
        Scene oldScene = Main.pStage.getScene();
        server.setScene(new Scene(root, oldScene.getWidth()-200,oldScene.getHeight()-200));
        root.getStylesheets().add(Main.cssBtn);
        root.getStylesheets().add(Main.cssMain);
        server.show();
    }
}
