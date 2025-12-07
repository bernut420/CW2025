package com.comp2042;

/**
 * Immutable result class indicating the outcome of a hold operation.
 * Contains success status and game over flag.
 */
public final class HoldResult {

    private final boolean success;
    private final boolean gameOver;

    /**
     * Constructs a new HoldResult.
     * 
     * @param success true if the hold operation was successful
     * @param gameOver true if the game ended as a result of this operation
     */
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

    /**
     * Creates a successful hold result.
     * 
     * @param gameOver true if the game ended after this hold
     * @return a HoldResult indicating success
     */
    public static HoldResult success(boolean gameOver) {
        return new HoldResult(true, gameOver);
    }

    /**
     * Creates a failed hold result.
     * 
     * @return a HoldResult indicating failure
     */
    public static HoldResult failure() {
        return new HoldResult(false, false);
    }
}

