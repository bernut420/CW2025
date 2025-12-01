package com.comp2042;

import java.io.*;
import java.util.Properties;


public class SettingsManager {
    private static final String SETTINGS_FILE = "tetris_settings.properties";
    private Properties properties;
    
    private boolean ghostPieceEnabled;
    private boolean musicEnabled;
    private double musicVolume;
    
    public SettingsManager() {
        properties = new Properties();
        loadSettings();
    }
    
    private void loadSettings() {
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
            ghostPieceEnabled = Boolean.parseBoolean(properties.getProperty("ghostPieceEnabled", "true"));
            musicEnabled = Boolean.parseBoolean(properties.getProperty("musicEnabled", "true"));
            musicVolume = Double.parseDouble(properties.getProperty("musicVolume", "0.7"));
        } catch (IOException e) {
            // Use defaults if file doesn't exist
            ghostPieceEnabled = true;
            musicEnabled = true;
            musicVolume = 0.7;
        }
    }
    
    private void saveSettings() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.setProperty("ghostPieceEnabled", String.valueOf(ghostPieceEnabled));
            properties.setProperty("musicEnabled", String.valueOf(musicEnabled));
            properties.setProperty("musicVolume", String.valueOf(musicVolume));
            properties.store(output, "Tetris Game Settings");
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
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
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume)); // Clamp between 0 and 1
        saveSettings();
    }
}

