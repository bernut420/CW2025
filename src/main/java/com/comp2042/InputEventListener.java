package com.comp2042;

/**
 * Interface for handling input events from the user or game thread.
 * Defines methods for all possible game actions.
 */
public interface InputEventListener {

    /**
     * Handles a down move event (brick moving down).
     * 
     * @param event the move event containing type and source information
     * @return DownData containing clear row information and updated view data
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles a left move event (brick moving left).
     * 
     * @param event the move event containing type and source information
     * @return ViewData containing updated brick position information
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles a right move event (brick moving right).
     * 
     * @param event the move event containing type and source information
     * @return ViewData containing updated brick position information
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles a rotate event (brick rotating).
     * 
     * @param event the move event containing type and source information
     * @return ViewData containing updated brick rotation information
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles a hard drop event (brick dropping instantly to the bottom).
     * 
     * @param event the move event containing type and source information
     * @return DownData containing clear row information and updated view data
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Handles a hold event (holding/swapping the current brick).
     * 
     * @param event the move event containing type and source information
     * @return ViewData containing updated brick information after hold
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Creates a new game, resetting the board and game state.
     */
    void createNewGame();
    
    /**
     * Gets the current board matrix state.
     * 
     * @return a 2D array representing the current board state
     */
    int[][] getBoardMatrix();
}
