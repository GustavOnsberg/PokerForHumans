package ui;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    public int raiseAmount = 0;
    public Button plusOne;
    public Button plusTen;
    public Button plusHundred;
    public Button plusThousand;
    public Button minusOne;
    public Button minusTen;
    public Button minusHundred;
    public Button minusThousand;
    public Button setZero;
    public ScrollPane chatWindowPane;

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/StartScreen.fxml",Main.pStage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerName.setText(NameScreenController.getPlayerNameCon());
        //Set style for main buttons
        btnCall.setId("game_btn");
        btnFold.setId("game_btn");
        btnRaise.setText("Raise: " + raiseAmount);

        //Set style for raise buttons
        plusOne.setId("raise_btn");
        plusTen.setId("raise_btn");
        plusHundred.setId("raise_btn");
        plusThousand.setId("raise_btn");
        minusOne.setId("raise_btn");
        minusTen.setId("raise_btn");
        minusHundred.setId("raise_btn");
        minusThousand.setId("raise_btn");
        setZero.setId("raise_btn");

        //Set raise amount
        checkZero(raiseAmount);


    }

    public void handleCallButton(ActionEvent actionEvent) throws InterruptedException {
        raiseAmount = 0;
        checkZero(raiseAmount);
        Main.client.sendAction("call",0);
    }

    public void handleRaiseButton(ActionEvent actionEvent) throws InterruptedException {
        Main.client.sendAction("raise",raiseAmount);
        raiseAmount = 0;
        checkZero(raiseAmount);
    }

    public void handleFoldButton(ActionEvent actionEvent) throws InterruptedException {
        raiseAmount = 0;
        checkZero(raiseAmount);
        Main.client.sendAction("fold",0);
    }

    public void handleSetZero(ActionEvent actionEvent) {
        raiseAmount = 0;
        checkZero(raiseAmount);
    }

    public void handlePlusOne(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 1;
        checkZero(raiseAmount);
    }

    public void handlePlusTen(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 10;
        checkZero(raiseAmount);
    }

    public void handlePlusHundred(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 100;
        checkZero(raiseAmount);
    }

    public void handleplusThousand(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 1000;
        checkZero(raiseAmount);
    }

    public void handleMinusOne(ActionEvent actionEvent) {
        if (raiseAmount - 1 <= 0) {
            raiseAmount = 0;
        } else {
            raiseAmount = raiseAmount - 10;
        }
        checkZero(raiseAmount);
    }

    public void handleMinusTen(ActionEvent actionEvent) {
        if (raiseAmount - 10 <= 0) {
            raiseAmount = 0;
        } else {
            raiseAmount = raiseAmount - 10;
        }
        checkZero(raiseAmount);
    }

    public void handleMinusHundred(ActionEvent actionEvent) {
        if (raiseAmount - 100 <= 0) {
            raiseAmount = 0;
        } else {
            raiseAmount = raiseAmount - 100;
        }
        checkZero(raiseAmount);
    }

    public void handleMinusThousand(ActionEvent actionEvent) {
        if (raiseAmount - 1000 <= 0) {
            raiseAmount = 0;
        } else {
            raiseAmount = raiseAmount - 1000;
        }
        checkZero(raiseAmount);
    }

    public void checkZero(int raiseAmount) {
        btnRaise.setText("Raise: " + raiseAmount);
        if (raiseAmount <= 0) {
            btnRaise.setId("raise_zero");
        } else {
            btnRaise.setId("game_btn");
        }
    }
    public Pane getPane(Pane name){
        return name;
    }
}
