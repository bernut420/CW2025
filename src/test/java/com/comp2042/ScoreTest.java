package com.comp2042;

import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Score.
 */
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("Test initial score is zero")
    void testInitialScoreIsZero() {
        assertEquals(0, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test adding positive points")
    void testAddPositivePoints() {
        score.add(10);
        assertEquals(10, score.scoreProperty().getValue());
        
        score.add(5);
        assertEquals(15, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test adding zero points")
    void testAddZeroPoints() {
        score.add(10);
        score.add(0);
        assertEquals(10, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test adding negative points")
    void testAddNegativePoints() {
        score.add(20);
        score.add(-5);
        assertEquals(15, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test reset sets score to zero")
    void testReset() {
        score.add(100);
        score.add(50);
        assertEquals(150, score.scoreProperty().getValue());
        
        score.reset();
        assertEquals(0, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test score property is observable")
    void testScorePropertyIsObservable() {
        IntegerProperty property = score.scoreProperty();
        assertNotNull(property);
        
        // Verify property value changes when score is updated
        score.add(25);
        assertEquals(25, property.getValue());
    }

    @Test
    @DisplayName("Test multiple additions accumulate")
    void testMultipleAdditionsAccumulate() {
        score.add(10);
        score.add(20);
        score.add(30);
        score.add(40);
        
        assertEquals(100, score.scoreProperty().getValue());
    }

    @Test
    @DisplayName("Test reset after multiple operations")
    void testResetAfterMultipleOperations() {
        score.add(50);
        score.add(25);
        score.add(75);
        score.reset();
        score.add(10);
        
        assertEquals(10, score.scoreProperty().getValue());
    }
}
