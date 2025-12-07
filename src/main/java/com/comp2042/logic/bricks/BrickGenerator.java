package com.comp2042.logic.bricks;

/**
 * Interface for generating bricks in the game.
 * Provides methods to get the current brick and preview the next brick.
 */
public interface BrickGenerator {

    /**
     * Gets the current brick and advances to the next one.
     * 
     * @return the current brick
     */
    Brick getBrick();

    /**
     * Gets a preview of the next brick without consuming it.
     * 
     * @return the next brick that will be generated
     */
    Brick getNextBrick();
}
