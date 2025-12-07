package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing static methods for matrix operations used in the game.
 * Handles intersection detection, matrix copying, merging, and row clearing logic.
 */
public class MatrixOperations {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MatrixOperations() {
    }

    /**
     * Checks if a brick at the specified position would intersect with the board matrix.
     * 
     * @param matrix the game board matrix
     * @param brick the brick shape matrix to check
     * @param x the X coordinate where the brick would be placed
     * @param y the Y coordinate where the brick would be placed
     * @return true if there is an intersection or out-of-bounds condition, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        return !(targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length);
    }

    /**
     * Creates a deep copy of a 2D integer array.
     * 
     * @param original the original array to copy
     * @return a new 2D array with copied values
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the board matrix at the specified position.
     * 
     * @param filledFields the current board matrix
     * @param brick the brick shape matrix to merge
     * @param x the X coordinate where the brick should be placed
     * @param y the Y coordinate where the brick should be placed
     * @return a new matrix with the brick merged into it
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    private static final int BASE_SCORE_MULTIPLIER = 50;
    
    /**
     * Checks for and removes completed rows from the board matrix.
     * Calculates score bonus based on the number of lines cleared.
     * 
     * @param matrix the current board matrix
     * @return ClearRow containing information about cleared rows and the new matrix
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] newMatrix = new int[matrix.length][matrix[0].length];
        Deque<int[]> rowsToKeep = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        identifyClearedRows(matrix, rowsToKeep, clearedRows);
        rebuildMatrix(newMatrix, rowsToKeep);
        
        int scoreBonus = calculateScoreBonus(clearedRows.size());
        return new ClearRow(clearedRows.size(), newMatrix, scoreBonus, clearedRows);
    }
    
    private static void identifyClearedRows(int[][] matrix, Deque<int[]> rowsToKeep, List<Integer> clearedRows) {
        for (int i = 0; i < matrix.length; i++) {
            int[] row = new int[matrix[i].length];
            System.arraycopy(matrix[i], 0, row, 0, matrix[i].length);
            
            if (isRowFull(matrix[i])) {
                clearedRows.add(i);
            } else {
                rowsToKeep.add(row);
            }
        }
    }
    
    private static boolean isRowFull(int[] row) {
        for (int cell : row) {
            if (cell == 0) {
                return false;
            }
        }
        return true;
    }
    
    private static void rebuildMatrix(int[][] newMatrix, Deque<int[]> rowsToKeep) {
        for (int i = newMatrix.length - 1; i >= 0; i--) {
            int[] row = rowsToKeep.pollLast();
            if (row != null) {
                newMatrix[i] = row;
            } else {
                break;
            }
        }
    }
    
    private static int calculateScoreBonus(int linesCleared) {
        return BASE_SCORE_MULTIPLIER * linesCleared * linesCleared;
    }

    /**
     * Creates a deep copy of a list of 2D integer arrays.
     * 
     * @param list the original list to copy
     * @return a new list containing deep copies of all arrays
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
