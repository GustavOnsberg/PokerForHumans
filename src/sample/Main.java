package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.IOException;


public class Main extends Application {
    public static Stage pStage;
    public static String cssBtn = "/ui/css/btn.css";
    public static String cssMain = "/ui/css/mainsheet.css";


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/ui/StartScreen.fxml"));
        primaryStage.setTitle("Poker For Humans");
        primaryStage.setScene(new Scene(root));
        root.getStylesheets().add(cssBtn);
        root.getStylesheets().add(cssMain);
        primaryStage.setMaximized(true);
        pStage = primaryStage;
        primaryStage.show();

    }

    public void setStage(String fxmlName, Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlName));
        Scene oldScene = primaryStage.getScene();
        primaryStage.setScene(new Scene(root, oldScene.getWidth(),oldScene.getHeight()));
        //Debug scene size
        System.out.println("Height " + oldScene.getHeight() + " Width " + oldScene.getWidth());
        root.getStylesheets().add(cssBtn);
        root.getStylesheets().add(cssMain);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
