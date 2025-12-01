package com.comp2042;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;


public class SettingsDialog extends Dialog<ButtonType> {
    private CheckBox ghostPieceCheckBox;
    private CheckBox musicCheckBox;
    private Slider volumeSlider;
    private Label volumeLabel;
    
    public SettingsDialog() {
        setTitle("Settings");
        setHeaderText("Game Settings");
        
        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        
        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        
        // Ghost piece setting
        ghostPieceCheckBox = new CheckBox("Show Ghost Piece");
        grid.add(new Label("Ghost Piece:"), 0, 0);
        grid.add(ghostPieceCheckBox, 1, 0);
        
        // Music setting
        musicCheckBox = new CheckBox("Enable Music");
        grid.add(new Label("Music:"), 0, 1);
        grid.add(musicCheckBox, 1, 1);
        
        // Volume setting
        volumeSlider = new Slider(0, 1, 0.7);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setBlockIncrement(0.1);
        volumeLabel = new Label("70%");
        
        grid.add(new Label("Volume:"), 0, 2);
        grid.add(volumeSlider, 1, 2);
        grid.add(volumeLabel, 2, 2);
        
        // Update volume label when slider changes
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int percent = (int) (newVal.doubleValue() * 100);
            volumeLabel.setText(percent + "%");
        });
        
        // Enable/disable volume slider based on music checkbox
        musicCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            volumeSlider.setDisable(!newVal);
        });
        
        getDialogPane().setContent(grid);
        
        // Set modality
        initModality(Modality.APPLICATION_MODAL);
    }
    
    public boolean isGhostPieceEnabled() {
        return ghostPieceCheckBox.isSelected();
    }
    
    public void setGhostPieceEnabled(boolean enabled) {
        ghostPieceCheckBox.setSelected(enabled);
    }
    
    public boolean isMusicEnabled() {
        return musicCheckBox.isSelected();
    }
    
    public void setMusicEnabled(boolean enabled) {
        musicCheckBox.setSelected(enabled);
        volumeSlider.setDisable(!enabled);
    }
    
    public double getMusicVolume() {
        return volumeSlider.getValue();
    }
    
    public void setMusicVolume(double volume) {
        volumeSlider.setValue(Math.max(0.0, Math.min(1.0, volume)));
        int percent = (int) (volume * 100);
        volumeLabel.setText(percent + "%");
    }
}

