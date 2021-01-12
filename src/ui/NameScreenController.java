package ui;

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
    public Label errorMsg;
    public int count = 0;
    public static String playerNameCon;

    public void handleConfirmNameButton(ActionEvent actionEvent) throws IOException {
        //Get name
        playerNameCon = playerNameField.getText();
        confirmName.setDefaultButton(true);
        if (playerNameField.getText().length() > 40 || playerNameField.getText().isEmpty()) {
            errorMsg.setText("Names must be between 1 and 40 characters");
            errorMsg.setWrapText(true);
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
        } else {
            Main main = new Main();
            System.out.println(playerNameCon);
            main.setStage("/ui/GameScreen.fxml", Main.pStage);
        }
    }

    public static String getPlayerNameCon() {
        return playerNameCon;
    }
}
