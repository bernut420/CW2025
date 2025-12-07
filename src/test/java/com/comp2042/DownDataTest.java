package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DownData.
 */
class DownDataTest {

    @Test
    @DisplayName("Test DownData creation and getters")
    void testDownDataCreation() {
        int[][] matrix = {{0, 0}, {0, 0}};
        ClearRow clearRow = new ClearRow(1, matrix, 50);
        int[][] brickData = {{1, 1}, {1, 1}};
        ViewData viewData = new ViewData(brickData, 5, 10, brickData, brickData, 5, 12);
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertEquals(clearRow, downData.getClearRow());
        assertEquals(viewData, downData.getViewData());
    }

    @Test
    @DisplayName("Test DownData with no cleared rows")
    void testDownDataNoClearedRows() {
        int[][] matrix = {{0, 0}, {0, 0}};
        ClearRow clearRow = new ClearRow(0, matrix, 0);
        int[][] brickData = {{1, 1}, {1, 1}};
        ViewData viewData = new ViewData(brickData, 0, 0, brickData, brickData, 0, 0);
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertEquals(0, downData.getClearRow().getLinesRemoved());
        assertNotNull(downData.getViewData());
    }

    @Test
    @DisplayName("Test DownData with multiple cleared rows")
    void testDownDataMultipleClearedRows() {
        int[][] matrix = {{0, 0}, {0, 0}};
        ClearRow clearRow = new ClearRow(3, matrix, 450);
        int[][] brickData = {{1, 1}, {1, 1}};
        ViewData viewData = new ViewData(brickData, 5, 10, brickData, brickData, 5, 12);
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertEquals(3, downData.getClearRow().getLinesRemoved());
        assertEquals(450, downData.getClearRow().getScoreBonus());
    }
}
