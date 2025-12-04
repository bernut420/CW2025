package com.comp2042;

import java.io.*;
import java.util.Properties;


public class SettingsManager {
    private static final String SETTINGS_FILE_NAME = "tetris_settings.properties";
    private String settingsFilePath;
    private Properties properties;
    
    private boolean ghostPieceEnabled;
    private boolean musicEnabled;
    private double musicVolume;
    
    public SettingsManager() {
        properties = new Properties();
        String userHome = System.getProperty("user.home");
        settingsFilePath = new File(userHome, SETTINGS_FILE_NAME).getAbsolutePath();
        loadSettings();
    }
    
    private void loadSettings() {
        try (InputStream input = new FileInputStream(settingsFilePath)) {
            properties.load(input);
            ghostPieceEnabled = Boolean.parseBoolean(properties.getProperty("ghostPieceEnabled", "true"));
            musicEnabled = Boolean.parseBoolean(properties.getProperty("musicEnabled", "true"));
            musicVolume = Double.parseDouble(properties.getProperty("musicVolume", "0.7"));
        } catch (IOException e) {
            ghostPieceEnabled = true;
            musicEnabled = true;
            musicVolume = 0.7;
        }
    }
    
    private void saveSettings() {
        try {
            File settingsFile = new File(settingsFilePath);
            File parentDir = settingsFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (OutputStream output = new FileOutputStream(settingsFile)) {
                properties.setProperty("ghostPieceEnabled", String.valueOf(ghostPieceEnabled));
                properties.setProperty("musicEnabled", String.valueOf(musicEnabled));
                properties.setProperty("musicVolume", String.valueOf(musicVolume));
                properties.store(output, "Tetris Game Settings");
            }
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean isGhostPieceEnabled() {
        return ghostPieceEnabled;
    }
    
    public void setGhostPieceEnabled(boolean enabled) {
        this.ghostPieceEnabled = enabled;
        saveSettings();
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        saveSettings();
    }
    
    public double getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));
        saveSettings();
    }
}

