package com.comp2042;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Tetris game application.
 * Initializes the JavaFX application and sets up the game window.
 */
public class Main extends Application {

    /**
     * Initializes and displays the main game window.
     * Loads the FXML layout, sets up the GUI controller, and configures
     * window properties including fullscreen mode and resize listeners.
     * 
     * @param primaryStage the primary stage for the application
     * @throws Exception if there is an error loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 400, 600);

        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(500);

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
        
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                if (primaryStage.isFullScreen()) {
                    c.updateGameScale();
                }
            });
        });
        
        primaryStage.fullScreenProperty().addListener((obs, wasFullscreen, isNowFullscreen) -> {
            Platform.runLater(() -> {
                Platform.runLater(() -> c.updateGameScale());
            });
        });
        
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> c.updateGameScale());
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> c.updateGameScale());
        });
    }

    /**
     * Main method to launch the JavaFX application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}