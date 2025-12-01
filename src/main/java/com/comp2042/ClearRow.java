package com.comp2042;

import java.util.List;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;
    private final List<Integer> clearedRowIndices;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this(linesRemoved, newMatrix, scoreBonus, null);
    }

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
