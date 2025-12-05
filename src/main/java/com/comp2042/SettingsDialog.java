package com.comp2042;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;


public class SettingsDialog extends Dialog<ButtonType> {
    private CheckBox ghostPieceCheckBox;
    private CheckBox musicCheckBox;
    private Slider volumeSlider;
    private Label volumeLabel;
    private CheckBox backgroundPictureCheckBox;
    
    private java.util.function.Consumer<Double> volumeChangeListener;
    private java.util.function.Consumer<Boolean> musicEnabledChangeListener;
    private java.util.function.Consumer<Boolean> ghostPieceChangeListener;
    private java.util.function.Consumer<Boolean> backgroundPictureChangeListener;
    
    public SettingsDialog() {
        setTitle("Settings");
        setHeaderText(null);
        
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
        
        getDialogPane().setStyle(
            "-fx-background-color: #1a1a1a; " +
            "-fx-border-color: linear-gradient(#5ba0f2, #4a90e2); " +
            "-fx-border-width: 2px;"
        );
        
        VBox mainContainer = new VBox(25);
        mainContainer.setPadding(new Insets(30, 40, 20, 40));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #1a1a1a;");
        
        Label titleLabel = new Label("SETTINGS");
        titleLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: linear-gradient(#7bb3ff, #4a90e2); " +
            "-fx-effect: dropshadow(gaussian, rgba(74, 144, 226, 0.6), 8, 0.6, 0, 0);"
        );
        titleLabel.setPadding(new Insets(0, 0, 10, 0));
        
        VBox settingsContainer = new VBox(0);
        settingsContainer.setAlignment(Pos.CENTER_LEFT);
        
        VBox ghostSection = createSettingSection("Ghost Piece", "Show the ghost piece preview");
        ghostPieceCheckBox = new CheckBox("Enable Ghost Piece");
        ghostPieceCheckBox.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;"
        );
        ghostSection.getChildren().add(ghostPieceCheckBox);
        
        ghostPieceCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (ghostPieceChangeListener != null && oldVal != null) {
                ghostPieceChangeListener.accept(newVal);
            }
        });
        
        VBox musicSection = createSettingSection("Music", "Enable or disable background music");
        musicCheckBox = new CheckBox("Enable Music");
        musicCheckBox.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;"
        );
        musicSection.getChildren().add(musicCheckBox);
        
        VBox volumeSection = createSettingSection("Volume", "Adjust music volume");
        HBox volumeControls = new HBox(15);
        volumeControls.setAlignment(Pos.CENTER_LEFT);
        
        volumeSlider = new Slider(0, 1, 0.7);
        volumeSlider.setPrefWidth(200);
        volumeSlider.setShowTickLabels(false);
        volumeSlider.setShowTickMarks(false);
        volumeSlider.setStyle(
            "-fx-control-inner-background: #2a2a2a; " +
            "-fx-background-color: #1a1a1a;"
        );
        
        volumeLabel = new Label("70%");
        volumeLabel.setStyle(
            "-fx-text-fill: #FFD700; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 50px;"
        );
        
        volumeControls.getChildren().addAll(volumeSlider, volumeLabel);
        volumeSection.getChildren().add(volumeControls);
        
        VBox backgroundSection = createSettingSection("Background Picture", "Show or hide the background picture in play area");
        backgroundPictureCheckBox = new CheckBox("Enable Background Picture");
        backgroundPictureCheckBox.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold;"
        );
        backgroundSection.getChildren().add(backgroundPictureCheckBox);
        
        backgroundPictureCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (backgroundPictureChangeListener != null && oldVal != null) {
                backgroundPictureChangeListener.accept(newVal);
            }
        });
        
        settingsContainer.getChildren().add(ghostSection);
        settingsContainer.getChildren().add(createSeparator());
        settingsContainer.getChildren().add(backgroundSection);
        settingsContainer.getChildren().add(createSeparator());
        settingsContainer.getChildren().add(musicSection);
        settingsContainer.getChildren().add(createSeparator());
        settingsContainer.getChildren().add(volumeSection);
        
        mainContainer.getChildren().addAll(titleLabel, settingsContainer);
        
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int percent = (int) (newVal.doubleValue() * 100);
            volumeLabel.setText(percent + "%");
            
            if (volumeChangeListener != null && oldVal != null) {
                volumeChangeListener.accept(newVal.doubleValue());
            }
        });
        
        musicCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            volumeSlider.setDisable(!newVal);
            if (!newVal) {
                volumeSlider.setStyle(
                    "-fx-control-inner-background: #1a1a1a; " +
                    "-fx-background-color: #1a1a1a; " +
                    "-fx-opacity: 0.5;"
                );
            } else {
                volumeSlider.setStyle(
                    "-fx-control-inner-background: #2a2a2a; " +
                    "-fx-background-color: #1a1a1a; " +
                    "-fx-opacity: 1.0;"
                );
            }
            
            if (musicEnabledChangeListener != null) {
                musicEnabledChangeListener.accept(newVal);
            }
        });
        
        getDialogPane().setContent(mainContainer);
        
        getDialogPane().lookupButton(okButtonType).setStyle(
            "-fx-background-color: linear-gradient(#4CAF50, #45a049); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-pref-width: 80px; " +
            "-fx-pref-height: 30px; " +
            "-fx-background-radius: 5px;"
        );
        
        getDialogPane().lookupButton(cancelButtonType).setStyle(
            "-fx-background-color: linear-gradient(#f44336, #da190b); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-pref-width: 80px; " +
            "-fx-pref-height: 30px; " +
            "-fx-background-radius: 5px;"
        );
        
        initModality(Modality.APPLICATION_MODAL);
    }
    
    private VBox createSettingSection(String title, String description) {
        VBox section = new VBox(8);
        section.setPadding(new Insets(10, 0, 10, 0));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #7bb3ff;"
        );
        
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 11px; " +
            "-fx-text-fill: #aaaaaa;"
        );
        
        section.getChildren().addAll(titleLabel, descLabel);
        return section;
    }
    
    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMinHeight(1);
        separator.setMaxHeight(1);
        separator.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent, rgba(74, 144, 226, 0.5), transparent); " +
            "-fx-padding: 15px 0 15px 0;"
        );
        return separator;
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
        double clampedVolume = Math.max(0.0, Math.min(1.0, volume));
        java.util.function.Consumer<Double> tempListener = volumeChangeListener;
        volumeChangeListener = null;
        volumeSlider.setValue(clampedVolume);
        int percent = (int) (clampedVolume * 100);
        volumeLabel.setText(percent + "%");
        volumeChangeListener = tempListener;
    }
    

    public void setVolumeChangeListener(java.util.function.Consumer<Double> listener) {
        this.volumeChangeListener = listener;
    }
    

    public void setMusicEnabledChangeListener(java.util.function.Consumer<Boolean> listener) {
        this.musicEnabledChangeListener = listener;
    }
    
    public void setGhostPieceChangeListener(java.util.function.Consumer<Boolean> listener) {
        this.ghostPieceChangeListener = listener;
    }
    
    public boolean isBackgroundPictureEnabled() {
        return backgroundPictureCheckBox.isSelected();
    }
    
    public void setBackgroundPictureEnabled(boolean enabled) {
        backgroundPictureCheckBox.setSelected(enabled);
    }
    
    public void setBackgroundPictureChangeListener(java.util.function.Consumer<Boolean> listener) {
        this.backgroundPictureChangeListener = listener;
    }
}

