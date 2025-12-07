package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HoldResult.
 */
class HoldResultTest {

    @Test
    @DisplayName("Test success factory method")
    void testSuccess() {
        HoldResult result = HoldResult.success(false);
        
        assertTrue(result.isSuccess());
        assertFalse(result.isGameOver());
    }

    @Test
    @DisplayName("Test success with game over")
    void testSuccessWithGameOver() {
        HoldResult result = HoldResult.success(true);
        
        assertTrue(result.isSuccess());
        assertTrue(result.isGameOver());
    }

    @Test
    @DisplayName("Test failure factory method")
    void testFailure() {
        HoldResult result = HoldResult.failure();
        
        assertFalse(result.isSuccess());
        assertFalse(result.isGameOver());
    }

    @Test
    @DisplayName("Test constructor with parameters")
    void testConstructor() {
        HoldResult result1 = new HoldResult(true, false);
        assertTrue(result1.isSuccess());
        assertFalse(result1.isGameOver());
        
        HoldResult result2 = new HoldResult(false, true);
        assertFalse(result2.isSuccess());
        assertTrue(result2.isGameOver());
    }

    @Test
    @DisplayName("Test all combinations of success and gameOver")
    void testAllCombinations() {
        // Success, no game over
        HoldResult r1 = new HoldResult(true, false);
        assertTrue(r1.isSuccess());
        assertFalse(r1.isGameOver());
        
        // Success, game over
        HoldResult r2 = new HoldResult(true, true);
        assertTrue(r2.isSuccess());
        assertTrue(r2.isGameOver());
        
        // Failure, no game over
        HoldResult r3 = new HoldResult(false, false);
        assertFalse(r3.isSuccess());
        assertFalse(r3.isGameOver());
        
        // Failure, game over (edge case)
        HoldResult r4 = new HoldResult(false, true);
        assertFalse(r4.isSuccess());
        assertTrue(r4.isGameOver());
    }
}
