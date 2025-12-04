package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameOverPanel extends BorderPane {

    private Label gameOverLabel;
    private Button playAgainButton;
    private VBox contentBox;

    public GameOverPanel() {
        contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        contentBox.setPadding(new Insets(20, 20, 20, 0));
        
        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        playAgainButton = new Button("Play Again");
        playAgainButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-pref-width: 150; -fx-pref-height: 40; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        VBox.setMargin(playAgainButton, new Insets(0, 0, 0, 30));
        
        contentBox.getChildren().addAll(gameOverLabel, playAgainButton);
        setCenter(contentBox);
    }

    public Button getPlayAgainButton() {
        return playAgainButton;
    }
}
