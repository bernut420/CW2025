package com.comp2042;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setLayoutX(0);
        setLayoutY(0);
        setPrefWidth(220);
        setPrefHeight(60);
        setMinWidth(220);
        setMinHeight(60);
        setMaxWidth(220);
        setMaxHeight(60);
        
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Glow glow = new Glow(0.8);
        score.setEffect(glow);
        score.setTextFill(Color.YELLOW);
        score.setStyle("-fx-alignment: center;");
        setCenter(score);
        
        setOpacity(1.0);
    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), this);
        
        tt.setFromY(0);
        tt.setToY(-60);
        
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(event -> {
            if (list.contains(NotificationPanel.this)) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
