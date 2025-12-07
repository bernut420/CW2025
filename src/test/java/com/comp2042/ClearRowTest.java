package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for ClearRow.
 */
class ClearRowTest {

    @Test
    @DisplayName("Test ClearRow creation without cleared row indices")
    void testClearRowWithoutIndices() {
        int[][] matrix = {{0, 0}, {0, 0}};
        
        ClearRow clearRow = new ClearRow(2, matrix, 200);
        
        assertEquals(2, clearRow.getLinesRemoved());
        assertEquals(200, clearRow.getScoreBonus());
        assertArrayEquals(matrix, clearRow.getNewMatrix());
        assertNull(clearRow.getClearedRowIndices());
    }

    @Test
    @DisplayName("Test ClearRow creation with cleared row indices")
    void testClearRowWithIndices() {
        int[][] matrix = {{0, 0}, {0, 0}};
        List<Integer> indices = List.of(1, 3);
        
        ClearRow clearRow = new ClearRow(2, matrix, 200, indices);
        
        assertEquals(2, clearRow.getLinesRemoved());
        assertEquals(200, clearRow.getScoreBonus());
        assertArrayEquals(matrix, clearRow.getNewMatrix());
        assertNotNull(clearRow.getClearedRowIndices());
        assertEquals(2, clearRow.getClearedRowIndices().size());
        assertEquals(1, clearRow.getClearedRowIndices().get(0));
        assertEquals(3, clearRow.getClearedRowIndices().get(1));
    }

    @Test
    @DisplayName("Test ClearRow returns copy of matrix")
    void testClearRowReturnsCopy() {
        int[][] original = {{1, 2}, {3, 4}};
        
        ClearRow clearRow = new ClearRow(0, original, 0);
        int[][] returned = clearRow.getNewMatrix();
        
        // Modify returned matrix and verify original is unchanged
        returned[0][0] = 99;
        assertEquals(1, original[0][0]);
        assertEquals(99, returned[0][0]);
    }

    @Test
    @DisplayName("Test ClearRow with zero lines removed")
    void testClearRowZeroLines() {
        int[][] matrix = {{0, 0}, {0, 0}};
        
        ClearRow clearRow = new ClearRow(0, matrix, 0);
        
        assertEquals(0, clearRow.getLinesRemoved());
        assertEquals(0, clearRow.getScoreBonus());
    }

    @Test
    @DisplayName("Test ClearRow with single line removed")
    void testClearRowSingleLine() {
        int[][] matrix = {{0, 0}, {0, 0}};
        List<Integer> indices = List.of(2);
        
        ClearRow clearRow = new ClearRow(1, matrix, 50, indices);
        
        assertEquals(1, clearRow.getLinesRemoved());
        assertEquals(50, clearRow.getScoreBonus());
        assertEquals(1, clearRow.getClearedRowIndices().size());
        assertEquals(2, clearRow.getClearedRowIndices().get(0));
    }
}
