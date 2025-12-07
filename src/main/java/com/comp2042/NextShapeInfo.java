package com.comp2042;

/**
 * Immutable data class containing information about the next rotated shape.
 * Stores both the shape matrix and its position in the rotation sequence.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo object.
     * 
     * @param shape the shape matrix for the next rotation
     * @param position the position index in the rotation sequence
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}
