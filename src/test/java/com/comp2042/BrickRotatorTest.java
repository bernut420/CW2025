package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for BrickRotator.
 */
class BrickRotatorTest {

    private BrickRotator brickRotator;
    private TestBrick testBrick;

    @BeforeEach
    void setUp() {
        brickRotator = new BrickRotator();
        testBrick = new TestBrick();
    }

    @Test
    @DisplayName("Test setting brick resets to first shape")
    void testSetBrickResetsToFirstShape() {
        brickRotator.setBrick(testBrick);
        int[][] currentShape = brickRotator.getCurrentShape();
        
        assertArrayEquals(testBrick.getShapeMatrix().get(0), currentShape);
    }

    @Test
    @DisplayName("Test getNextShape returns next rotation")
    void testGetNextShape() {
        brickRotator.setBrick(testBrick);
        
        NextShapeInfo nextShape = brickRotator.getNextShape();
        
        assertNotNull(nextShape);
        assertArrayEquals(testBrick.getShapeMatrix().get(1), nextShape.getShape());
        assertEquals(1, nextShape.getPosition());
    }

    @Test
    @DisplayName("Test getNextShape wraps around to first shape")
    void testGetNextShapeWrapsAround() {
        brickRotator.setBrick(testBrick);
        
        // Rotate to last shape
        brickRotator.setCurrentShape(3);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        
        assertEquals(0, nextShape.getPosition());
        assertArrayEquals(testBrick.getShapeMatrix().get(0), nextShape.getShape());
    }

    @Test
    @DisplayName("Test setCurrentShape changes rotation state")
    void testSetCurrentShape() {
        brickRotator.setBrick(testBrick);
        
        brickRotator.setCurrentShape(2);
        int[][] currentShape = brickRotator.getCurrentShape();
        
        assertArrayEquals(testBrick.getShapeMatrix().get(2), currentShape);
    }

    @Test
    @DisplayName("Test getCurrentShape returns correct shape")
    void testGetCurrentShape() {
        brickRotator.setBrick(testBrick);
        
        int[][] shape0 = brickRotator.getCurrentShape();
        assertArrayEquals(testBrick.getShapeMatrix().get(0), shape0);
        
        brickRotator.setCurrentShape(1);
        int[][] shape1 = brickRotator.getCurrentShape();
        assertArrayEquals(testBrick.getShapeMatrix().get(1), shape1);
    }

    @Test
    @DisplayName("Test getBrick returns set brick")
    void testGetBrick() {
        brickRotator.setBrick(testBrick);
        assertEquals(testBrick, brickRotator.getBrick());
    }

    @Test
    @DisplayName("Test rotation cycle through all shapes")
    void testRotationCycle() {
        brickRotator.setBrick(testBrick);
        
        for (int i = 0; i < 4; i++) {
            int[][] expected = testBrick.getShapeMatrix().get(i);
            int[][] actual = brickRotator.getCurrentShape();
            assertArrayEquals(expected, actual, "Shape " + i + " should match");
            
            NextShapeInfo next = brickRotator.getNextShape();
            brickRotator.setCurrentShape(next.getPosition());
        }
    }

    /**
     * Simple test brick implementation for testing purposes.
     */
    private static class TestBrick implements Brick {
        private final List<int[][]> shapes;

        public TestBrick() {
            shapes = new ArrayList<>();
            // Create 4 different rotation states
            shapes.add(new int[][]{{1, 0}, {1, 1}});
            shapes.add(new int[][]{{1, 1}, {0, 1}});
            shapes.add(new int[][]{{1, 1}, {1, 0}});
            shapes.add(new int[][]{{0, 1}, {1, 1}});
        }

        @Override
        public List<int[][]> getShapeMatrix() {
            return MatrixOperations.deepCopyList(shapes);
        }
    }
}
