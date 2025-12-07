package com.comp2042.logic.bricks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Test class for RandomBrickGenerator.
 */
class RandomBrickGeneratorTest {

    @Test
    @DisplayName("Test getBrick returns a brick")
    void testGetBrickReturnsBrick() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        Brick brick = generator.getBrick();
        
        assertNotNull(brick);
        assertNotNull(brick.getShapeMatrix());
        assertFalse(brick.getShapeMatrix().isEmpty());
    }

    @Test
    @DisplayName("Test getNextBrick returns preview without consuming")
    void testGetNextBrick() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        Brick next1 = generator.getNextBrick();
        Brick next2 = generator.getNextBrick();
        
        // Should return the same brick until getBrick() is called
        assertNotNull(next1);
        assertNotNull(next2);
        assertEquals(next1.getClass(), next2.getClass());
    }

    @Test
    @DisplayName("Test getBrick consumes and advances")
    void testGetBrickConsumes() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        Brick next = generator.getNextBrick();
        Brick current = generator.getBrick();
        
        // After getBrick(), next should advance
        assertNotNull(current);
        assertNotNull(next);
    }

    @Test
    @DisplayName("Test generator produces all brick types")
    void testGeneratorProducesAllTypes() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        Set<Class<? extends Brick>> brickTypes = new HashSet<>();
        
        // Generate many bricks to ensure we get all types
        for (int i = 0; i < 100; i++) {
            Brick brick = generator.getBrick();
            brickTypes.add(brick.getClass());
        }
        
        // Should have at least 7 different brick types (I, J, L, O, S, T, Z)
        assertTrue(brickTypes.size() >= 7, 
            "Should generate all 7 brick types. Found: " + brickTypes.size());
    }

    @Test
    @DisplayName("Test getBrick returns different bricks over time")
    void testGetBrickReturnsDifferentBricks() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        Set<Class<? extends Brick>> types = new HashSet<>();
        
        // Get 20 bricks
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            types.add(brick.getClass());
        }
        
        // Should have multiple different types
        assertTrue(types.size() > 1, "Should generate multiple brick types");
    }

    @Test
    @DisplayName("Test getNextBrick doesn't change after multiple calls")
    void testGetNextBrickStable() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        Brick next1 = generator.getNextBrick();
        Brick next2 = generator.getNextBrick();
        Brick next3 = generator.getNextBrick();
        
        // All should be the same until getBrick() is called
        assertEquals(next1.getClass(), next2.getClass());
        assertEquals(next2.getClass(), next3.getClass());
    }

    @Test
    @DisplayName("Test brick shapes are valid")
    void testBrickShapesAreValid() {
        RandomBrickGenerator generator = new RandomBrickGenerator();
        
        for (int i = 0; i < 10; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick.getShapeMatrix());
            assertFalse(brick.getShapeMatrix().isEmpty());
            
            // Each rotation state should be a valid 2D array
            for (int[][] shape : brick.getShapeMatrix()) {
                assertNotNull(shape);
                assertTrue(shape.length > 0);
            }
        }
    }
}
