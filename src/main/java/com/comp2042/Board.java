package com.comp2042;

/**
 * Interface representing the game board for Tetris.
 * Defines operations for moving, rotating, and managing bricks on the board.
 */
public interface Board {

    /**
     * Moves the current brick down by one position.
     * 
     * @return true if the move was successful, false if the brick cannot move down
     */
    boolean moveBrickDown();

    /**
     * Moves the current brick left by one position.
     * 
     * @return true if the move was successful, false if the brick cannot move left
     */
    boolean moveBrickLeft();

    /**
     * Moves the current brick right by one position.
     * 
     * @return true if the move was successful, false if the brick cannot move right
     */
    boolean moveBrickRight();

    /**
     * Rotates the current brick counterclockwise.
     * 
     * @return true if the rotation was successful, false if the brick cannot rotate
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new brick at the top of the board.
     * 
     * @return true if the game is over (no space for new brick), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return a 2D array representing the board state
     */
    int[][] getBoardMatrix();

    /**
     * Gets the view data for rendering the current game state.
     * 
     * @return ViewData containing brick positions and preview data
     */
    ViewData getViewData();

    /**
     * Merges the current brick into the background board matrix.
     */
    void mergeBrickToBackground();

    /**
     * Clears completed rows from the board.
     * 
     * @return ClearRow containing information about cleared rows and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the score object for the current game.
     * 
     * @return the Score object
     */
    Score getScore();

    /**
     * Resets the board for a new game.
     */
    void newGame();

    /**
     * Holds the current brick and swaps it with the previously held brick.
     * 
     * @return HoldResult indicating success or failure of the hold operation
     */
    HoldResult holdCurrentBrick();

}
