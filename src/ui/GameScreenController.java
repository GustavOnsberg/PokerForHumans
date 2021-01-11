package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameScreenController implements Initializable {
    public Button btnBack;
    public Label playerName;
    public Pane gameCanvas;
    public HBox gameBtns;
    public VBox chatWindow;
    public Pane moneyWindow;
    public Button btnCall;
    public Button btnRaise;
    public Button btnFold;

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/StartScreen.fxml",Main.pStage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerName.setText(NameScreenController.getPlayerNameCon());
        btnCall.setMinWidth(200);
        btnCall.setMaxWidth(700);
        btnRaise.setMinWidth(200);
        btnRaise.setMaxWidth(700);
        btnFold.setMinWidth(200);
        btnFold.setMaxWidth(700);


    }

    public void handleCallButton(ActionEvent actionEvent) {
    }

    public void handleRaiseButton(ActionEvent actionEvent) {
    }

    public void handleFoldButton(ActionEvent actionEvent) {
    }
}
