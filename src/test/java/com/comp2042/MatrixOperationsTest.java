package com.comp2042;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MatrixOperations utility methods.
 */
class MatrixOperationsTest {

    @Test
    @DisplayName("Test copying a matrix creates a deep copy")
    void testCopyCreatesDeepCopy() {
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        int[][] copy = MatrixOperations.copy(original);
        
        // Verify it's a different object
        assertNotSame(original, copy);
        
        // Verify values are the same
        assertArrayEquals(original, copy);
        
        // Verify modifying copy doesn't affect original
        copy[0][0] = 99;
        assertEquals(1, original[0][0]);
        assertEquals(99, copy[0][0]);
    }

    @Test
    @DisplayName("Test copying empty matrix")
    void testCopyEmptyMatrix() {
        int[][] original = {};
        int[][] copy = MatrixOperations.copy(original);
        assertArrayEquals(original, copy);
    }

    @Test
    @DisplayName("Test intersect detects collision when brick overlaps filled cells")
    void testIntersectDetectsCollision() {
        int[][] board = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Brick at (1, 1) should intersect with filled cells at (2, 1) and (2, 2)
        assertTrue(MatrixOperations.intersect(board, brick, 1, 1));
    }

    @Test
    @DisplayName("Test intersect returns false when no collision")
    void testIntersectNoCollision() {
        int[][] board = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        assertFalse(MatrixOperations.intersect(board, brick, 0, 0));
    }

    @Test
    @DisplayName("Test intersect detects out of bounds on right side")
    void testIntersectOutOfBoundsRight() {
        int[][] board = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Brick at (2, 0) extends beyond board width
        assertTrue(MatrixOperations.intersect(board, brick, 2, 0));
    }

    @Test
    @DisplayName("Test intersect detects out of bounds on bottom")
    void testIntersectOutOfBoundsBottom() {
        int[][] board = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Brick at (0, 2) extends beyond board height
        assertTrue(MatrixOperations.intersect(board, brick, 0, 2));
    }

    @Test
    @DisplayName("Test intersect detects out of bounds on left side")
    void testIntersectOutOfBoundsLeft() {
        int[][] board = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        // Brick at (-1, 0) is out of bounds
        assertTrue(MatrixOperations.intersect(board, brick, -1, 0));
    }

    @Test
    @DisplayName("Test merge places brick into board")
    void testMergePlacesBrick() {
        int[][] board = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        int[][] brick = {
            {1, 1},
            {1, 1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 1, 1);
        
        // Verify brick was placed
        assertEquals(1, result[1][1]);
        assertEquals(1, result[1][2]);
        assertEquals(1, result[2][1]);
        assertEquals(1, result[2][2]);
        
        // Verify original board wasn't modified
        assertEquals(0, board[1][1]);
    }

    @Test
    @DisplayName("Test merge doesn't modify original board")
    void testMergeDoesNotModifyOriginal() {
        int[][] board = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };
        
        int[][] brick = {
            {1}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 1, 1);
        
        // Original should remain unchanged
        assertEquals(0, board[1][1]);
        // Result should have the brick
        assertEquals(1, result[1][1]);
    }

    @Test
    @DisplayName("Test checkRemoving clears single full row")
    void testCheckRemovingSingleRow() {
        int[][] board = {
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {1, 1, 1, 1},  // Full row
            {0, 0, 0, 0}
        };
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(1, result.getLinesRemoved());
        assertEquals(50, result.getScoreBonus()); // 50 * 1 * 1
        assertNotNull(result.getClearedRowIndices());
        assertEquals(1, result.getClearedRowIndices().size());
        assertEquals(2, result.getClearedRowIndices().get(0));
        
        // Verify row was cleared
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(0, newMatrix[2][0]);
        assertEquals(0, newMatrix[2][1]);
        assertEquals(0, newMatrix[2][2]);
        assertEquals(0, newMatrix[2][3]);
    }

    @Test
    @DisplayName("Test checkRemoving clears multiple rows")
    void testCheckRemovingMultipleRows() {
        int[][] board = {
            {0, 0, 0, 0},
            {1, 1, 1, 1},  // Full row
            {1, 1, 1, 1},  // Full row
            {0, 0, 0, 0}
        };
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus()); // 50 * 2 * 2
        assertEquals(2, result.getClearedRowIndices().size());
    }

    @Test
    @DisplayName("Test checkRemoving with no full rows")
    void testCheckRemovingNoFullRows() {
        int[][] board = {
            {0, 0, 0, 0},
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 0, 0, 0}
        };
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
        assertTrue(result.getClearedRowIndices().isEmpty());
    }

    @Test
    @DisplayName("Test checkRemoving with tetris (4 rows)")
    void testCheckRemovingTetris() {
        int[][] board = {
            {1, 1, 1, 1},  // Full row
            {1, 1, 1, 1},  // Full row
            {1, 1, 1, 1},  // Full row
            {1, 1, 1, 1}   // Full row
        };
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(4, result.getLinesRemoved());
        assertEquals(800, result.getScoreBonus()); // 50 * 4 * 4
        assertEquals(4, result.getClearedRowIndices().size());
    }

    @Test
    @DisplayName("Test deepCopyList creates independent copies")
    void testDeepCopyList() {
        int[][] matrix1 = {{1, 2}, {3, 4}};
        int[][] matrix2 = {{5, 6}, {7, 8}};
        
        java.util.List<int[][]> original = new java.util.ArrayList<>();
        original.add(matrix1);
        original.add(matrix2);
        
        java.util.List<int[][]> copy = MatrixOperations.deepCopyList(original);
        
        assertEquals(original.size(), copy.size());
        
        // Modify copy and verify original is unchanged
        copy.get(0)[0][0] = 99;
        assertEquals(1, original.get(0)[0][0]);
        assertEquals(99, copy.get(0)[0][0]);
    }
}
