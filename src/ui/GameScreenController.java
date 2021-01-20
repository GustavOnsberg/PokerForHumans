package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sample.Main;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameScreenController implements Initializable {
    public Button btnBack;
    public Label playerName;
    public HBox gameBtns;
    public ScrollPane serverWindow;
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
    public AnchorPane gameCanvas;
    public int playerMoney;
    public int smallBlind;
    public int bigBlind;
    public int whatTableCard = 0;
    public Stage gameConfig;
    public VBox textWindow = new VBox();

    public ArrayList<ImageView> playerImages = new ArrayList<>();
    public ArrayList<ImageView> playerCards = new ArrayList<>();
    public ArrayList<Label> playerNames = new ArrayList<>();
    public ArrayList<ImageView> tableCards = new ArrayList<>();


    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to leave the game?");
        alert.setTitle("Leave Game");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            Main main = new Main();
            gameConfig.close();
            main.setStage("/ui/StartScreen.fxml", Main.pStage);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGameConfig();
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
        checkIfZero(raiseAmount);

        //Setup table
        addNodes();
        setPlayerSeats();
        setPlayerCards();
        setPlayerNames();
        setTableCards();

        //Setup server window
        textWindow.setSpacing(10);

        AnchorPane.setTopAnchor(serverWindow, 0.0);
        AnchorPane.setBottomAnchor(serverWindow, 0.0);
        AnchorPane.setLeftAnchor(serverWindow, 0.0);

        sendServerMsgToWindow("hej");

        gameCanvas.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!newValue.equals(oldValue)) {
                    setPlayerPos();
                    setCardPos();
                    setPlayerNamePos();
                    setTableCardPos();
                }
            }
        });
        gameCanvas.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!newValue.equals(oldValue)) {
                    setPlayerPos();
                    setCardPos();
                    setPlayerNamePos();
                }
            }
        });
    }

    public void sendServerMsgToWindow(String serverMsg) {
        Label text = new Label("--· " + serverMsg);
        text.setLayoutX(20);
        text.setId("server_window_label");
        textWindow.getChildren().add(text);
        serverWindow.setContent(textWindow);
        serverWindow.setVvalue(1.0);
    }

    private void addNodes() {
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

        //Add imageview to player cards
        for (int i = 0; i < playerImages.size() * 2; i++) {
            Image placeHolderImage = new Image(getClass().getResourceAsStream("img/cards/800px-Playing_card_club_A.svg.png"));
            ImageView placeHolderImageView = new ImageView(placeHolderImage);
            placeHolderImageView.setFitHeight(80);
            placeHolderImageView.setFitWidth(60);
            playerCards.add(placeHolderImageView);
        }

        //Add cards on table

        for (int i = 0; i < 5; i++) {
            Image placeHolderImage = new Image(getClass().getResourceAsStream("img/cards/800px-Playing_card_club_A.svg.png"));
            ImageView placeHolderImageView = new ImageView(placeHolderImage);
            placeHolderImageView.setFitHeight(120);
            placeHolderImageView.setFitWidth(100);
            tableCards.add(placeHolderImageView);
        }

        //Add player name labels
        for (int i = 0; i < playerImages.size(); i++) {
            Label playerName = new Label();
            playerName.setId("player_name_label");
            playerName.setText(NameScreenController.getPlayerName());
            playerNames.add(playerName);
        }
    }

    private void setTableCardPos() {
        double centerX = ((Main.pStage.getScene().getWidth() - 409) / 2) - 150;
        double centerY = ((Main.pStage.getScene().getHeight() - 277) / 2) - 50;
        for (int i = 0; i < tableCards.size(); i++) {
            tableCards.get(i).setX(centerX);
            tableCards.get(i).setY(centerY);
            centerX = centerX + 70;
            tableCards.get(i).setVisible(false);
        }
    }

    private void setTableCards() {
        for (ImageView card : tableCards) {
            try {
                gameCanvas.getChildren().add(card);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void setPlayerNamePos() {
        double posX;
        double posY;
        for (int i = 0; i < playerNames.size(); i++) {
            posX = playerImages.get(i).getX();
            posY = playerImages.get(i).getY();
            playerNames.get(i).setVisible(i >= Main.client.total_players);
            playerNames.get(i).setLayoutX(posX + 150);
            playerNames.get(i).setLayoutY(posY - 10);
            playerNames.get(i).setText(NameScreenController.getPlayerName());
            playerNames.get(i).setVisible(true);

            //Debug label position
//            System.out.println("X: " + playerNames.get(i).getLayoutX());
//            System.out.println("Y: " + playerNames.get(i).getLayoutY());
//            System.out.println("Name: " + playerNames.get(i).getText());
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
        for (int i = 0; i < playerCards.size(); i++) {
            try {
                for (int j = 0; j < 2; j++) {
                    posX = playerImages.get(i).getX();
                    posY = playerImages.get(i).getY();
                    if (j == 0) {
                        playerCards.get(cardNumber).setVisible(cardNumber <= Main.client.total_players * 2);
                        playerCards.get(cardNumber).setX(posX);
                        playerCards.get(cardNumber).setY(posY - 85);
                        cardNumber++;
                    } else {
                        playerCards.get(cardNumber).setVisible(cardNumber <= Main.client.total_players * 2);
                        playerCards.get(cardNumber).setX(posX + 65);
                        playerCards.get(cardNumber).setY(posY - 85);
                        cardNumber++;
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }

    private void setPlayerCards() {
        for (ImageView cardImages : playerCards) {
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
            double offsetX = -Math.sin(angle) * centerX * 0.6;
            double offsetY = Math.cos(angle) * centerY * 0.6;
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
        gameConfig = new Stage();
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
        checkIfZero(raiseAmount);
        Main.client.sendAction("call", 0);
        try {
        } catch (Exception ignored) {
        }
        sendServerMsgToWindow("Fuck");
    }

    public void handleRaiseButton(ActionEvent actionEvent) throws InterruptedException {
        Main.client.sendAction("raise", raiseAmount);
        raiseAmount = 0;
        checkIfZero(raiseAmount);
    }

    public void handleFoldButton(ActionEvent actionEvent) throws InterruptedException {
        raiseAmount = 0;
        checkIfZero(raiseAmount);
        Main.client.sendAction("fold", 0);
    }

    public void handleSetZero(ActionEvent actionEvent) {
        raiseAmount = 0;
        checkIfZero(raiseAmount);
    }

    public void handlePlusOne(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 1;
        checkIfZero(raiseAmount);
    }

    public void handlePlusTen(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 10;
        checkIfZero(raiseAmount);
    }

    public void handlePlusHundred(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 100;
        checkIfZero(raiseAmount);
    }

    public void handleplusThousand(ActionEvent actionEvent) {
        raiseAmount = raiseAmount + 1000;
        checkIfZero(raiseAmount);
    }

    public void handleMinusOne(ActionEvent actionEvent) {
        if (raiseAmount - 1 <= 0) {
            raiseAmount = 0;
        } else {
            raiseAmount = raiseAmount - 1;
        }
        checkIfZero(raiseAmount);
    }

    public void handleMinusTen(ActionEvent actionEvent) {
        raiseAmount = Math.max(raiseAmount - 10, 0);
        checkIfZero(raiseAmount);
    }

    public void handleMinusHundred(ActionEvent actionEvent) {
        raiseAmount = Math.max(raiseAmount - 100, 0);
        checkIfZero(raiseAmount);
    }

    public void handleMinusThousand(ActionEvent actionEvent) {
        raiseAmount = Math.max(raiseAmount - 1000, 0);
        checkIfZero(raiseAmount);
    }

    public void checkIfZero(int raiseAmount) {
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
