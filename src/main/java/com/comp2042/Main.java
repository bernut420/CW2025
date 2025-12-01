package com.comp2042;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

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
        primaryStage.setFullScreen(true);
        primaryStage.show();
        
        // Center the game after the window is shown and fullscreen is activated
        // Use multiple delayed calls to ensure dimensions are available
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                if (primaryStage.isFullScreen()) {
                    c.updateGameScale();
                }
            });
        });
        
        // Listen for fullscreen changes to re-center
        primaryStage.fullScreenProperty().addListener((obs, wasFullscreen, isNowFullscreen) -> {
            Platform.runLater(() -> {
                Platform.runLater(() -> c.updateGameScale());
            });
        });
        
        // Also listen for scene size changes
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> c.updateGameScale());
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> c.updateGameScale());
        });
        
        // GameController will be initialized when user clicks "Play" button
    }

    public static void main(String[] args) {
        launch(args);
    }
}