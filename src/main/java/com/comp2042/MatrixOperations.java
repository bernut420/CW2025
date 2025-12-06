package com.comp2042;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {
    private MatrixOperations() {
    }

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

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
