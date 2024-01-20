package org.db;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class javelinMain extends Application {

    static Stage primaryStage;
    static Stage getPrimaryStage() {
        return javelinMain.primaryStage;
    }

    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("javelin.fxml")));
        primaryStage.setTitle("Javelin");
        primaryStage.setScene(new Scene(root, 1200, 850));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/javelin.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
