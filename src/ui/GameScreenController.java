package ui;

import client.PokerClient;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jspace.SpaceRepository;
import org.jspace.Tuple;
import sample.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameScreenController implements Initializable {
    public Button btnBack;
    public Label playerName;
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
    public AnchorPane gameCanvas;
    public int playerMoney;
    public int smallBlind;
    public int bigBlind;

    public ArrayList<ImageView> playerImages = new ArrayList<ImageView>();
    public ArrayList<ImageView> cards = new ArrayList<ImageView>();
    public ArrayList<Label> playerNames = new ArrayList<Label>();


    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Main main = new Main();
        main.setStage("/ui/StartScreen.fxml", Main.pStage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGameConfig();

        playerName.setText(NameScreenController.getPlayerName());
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

        //Add imageview to players
        for (int i = 0; i < 8; i++) {
            if (i >= 0) {
                Image placeHolderImage = new Image(getClass().getResourceAsStream("img/placeholder.png"));
                ImageView placeHolderImageView = new ImageView(placeHolderImage);
                placeHolderImageView.setFitWidth(125);
                placeHolderImageView.setFitHeight(125);
                playerImages.add(placeHolderImageView);
            }
        }

        //Add imageview to cards
        for (int i = 0; i < playerImages.size() * 2; i++) {
            Image placeHolderImage = new Image(getClass().getResourceAsStream("img/cards/800px-Playing_card_club_A.svg.png"));
            ImageView placeHolderImageView = new ImageView(placeHolderImage);
            placeHolderImageView.setFitHeight(80);
            placeHolderImageView.setFitWidth(60);
            cards.add(placeHolderImageView);
        }

        //Add player name labels
        for (int i = 0; i < playerImages.size(); i++) {
            Label playerName = new Label();
            playerName.setWrapText(true);
            playerName.setMaxHeight(80);
            playerName.setMaxWidth(100);
            playerName.setText(NameScreenController.getPlayerName());
            playerNames.add(playerName);
        }

        setPlayerSeats();
        setPlayerCards();
        setPlayerNames();
        gameCanvas.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!newValue.equals(oldValue)) {
                    setPlayerPos();
                    setCardPos();
                    setPlayerNamePos();
                }
            }
        });
        gameCanvas.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!newValue.equals(oldValue)) {
                    setPlayerPos();
                    setCardPos();
                }
            }
        });
    }

    private void setPlayerNamePos() {
        double posX;
        double posY;
        for (int i = 0; i < playerNames.size(); i++) {
            posX = playerImages.get(i).getX();
            posY = playerImages.get(i).getY();
            playerNames.get(i).setVisible(i >= Main.client.total_players);
            playerNames.get(i).setLayoutX(posX);
            playerNames.get(i).setLayoutY(posY + 100);

        }
    }

    private void setPlayerNames() {
        for (Label playerName : playerNames) {
            try {
                gameCanvas.getChildren().add(playerName);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void setCardPos() {
        double posX;
        double posY;
        int cardNumber = 0;
        for (int i = 0; i < cards.size(); i++) {
            try {
                for (int j = 0; j < 2; j++) {
                    posX = playerImages.get(i).getX();
                    posY = playerImages.get(i).getY();
                    if (j == 0) {
                        cards.get(cardNumber).setVisible(cardNumber <= Main.client.total_players * 2);
                        cards.get(cardNumber).setX(posX);
                        cards.get(cardNumber).setY(posY - 80);
                        cardNumber++;
                    } else {
                        cards.get(cardNumber).setVisible(cardNumber <= Main.client.total_players * 2);
                        cards.get(cardNumber).setX(posX + 65);
                        cards.get(cardNumber).setY(posY - 80);
                        cardNumber++;
                    }
                    System.out.println("X: " + cards.get(i).getX() + ", Y: " + cards.get(i).getY());
                }
            } catch (Exception e) {
                System.out.println("Stop");
            }
            System.out.println("Nummer:" + cardNumber);
        }
    }

    private void setPlayerCards() {
        for (ImageView cardImages : cards) {
            try {
                gameCanvas.getChildren().add(cardImages);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void setPlayerSeats() {
        for (ImageView playerImage : playerImages) {
            try {
                gameCanvas.getChildren().add(playerImage);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private void setPlayerPos() {
        double centerX = (Main.pStage.getScene().getWidth() - 409) / 2;
        double centerY = (Main.pStage.getScene().getHeight() - 277) / 2;
        for (int i = 0; i < playerImages.size(); i++) {
            ImageView placeHolderImageView = playerImages.get(i);
            placeHolderImageView.toFront();
            double angle = 2 * i * Math.PI / Main.client.total_players;
            double offsetX = -Math.sin(angle) * centerX * 0.7;
            double offsetY = Math.cos(angle) * centerY * 0.7;
            double imageX = (centerX + offsetX) - 50;
            double imageY = (centerY + offsetY) - 50;
            placeHolderImageView.setX(imageX);
            placeHolderImageView.setY(imageY);
            if (i >= Main.client.total_players) {
                placeHolderImageView.setVisible(false);
            } else {
                placeHolderImageView.setVisible(true);
            }
        }
    }

    private void initializeGameConfig() {
        //Setup grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        //Setup textfields
        TextField playerMoneyField = new TextField();
        playerMoneyField.setPromptText("Enter money amount");
        TextField smallBlindField = new TextField();
        smallBlindField.setPromptText("Enter small blind amount");
        TextField bigBlindField = new TextField();
        bigBlindField.setPromptText("Enter big blind amount");

        //Add textfields and labels to grid
        grid.add(new Label("Player Money:"), 0, 0);
        grid.add(playerMoneyField, 1, 0);
        grid.add(new Label("Small Blind:"), 0, 1);
        grid.add(smallBlindField, 1, 1);
        grid.add(new Label("Big Blind:"), 0, 2);
        grid.add(bigBlindField, 1, 2);
        Button sendServer = new Button("Send to server");
        grid.add(sendServer, 0, 3);

        //Add grid to scene and stage
        Group root = new Group(grid);
        Scene scene = new Scene(root, 900, 362);
        Stage gameConfig = new Stage();
        gameConfig.setTitle("Game configurations for -Poker For Humans-");
        gameConfig.setScene(scene);
        root.getStylesheets().add(Main.cssBtn);
        root.getStylesheets().add(Main.cssMain);
        root.getStylesheets().add("/ui/css/gameconfig.css");
        grid.setStyle("-fx-background-color:  #345599");
        gameConfig.setResizable(false);

        //Make on action for button
        sendServer.setOnAction(event -> {
            try {
                playerMoney = Integer.parseInt(playerMoneyField.getText());
                smallBlind = Integer.parseInt(smallBlindField.getText());
                bigBlind = Integer.parseInt(bigBlindField.getText());
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter numbers");
                alert.show();
            }
        });

        gameConfig.show();

    }

    public void handleCallButton(ActionEvent actionEvent) throws InterruptedException {
        raiseAmount = 0;
        checkZero(raiseAmount);
        Main.client.sendAction("call", 0);
    }

    public void handleRaiseButton(ActionEvent actionEvent) throws InterruptedException {
        Main.client.sendAction("raise", raiseAmount);
        raiseAmount = 0;
        checkZero(raiseAmount);
    }

    public void handleFoldButton(ActionEvent actionEvent) throws InterruptedException {
        raiseAmount = 0;
        checkZero(raiseAmount);
        Main.client.sendAction("fold", 0);
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

    public int getPlayerMoney() {
        return playerMoney;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public int getBigBlind() {
        return bigBlind;
    }
}
