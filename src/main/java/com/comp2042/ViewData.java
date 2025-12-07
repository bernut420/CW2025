package com.comp2042;

/**
 * Immutable data class containing all information needed to render the game view.
 * Includes current brick position, next brick preview, hold brick preview, and ghost piece position.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final int[][] holdBrickData;
    private final int ghostXPosition;
    private final int ghostYPosition;

    /**
     * Constructs a new ViewData object with all view information.
     * 
     * @param brickData the current brick's shape matrix
     * @param xPosition the current brick's X position on the board
     * @param yPosition the current brick's Y position on the board
     * @param nextBrickData the next brick's shape matrix for preview
     * @param holdBrickData the held brick's shape matrix for preview
     * @param ghostXPosition the ghost piece's X position
     * @param ghostYPosition the ghost piece's Y position
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int[][] holdBrickData, int ghostXPosition, int ghostYPosition) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.holdBrickData = holdBrickData;
        this.ghostXPosition = ghostXPosition;
        this.ghostYPosition = ghostYPosition;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    public int[][] getHoldBrickData() {
        return MatrixOperations.copy(holdBrickData);
    }

    public int getGhostXPosition() {
        return ghostXPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }
}
