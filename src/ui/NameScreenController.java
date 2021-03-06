package ui;

import client.PokerClient;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;

public class NameScreenController {
    public TextField playerNameField;
    public Button confirmName;
    public Label errorMsg1;
    public int count = 0;
    public static String playerName;
    public static String tableID;
    public TextField serverIPField;
    public Label errorMsg2;
    public TextField tableIDField;
    public Label errorMsg3;

    public void handleConfirmNameButton(ActionEvent actionEvent) throws IOException, InterruptedException {
        //Get info
        playerName = playerNameField.getText();
        tableID = tableIDField.getText();
        confirmName.setDefaultButton(true);

        if (playerNameField.getText().length() > 40 || playerNameField.getText().isEmpty()) {
            errorMsg1.setText("Names must be between 1 and 40 characters");
            errorMsg1.setWrapText(true);
            funnyButton(errorMsg1);
        } else if (serverIPField.getText().isEmpty()) {
            errorMsg2.setText("Please enter an IP adress");
            funnyButton(errorMsg2);
        } else {
            Main main = new Main();
            try {
                Main.client = new PokerClient(serverIPField.getText(), tableIDField.getText());
                main.setStage("/ui/GameScreen.fxml", Main.pStage);
            } catch (Exception InterruptedException) {
                errorMsg2.setText("This IP is not valid");
                System.out.println(InterruptedException);
            }
        }
    }

    private void funnyButton(Label errorMsg) {
        confirmName.setOnMouseClicked(event -> {
            count++;
            System.out.println(count);
        });
        if (count >= 10) {
            errorMsg.setText("Why have you clicked the button \n" + count + " times?");
            if (count >= 30) {
                errorMsg.setText("Please stop...");
                if (count >= 50) {
                    errorMsg.setText("I SAID STOP");
                    if (count >= 70) {
                        errorMsg.setText("15 more clicks and I close");
                        if (count >= 85) {
                            Stage stage = (Stage) confirmName.getScene().getWindow();
                            stage.close();
                        }
                    }
                }
            }
        }
    }

    public static String getPlayerName() {
        return playerName;
    }

    public static String getTableID() {
        return tableID;
    }
}
