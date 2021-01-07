package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
    public static Stage pStage;
    public static int width = 600;
    public static int height = 500;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/ui/StartScreen.fxml"));
        primaryStage.setTitle("Poker For Humans");
        primaryStage.setScene(new Scene(root, width, height));
        pStage = primaryStage;
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
