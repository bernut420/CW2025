package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface representing a Tetris brick (tetromino).
 * Each brick has multiple rotation states defined by shape matrices.
 */
public interface Brick {

    /**
     * Gets all rotation states of this brick as a list of shape matrices.
     * 
     * @return a list of 2D arrays, each representing a rotation state
     */
    List<int[][]> getShapeMatrix();
}
