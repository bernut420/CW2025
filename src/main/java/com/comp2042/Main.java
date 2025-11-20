package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 400, 600); // Slightly larger initial size

        // Enable fullscreen support
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(500);
        primaryStage.setFullScreenExitHint("Press ESC to exit fullscreen");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("ESC"));

        primaryStage.setScene(scene);
        primaryStage.show();
        new GameController(c);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



    public static void main(String[] args) {
        launch(args);
    }
}
