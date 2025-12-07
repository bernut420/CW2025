package com.comp2042;

import com.comp2042.logic.bricks.Brick;

/**
 * Manages brick rotation by tracking the current rotation state
 * and providing the next rotation shape.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets the next rotation shape for the current brick.
     * 
     * @return NextShapeInfo containing the next shape matrix and its position
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the current rotation shape of the brick.
     * 
     * @return the current shape matrix
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation state of the brick.
     * 
     * @param currentShape the index of the rotation state to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick to rotate and resets to the first rotation state.
     * 
     * @param brick the brick to set
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Gets the current brick being rotated.
     * 
     * @return the current brick
     */
    public Brick getBrick() {
        return brick;
    }
}
