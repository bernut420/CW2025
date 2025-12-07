package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ViewData.
 */
class ViewDataTest {

    @Test
    @DisplayName("Test ViewData creation and getters")
    void testViewDataCreation() {
        int[][] brickData = {{1, 1}, {1, 1}};
        int[][] nextBrickData = {{2, 2}, {2, 2}};
        int[][] holdBrickData = {{3, 3}, {3, 3}};
        
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData, holdBrickData, 5, 12);
        
        assertArrayEquals(brickData, viewData.getBrickData());
        assertEquals(5, viewData.getxPosition());
        assertEquals(10, viewData.getyPosition());
        assertArrayEquals(nextBrickData, viewData.getNextBrickData());
        assertArrayEquals(holdBrickData, viewData.getHoldBrickData());
        assertEquals(5, viewData.getGhostXPosition());
        assertEquals(12, viewData.getGhostYPosition());
    }

    @Test
    @DisplayName("Test ViewData returns copies of arrays")
    void testViewDataReturnsCopies() {
        int[][] brickData = {{1, 1}, {1, 1}};
        int[][] nextBrickData = {{2, 2}, {2, 2}};
        int[][] holdBrickData = {{3, 3}, {3, 3}};
        
        ViewData viewData = new ViewData(brickData, 0, 0, nextBrickData, holdBrickData, 0, 0);
        
        // Modify returned arrays and verify original is unchanged
        int[][] returnedBrick = viewData.getBrickData();
        returnedBrick[0][0] = 99;
        assertEquals(1, brickData[0][0]); // Original should be unchanged
        
        int[][] returnedNext = viewData.getNextBrickData();
        returnedNext[0][0] = 99;
        assertEquals(2, nextBrickData[0][0]); // Original should be unchanged
        
        int[][] returnedHold = viewData.getHoldBrickData();
        returnedHold[0][0] = 99;
        assertEquals(3, holdBrickData[0][0]); // Original should be unchanged
    }

    @Test
    @DisplayName("Test ViewData with zero positions")
    void testViewDataWithZeroPositions() {
        int[][] empty = {{0, 0}, {0, 0}};
        
        ViewData viewData = new ViewData(empty, 0, 0, empty, empty, 0, 0);
        
        assertEquals(0, viewData.getxPosition());
        assertEquals(0, viewData.getyPosition());
        assertEquals(0, viewData.getGhostXPosition());
        assertEquals(0, viewData.getGhostYPosition());
    }

    @Test
    @DisplayName("Test ViewData with negative positions")
    void testViewDataWithNegativePositions() {
        int[][] empty = {{0, 0}, {0, 0}};
        
        ViewData viewData = new ViewData(empty, -2, -5, empty, empty, -1, -3);
        
        assertEquals(-2, viewData.getxPosition());
        assertEquals(-5, viewData.getyPosition());
        assertEquals(-1, viewData.getGhostXPosition());
        assertEquals(-3, viewData.getGhostYPosition());
    }
}
