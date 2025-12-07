package com.comp2042;

import java.util.List;

/**
 * Immutable data class containing information about cleared rows.
 * Stores the number of lines removed, the updated board matrix, score bonus,
 * and indices of cleared rows for animation purposes.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final List<Integer> clearedRowIndices;

    /**
     * Constructs a ClearRow without cleared row indices.
     * 
     * @param linesRemoved the number of lines that were cleared
     * @param newMatrix the updated board matrix after clearing rows
     * @param scoreBonus the score bonus for clearing these rows
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, null);
    }

    /**
     * Constructs a ClearRow with all information including cleared row indices.
     * 
     * @param linesRemoved the number of lines that were cleared
     * @param newMatrix the updated board matrix after clearing rows
     * @param scoreBonus the score bonus for clearing these rows
     * @param clearedRowIndices the list of row indices that were cleared (for animation)
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus, List<Integer> clearedRowIndices) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
        this.clearedRowIndices = clearedRowIndices;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public List<Integer> getClearedRowIndices() {
        return clearedRowIndices;
    }
}
