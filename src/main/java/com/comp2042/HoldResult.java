package com.comp2042;

public final class HoldResult {

    private final boolean success;
    private final boolean gameOver;

    public HoldResult(boolean success, boolean gameOver) {
        this.success = success;
        this.gameOver = gameOver;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public static HoldResult success(boolean gameOver) {
        return new HoldResult(true, gameOver);
    }

    public static HoldResult failure() {
        return new HoldResult(false, false);
    }
}

