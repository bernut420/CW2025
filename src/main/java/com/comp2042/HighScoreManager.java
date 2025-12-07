package com.comp2042;

import java.io.*;

/**
 * Manages the high score persistence.
 * Loads and saves the high score to a text file.
 */
public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "tetris_highscore.txt";
    private int highScore;
    
    public HighScoreManager() {
        loadHighScore();
    }
    
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                highScore = Integer.parseInt(line.trim());
            } else {
                highScore = 0;
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }
    
    private void saveHighScore() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.println(highScore);
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
    
    public int getHighScore() {
        return highScore;
    }
    
    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }
}

