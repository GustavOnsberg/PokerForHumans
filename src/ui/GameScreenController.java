package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sample.Main;

import java.io.IOException;

public class GameScreenController {
    public Button btnBack;
    public Label playerName;
    public Pane gameCanvas;
    public HBox gameBtns;
    public VBox chatWindow;
    public Pane moneyWindow;

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/StartScreen.fxml",Main.pStage);
    }
}
