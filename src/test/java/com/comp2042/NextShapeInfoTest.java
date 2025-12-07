package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for NextShapeInfo.
 */
class NextShapeInfoTest {

    @Test
    @DisplayName("Test NextShapeInfo creation and getters")
    void testNextShapeInfoCreation() {
        int[][] shape = {{1, 1}, {1, 1}};
        NextShapeInfo info = new NextShapeInfo(shape, 2);
        
        assertArrayEquals(shape, info.getShape());
        assertEquals(2, info.getPosition());
    }

    @Test
    @DisplayName("Test NextShapeInfo returns copy of shape")
    void testNextShapeInfoReturnsCopy() {
        int[][] original = {{1, 2}, {3, 4}};
        NextShapeInfo info = new NextShapeInfo(original, 0);
        
        int[][] returned = info.getShape();
        
        // Modify returned shape and verify original is unchanged
        returned[0][0] = 99;
        assertEquals(1, original[0][0]);
        assertEquals(99, returned[0][0]);
    }

    @Test
    @DisplayName("Test NextShapeInfo with position zero")
    void testNextShapeInfoPositionZero() {
        int[][] shape = {{1, 1}, {1, 1}};
        NextShapeInfo info = new NextShapeInfo(shape, 0);
        
        assertEquals(0, info.getPosition());
    }

    @Test
    @DisplayName("Test NextShapeInfo with maximum position")
    void testNextShapeInfoMaxPosition() {
        int[][] shape = {{1, 1}, {1, 1}};
        NextShapeInfo info = new NextShapeInfo(shape, 3);
        
        assertEquals(3, info.getPosition());
    }
}
